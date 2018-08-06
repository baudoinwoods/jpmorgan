package com.woobadeau.jpmorgan.transaction;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.woobadeau.jpmorgan.transaction.TransactionDirection.B;
import static com.woobadeau.jpmorgan.transaction.TransactionDirection.S;

/**
 * Holder for some Instruction objects for testing purposes.
 */
public class PresetInstructions {
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    public static final Instruction INSTRUCTION_1 = getInstruction("foo", B, new BigDecimal("0.50"), "SGP", "20160101", "20160102", 200, new BigDecimal(100.25));
    public static final Instruction INSTRUCTION_2 = getInstruction("foo", B, new BigDecimal("1.02"), "EUR", "20160101", "20160102", 200, new BigDecimal(100.25));
    public static final Instruction INSTRUCTION_3 = getInstruction("foo", B, new BigDecimal("1.3"), "GBP", "20160101", "20160102", 200, new BigDecimal(100.25));
    public static final Instruction INSTRUCTION_4 = getInstruction("foo", S, new BigDecimal("0.75"), "AED", "20160101", "20160102", 200, new BigDecimal(100.25));
    public static final Instruction INSTRUCTION_5 = getInstruction("bar", B, new BigDecimal("1.3"), "GBP", "20160101", "20160106", 200, new BigDecimal(100.25));
    public static final Instruction INSTRUCTION_6 = getInstruction("bar", B, new BigDecimal("1.02"), "GBP", "20160101", "20160102", 200, new BigDecimal(100.25));

    public static final double INSTRUCTION_1_VALUE = 10025d;
    public static final double INSTRUCTION_2_VALUE = 20451d;
    public static final double INSTRUCTION_3_VALUE = 26065d;
    public static final double INSTRUCTION_4_VALUE = 15037.5d;


    public static Instruction getInstruction(String entityName, TransactionDirection transactionDirection, BigDecimal forex, String currency, String instructionDateString, String settlementDateString, int units, BigDecimal pricePerUnit) {
        Instruction instruction = new Instruction();
        instruction.setEntity(entityName);
        instruction.setTransactionDirection(transactionDirection);
        instruction.setForex(forex);
        instruction.setCurrency(currency);
        try {
            instruction.setInstructionDate(formatter.parse(instructionDateString));
            instruction.setSettlementDate(formatter.parse(settlementDateString));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        instruction.setUnits(units);
        instruction.setPricePerUnit(pricePerUnit);
        return instruction;
    }
}
