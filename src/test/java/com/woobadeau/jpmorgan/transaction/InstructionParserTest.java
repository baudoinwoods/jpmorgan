package com.woobadeau.jpmorgan.transaction;

import com.woobadeau.jpmorgan.exception.InstructionParsingException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class InstructionParserTest {

    /**
     * Test parsing of lines with different separators and date formats
     */
    @Test
    public void parseInstructionLineTest() throws InstructionParsingException {
        Instruction parsedInstruction = InstructionParser.parseLine("foo B 0.50 SGP 01 Jan 2016 02 Jan 2016 200 100.25");
        assertInstructionsEquals(PresetInstructions.INSTRUCTION_1, parsedInstruction);
        parsedInstruction = InstructionParser.parseLine("foo,B,0.50,SGP,01 Jan 2016,02 Jan 2016,200,100.25", ",");
        assertInstructionsEquals(PresetInstructions.INSTRUCTION_1, parsedInstruction);
        parsedInstruction = InstructionParser.parseLine("foo,B,0.50,SGP,20160101,20160102,200,100.25", ",", "yyyyMMdd");
        assertInstructionsEquals(PresetInstructions.INSTRUCTION_1, parsedInstruction);
    }

    /**
     * Test com.woobadeau.jpmorgan.exception.InstructionParsingException thrown when dates are in the wrong format.
     */
    @Test
    public void parsingBadDateFormat() {
        assertThrows(InstructionParsingException.class, () -> InstructionParser.parseLine("foo,B,0.50,SGP,20160101,20160102,200,100.25", ","));
    }

    /**
     * Test com.woobadeau.jpmorgan.exception.InstructionParsingException thrown when line does not have the correct number of fields.
     */
    @Test
    public void wrongNumberOfFields() {
        assertThrows(InstructionParsingException.class, () -> InstructionParser.parseLine("foo B 0.50 SGP 01 Jan 2016 02 Jan 2016 200"));
        assertThrows(InstructionParsingException.class, () -> InstructionParser.parseLine("foo B 0.50 SGP 01 Jan 2016 02 2016 200 100.25 2"));
    }

    private void assertInstructionsEquals(Instruction expected, Instruction actual) {
        assertEquals(expected.getEntity(),actual.getEntity());
        assertEquals(expected.getTransactionDirection(),actual.getTransactionDirection());
        assertEquals(expected.getForex(),actual.getForex());
        assertEquals(expected.getCurrency(),actual.getCurrency());
        assertEquals(expected.getInstructionDate(),actual.getInstructionDate());
        assertEquals(expected.getSettlementDate(),actual.getSettlementDate());
        assertEquals(expected.getUnits(),actual.getUnits());
        assertEquals(expected.getPricePerUnit(),actual.getPricePerUnit());
    }

}
