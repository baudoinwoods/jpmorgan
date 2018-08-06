package com.woobadeau.jpmorgan.transaction;

import com.woobadeau.jpmorgan.exception.InstructionParsingException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class InstructionParser {
    private static final String DEFAULT_DATE_FORMAT = "dd MMM yyyy";
    private static final String DEFAULT_SEPARATOR = " ";

    /**
     * Parse a line representing an instruction.
     * The instruction should contain exactly 8 fields representing:
     *  - Entity
     *  - Buy/Sell flag
     *  - Agreed FX
     *  - Currency
     *  - Instruction Date
     *  - Settlement Date requested
     *  - Units
     *  - Price per Unit
     * @param line the formated line to be parsed
     * @param separator the field separator
     * @param dateFormat the date format
     * @return The com.woobadeau.jpmorgan.transaction.Instruction object represented by this line
     * @throws InstructionParsingException if the line cannot be parsed (Wrong number of fields, incorrect data...).
     */
    public static Instruction parseLine(String line, String separator, String dateFormat) throws InstructionParsingException {
        if (separator == null) {
            separator = DEFAULT_SEPARATOR;
        }
        if (dateFormat == null) {
            dateFormat = DEFAULT_DATE_FORMAT;
        }
        Instruction instruction = new Instruction();
        String[] fields = line.split(separator);
        int dateLength = dateFormat.split(separator).length;
        int countedFields = fields.length - ((dateLength - 1) * 2);
        if (fields.length - ((dateLength - 1) * 2) != 8) {
            throw new InstructionParsingException("Line {"+line+"} does not contain the correct number of fields.\n found "+ countedFields +" and expecting 8" );
        }
        int offset = 0;
        try {
            instruction.setEntity(fields[offset++]);
            instruction.setTransactionDirection(TransactionDirection.valueOf(fields[offset++]));
            instruction.setForex(new BigDecimal(fields[offset++]));
            instruction.setCurrency(fields[offset++]);
            instruction.setInstructionDate(parseDate(dateFormat, fields, dateLength, offset, separator));
            offset += dateLength;
            instruction.setSettlementDate(parseDate(dateFormat, fields, dateLength, offset, separator));
            offset += dateLength;
            instruction.setUnits(Integer.parseInt(fields[offset++]));
            instruction.setPricePerUnit(new BigDecimal(fields[offset++]));
        } catch (Exception e) {
            throw new InstructionParsingException("Error parsing line {"+line+"} for field "+offset, e);
        }

        return instruction;
    }

    /**
     * Parse a date in the requested format with potentially a separator that should be used to join fields.
     * @param dateFormat format the date should be parsed
     * @param fields the fields in which the date is stored
     * @param dateLength the number of fields the date consists of
     * @param offset the first field of the date in the fields parameter
     * @param separator the separator that should be used to join the date
     * @return the date parsed
     * @throws ParseException if the date cannot be parsed with the given dateFormat
     */
    private static Date parseDate(String dateFormat, String[] fields, int dateLength, int offset, String separator) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

        String instructionDate = String.join(separator, Arrays.copyOfRange(fields, offset, offset+dateLength));
        return simpleDateFormat.parse(instructionDate);
    }

    /**
     * Parse a line representing an instruction.
     * The instruction should contain exactly 8 fields representing:
     *  - Entity
     *  - Buy/Sell flag
     *  - Agreed FX
     *  - Currency
     *  - Instruction Date
     *  - Settlement Date requested
     *  - Units
     *  - Price per Unit
     *  The date format is assumed to be dd MMM yyyy
     * @param line the formated line to be parsed
     * @param separator the field separator
     * @return The com.woobadeau.jpmorgan.transaction.Instruction object represented by this line
     * @throws InstructionParsingException if the line cannot be parsed (Wrong number of fields, incorrect data...).
     */
    public static Instruction parseLine(String line, String separator) throws InstructionParsingException {
        return parseLine(line, separator, null);
    }

    /**
     * Parse a line representing an instruction.
     * The instruction should contain exactly 8 fields representing:
     *  - Entity
     *  - Buy/Sell flag
     *  - Agreed FX
     *  - Currency
     *  - Instruction Date
     *  - Settlement Date requested
     *  - Units
     *  - Price per Unit
     *  The dateFormat is assumed to be dd MMM yyyy
     *  The separator is assumed to be a space
     * @param line the formated line to be parsed
     * @return The com.woobadeau.jpmorgan.transaction.Instruction object represented by this line
     * @throws InstructionParsingException if the line cannot be parsed (Wrong number of fields, incorrect data...).
     */
    public static Instruction parseLine(String line) throws InstructionParsingException {
        return parseLine(line, null);
    }
}
