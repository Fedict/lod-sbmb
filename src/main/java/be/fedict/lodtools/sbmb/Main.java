/*
 * Copyright (c) 2017, Bart Hanssens <bart.hanssens@fedict.be>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package be.fedict.lodtools.sbmb;

import be.fedict.lodtools.sbmb.helper.LegalDoc;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.jsoup.nodes.Document;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.mapdb.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class
 * 
 * @author Bart.Hanssens
 */
public class Main {
	private final static Logger LOG = LoggerFactory.getLogger(Main.class);
	
	private final static PageParser PARSER = new PageParser();
	private final static Random RND = new Random();
	
	private final static Options OPTS = 
			new Options().addRequiredOption("s", "start", true, "Start year")
						.addOption("e", "end", true, "End year")
						.addOption("m", "month", true, "Month")
						.addRequiredOption("b", "base", true, "Base URL")
						.addRequiredOption("n", "nl", true, "Dutch doc type")
						.addRequiredOption("f", "fr", true, "French doc type")
						.addRequiredOption("t", "type", true, "Type")
						.addOption("c", "cache", true, "Cache file")
						.addOption("g", "get", false, "Get files from site")
						.addOption("o", "outdir", true, "Output directory")
						.addOption("w", "wait", true, "Wait between requests");
	
	private static DB CACHE;
	private static HTreeMap<String,String> MAP;

	/**
	 * Log message and exit with exit code
	 * 
	 * @param code system exit code
	 * @param msg message to be logged
	 */
	private static void exit(int code, String msg) {
		if (CACHE != null) {
			CACHE.commit();
			CACHE.close();
		}
		LOG.error(msg);
		System.exit(code);
	}
	
	/**
	 * Parse command line arguments
	 * 
	 * @param args arguments
	 * @return commandline object 
	 */
	private static CommandLine parseArgs(String[] args) {
		try {
			return new DefaultParser().parse(OPTS, args);
		} catch (ParseException ex) {
			LOG.error(ex.getMessage());
			HelpFormatter help = new HelpFormatter();
			help.printHelp("lod-sbmb.jar", OPTS);
			return null;
		}
	}
	
	/**
	 * Get base URL
	 * 
	 * @param base
	 * @return url as string 
	 */
	private static String getBase(String base) {
		if (! base.startsWith("http")) {
			base = "http://" + base;
		}
		LOG.info("Using base {}", base);
		return base;
	}
	
	/**
	 * Sleep for a random time (between T and T * 2)
	 * 
	 * @param wait minimum sleep (in seconds)
	 * @throws InterruptedException 
	 */
	private static void sleep(long wait) throws InterruptedException {
		Thread.sleep(wait + ((long) RND.nextFloat() * wait));
	}

	/**
	 * Get database cache file
	 * 
	 * @param file MapDB file
	 */
	private static  void getMap(String file) {
		CACHE = DBMaker.fileDB(file).closeOnJvmShutdown().transactionEnable().make();
		MAP = CACHE.hashMap("sbmb", Serializer.STRING, Serializer.STRING).createOrOpen();
	}
	
	/**
	 * Get pages 
	 * @param start start year
	 * @param end end year
	 * @param base base URL
	 * @param types types
	 * @param wait delay between requests in seconds
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private static void getPages(int start, int end, String base, Map<String,String> types, String wait) 
									throws InterruptedException, IOException {
		int w = Integer.valueOf(wait) * 1000;
		
		for(int year = start; year <= end; year++) {	
			LOG.info("Get page for year {}", year);
		
			for(Entry<String,String> type: types.entrySet()) {
				sleep(w);
				Document page = PARSER.get(base, type.getValue(), year, type.getKey());
				MAP.put(page.location(), page.body().html());
				CACHE.commit();
			}
		}
	}
	
	/**
	 * Write page to RDF and CSV file
	 * 
	 * @param start start year
	 * @param end end year
	 * @param base base URL
	 * @param type
	 * @param types map of types
	 * @param outdir output dir
	 * @throws IOException
	 */
	private static void writePages(int start, int end, String base, String type,
					Map<String,String> types, String outdir) throws IOException {
		LegalDocWriter rdf = new LegalDocWriterRDF();
		LegalDocWriter csv = new LegalDocWriterCSV();
				
		for (int year = start; year <= end; year++) {	
			for(Entry<String,String> e: types.entrySet()) {
				String lang = e.getKey();
				String doctype = e.getValue();
				
				String html = MAP.get(base + "/" + doctype + "/" + year);
				if (html == null || html.isEmpty()) {
					throw new IOException("Could not get " + doctype + "/" + year + " from cache");
				}
				List<LegalDoc> docs = PARSER.parse(html, lang);
				
				Path rdfOut = Paths.get(outdir, doctype + "-" + year + ".nt");
				LOG.info("Writing docs to file {}", rdfOut);
				rdf.write(docs, rdfOut, year, type, types);
				
				Path csvOut = Paths.get(outdir, doctype + "-" + year + ".csv");			
				LOG.info("Writing docs to file {}", csvOut);
				csv.write(docs, csvOut, year, type, types);
			}
		}
	}
				
	/**
	 * Main
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		CommandLine cli = parseArgs(args);
		if (cli == null) {
			exit(-1, "Couldn't parse command line");
		}
		
		int start = Integer.valueOf(cli.getOptionValue("s"));
		int end = cli.hasOption("e") ? Integer.valueOf(cli.getOptionValue("e")) 
									: start;

		if (start < 1800 || start > end) {
			exit(-2, "Invalid start year");
		}
	
		getMap(cli.getOptionValue("c", "cache"));	
		
		// Map language to (name of) types
		Map<String,String> types = new HashMap();
		types.put("nl", cli.getOptionValue("n"));
		types.put("fr", cli.getOptionValue("f"));
		
		try {
			String base = getBase(cli.getOptionValue("b"));
			
			if (cli.hasOption("g")) {
				getPages(start, end, base, types, cli.getOptionValue("w"));
			}
			writePages(start, end, base, cli.getOptionValue("t"), types, 
												cli.getOptionValue("o", "."));
		} catch (IOException ex) {
			exit(-4, ex.getMessage());
		} catch (InterruptedException ex) {
			exit(-5, "Interrupted");
		}
	}	
}
