/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.picdrop.security;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author nvonstein
 */
public class SecureStoreMain {

    static Options generateBasicOptions() {
        Options ops = new Options();
        ops.addOption(Option.builder().longOpt("keystore")
                .argName("path")
                .desc("Specifies the path of the secure store to be used. By default the local path is used.")
                .hasArg()
                .optionalArg(false)
                .required(false)
                .build());
        ops.addOption(Option.builder().longOpt("help")
                .desc("Prints this usage.")
                .required(false)
                .build());
        ops.addOption(Option.builder().longOpt("clear")
                .argName("alias")
                .desc("Clears the given value alias.")
                .numberOfArgs(1)
                .optionalArg(false)
                .required(false)
                .build());
        ops.addOption(Option.builder().longOpt("store")
                .numberOfArgs(2)
                .argName("alias> <value")
                .desc("Stores the given value at the given alias in the secure store.")
                .optionalArg(false)
                .required(false)
                .build());
        ops.addOption(Option.builder().longOpt("create")
                .desc("Creates a secure store at the path given by --keystore or by default the current working directory.")
                .hasArg(false)
                .required(false)
                .build());
        ops.addOption(Option.builder().longOpt("list")
                .desc("Lists current stored alias names.")
                .required(false)
                .hasArg(false)
                .build());

        return ops;
    }

    static public void main(String[] args) throws ParseException, IOException, FileNotFoundException, NoSuchAlgorithmException, CertificateException, KeyStoreException, KeyStoreException, InterruptedException {

        CommandLineParser cliP = new DefaultParser();

        Options ops = generateBasicOptions();
        CommandLine cli = cliP.parse(ops, args);
        HelpFormatter hlp = new HelpFormatter();

        SecureStore ss;
        String path = ".";

        try {
            if (cli.hasOption("help")) {
                hlp.printHelp("SecureStore", ops);
                System.exit(0);
            }

            if (cli.hasOption("keystore")) {
                path = cli.getOptionValue("keystore", ".");
            }

            if (cli.hasOption("create")) {
                ss = new SecureStore(path, false);
                ss.createKeyStore();
                System.exit(0);
            } else {
                ss = new SecureStore(path, true);
            }

            if (cli.hasOption("list")) {
                Enumeration<String> en = ss.listAlias();
                while (en.hasMoreElements()) {
                    System.out.println(en.nextElement());
                }
                System.exit(0);
            }

            if (cli.hasOption("store")) {
                ss.storeValue(cli.getOptionValues("store")[0], cli.getOptionValues("store")[1]);
                ss.writeStore();
                System.exit(0);
            }

            if (cli.hasOption("clear")) {
                ss.deleteValue(cli.getOptionValue("clear"));
                ss.writeStore();
                System.exit(0);
            }
        } finally {
            hlp.printHelp("SecureStore", ops);
            System.exit(0);
        }
    }
}
