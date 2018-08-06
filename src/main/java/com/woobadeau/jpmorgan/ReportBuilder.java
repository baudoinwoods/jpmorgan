package com.woobadeau.jpmorgan;

import com.woobadeau.jpmorgan.exception.InstructionParsingException;
import com.woobadeau.jpmorgan.exception.SettlementConfigurationException;
import com.woobadeau.jpmorgan.exception.SettlementException;
import com.woobadeau.jpmorgan.settlement.SettlementDateManager;
import com.woobadeau.jpmorgan.settlement.SettlementReport;
import com.woobadeau.jpmorgan.settlement.SettlementReportManager;
import com.woobadeau.jpmorgan.transaction.Instruction;
import com.woobadeau.jpmorgan.transaction.InstructionParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Main class for the report builder.
 * Parses the command line and runs the report on the file specified.
 *
 * Usage of ReportBuilder:
 * java -jar reportBuilder.jar -f settlementFileName [OPTIONS]
 * OPTIONS:
 * -c {propertiesFileName} : a currency property file with configuration for currency settlement dates
 * -df {format} : the date format used in the settlement file - default dd MMM yyyy (see https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html)
 * -s {separator} : the filed separator used in the settlement file - default " " (single space)
 */
public class ReportBuilder {

    private static String settlementFile = null;
    private static String separator = null;
    private static String dateFormat = null;

    public static void main(String[] args) throws SettlementConfigurationException, FileNotFoundException, InstructionParsingException, SettlementException {
        parseCommandLine(args);
        Scanner scanner = new Scanner(new File(settlementFile));
        Set<Instruction> instructions = new HashSet<>();
        while (scanner.hasNextLine()) {
            Instruction instruction = InstructionParser.parseLine(scanner.nextLine(), separator, dateFormat);
            instructions.add(instruction);
        }
        SettlementReport report = SettlementReportManager.createReport(instructions);
        System.out.println(report.toString());
    }

    private static void parseCommandLine(String[] args) throws SettlementConfigurationException, FileNotFoundException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-f":
                    if (i == args.length -1) {
                        printUsage();
                        System.exit(1);
                    }
                    settlementFile = args[++i];
                    break;
                case "-c":
                    if (i == args.length -1) {
                        printUsage();
                        System.exit(1);
                    }
                    SettlementDateManager.setup(new FileInputStream(new File(args[++i])));
                    break;
                case "-df":
                    if (i == args.length -1) {
                        printUsage();
                        System.exit(1);
                    }
                    dateFormat = args[++i];
                    break;
                case "-s":
                    if (i == args.length -1) {
                        printUsage();
                        System.exit(1);
                    }
                    separator = args[++i];
                    break;
                default:
                    printUsage();
                    System.exit(1);
            }
        }

        if (settlementFile == null) {
            printUsage();
            System.exit(1);
        }
    }

    private static void printUsage() {
        System.out.println("Usage of ReportBuilder:");
        System.out.println("java -jar reportBuilder.jar -f settlementFileName [OPTIONS]");
        System.out.println("OPTIONS:");
        System.out.println(" -c {propertiesFileName} : a currency property file with configuration for currency settlement dates");
        System.out.println(" -df {format} : the date format used in the settlement file - default dd MMM yyyy (see https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html)");
        System.out.println(" -s {separator} : the filed separator used in the settlement file - default \" \" (single space)");
    }
}
