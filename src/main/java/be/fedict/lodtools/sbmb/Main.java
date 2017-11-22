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

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Main class
 * 
 * @author Bart.Hanssens
 */
public class Main {
	private final static Logger LOG = LoggerFactory.getLogger(Main.class);
	
	private final static Random RND = new Random();
	
	private final static Options OPTS = 
			new Options().addRequiredOption("s", "start", true, "Start year")
						.addRequiredOption("e", "end", true, "End year")
						.addRequiredOption("b", "base", true, "Base URL")
						.addRequiredOption("n", "nl", true, "Dutch type")
						.addRequiredOption("f", "fr", true, "French type")
						.addOption("o", "outdir", true, "Output directory")
						.addOption("w", "wait", true, "Wait between requests");
	
	/**
	 * Log message and exit with exit code
	 * 
	 * @param code system exit code
	 * @param msg message to be logged
	 */
	private static void exit(int code, String msg) {
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
			HelpFormatter help = new HelpFormatter();
			help.printHelp("", "", OPTS, "");
			return null;
		}
	}

	private static void sleep(long wait) throws InterruptedException {
		Thread.sleep(wait + ((long) RND.nextFloat() * wait));
	}
	
	/**
	 * Main
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		CommandLine cli = parseArgs(args);
		if (cli == null) {
			exit(-1, "Couldn't pase command line");
		}
		
		int start = Integer.valueOf(cli.getOptionValue("s"));
		int end = Integer.valueOf(cli.getOptionValue("e"));
		if (start == 0 || end == 0 || end < start) {
			exit(-2, "Invalid start/end year combination");
		}
		
		String base = cli.getOptionValue("b");
		if (! base.startsWith("http")) {
			base = "http://" + base;
		}
		LOG.info("Using base {}", base);
		
		String o = cli.getOptionValue("o", ".");
		File outdir = Paths.get(o).toFile();
		if (! outdir.canWrite()) {
			exit(-3, "Can't write to output dir");
		}
		
		int wait = Integer.valueOf(cli.getOptionValue("w", "6")) * 1000;
		
		PageParser pp = new PageParser();
		List<LegalDoc> docs = new ArrayList();
		
		for (int year = start; year <= end; year++) {	
			LOG.info("Year {}", year);
			try {
				sleep(wait);
				docs.addAll(pp.parse(base, cli.getOptionValue("n"), year, "nl"));
				
				sleep(wait);
				docs.addAll(pp.parse(base, cli.getOptionValue("f"), year, "fr"));
				
				LegalDocWriter w = new LegalDocWriter();
				w.write(docs, outdir, year);
			} catch (IOException ex) {
				LOG.error(ex.getMessage());
			} catch (InterruptedException ex) {
				exit(-4, "Interrupted");
			}
		}
	}	
}
