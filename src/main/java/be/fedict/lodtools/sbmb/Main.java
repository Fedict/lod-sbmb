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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


/**
 * Main class
 * 
 * @author Bart.Hanssens
 */
public class Main {
	private final static Options OPTS = 
			new Options().addRequiredOption("s", "start", true, "Start year")
						.addRequiredOption("e", "end", true, "End year")
						.addRequiredOption("b", "base", true, "Base URL")
						.addRequiredOption("n", "nl", true, "Dutch type")
						.addRequiredOption("f", "fr", true, "French type")
						.addOption("o", "outdir", true, "Output directory");
	
	
	private static CommandLine parseArgs(String[] args) {
		try {
			return new DefaultParser().parse(OPTS, args);
		} catch (ParseException ex) {
			HelpFormatter help = new HelpFormatter();
			help.printHelp("", "", OPTS, "");
			return null;
		}
	}
	
	public static void main(String[] args) {
		CommandLine cli = parseArgs(args);
		if (cli == null) {
			System.exit(-1);
		}
		
		int start = Integer.valueOf(cli.getOptionValue("s"));
		int end = Integer.valueOf(cli.getOptionValue("e"));
		if (start == 0 || end == 0 || end < start) {
			System.exit(-2);
		}
		
		String base = cli.getOptionValue("b");
		
		String nl = base + cli.getOptionValue("n");
		String fr = base + cli.getOptionValue("f");

		String o = cli.getOptionValue("o", ".");

		PageParser pp = new PageParser();
		
		for (int year = start; year < end; year++) {
			pp.parse(nl, year);
			pp.parse(fr, year);
		}
		
		
//		"http://www.ejustice.just.fgov.be/eli/wet/",
//									"http://www.ejustice.just.fgov.be/eli/loi/");
		try {
			pp.parse(2017);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}	
}
