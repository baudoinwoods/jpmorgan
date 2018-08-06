package com.woobadeau.jpmorgan.settlement;

import com.woobadeau.jpmorgan.exception.SettlementConfigurationException;
import com.woobadeau.jpmorgan.exception.SettlementException;
import com.woobadeau.jpmorgan.transaction.Instruction;
import com.woobadeau.jpmorgan.transaction.PresetInstructions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class SettlementReportManagerTest {

    private final String FOO_ENTITY = "foo";
    private static final String BAR_ENTITY = "bar";
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    private final Date INSTRUCTION_DATE = formatter.parse("20160104");

    public SettlementReportManagerTest() throws ParseException {
    }

    @Test
    public void createReportFromInstructionTest() throws SettlementConfigurationException, SettlementException, ParseException {
        Set<Instruction> instructions = new HashSet<Instruction>() {{
            add(PresetInstructions.INSTRUCTION_1);
        }};
        SettlementReport report = SettlementReportManager.createReport(instructions);
        Map<Date, DailyReport> dailyReports = report.getDailyReports();

        assertEquals(1, dailyReports.size());
        DailyReport reportFor20160104 = dailyReports.get(INSTRUCTION_DATE);
        assertNotNull(reportFor20160104);

        assertEquals(1, reportFor20160104.getEntityReports().size());

        EntityReport foo = reportFor20160104.getEntityReports().get(FOO_ENTITY);
        assertNotNull(foo);
        assertEquals(0, foo.getIncomingValue().doubleValue());
        assertEquals(PresetInstructions.INSTRUCTION_1_VALUE, foo.getOutgoingValue().doubleValue());
    }

    @Test
    public void testSeveralInstructionsForSameEntityOnSameDay() throws SettlementConfigurationException, SettlementException {
        Set<Instruction> instructions = new HashSet<Instruction>() {{
            add(PresetInstructions.INSTRUCTION_1);
            add(PresetInstructions.INSTRUCTION_2);
            add(PresetInstructions.INSTRUCTION_3);
        }};

        SettlementReport report = SettlementReportManager.createReport(instructions);
        Map<Date, DailyReport> dailyReports = report.getDailyReports();

        assertEquals(1, dailyReports.size());
        DailyReport reportFor20160104 = dailyReports.get(INSTRUCTION_DATE);
        assertNotNull(reportFor20160104);

        assertEquals(1, reportFor20160104.getEntityReports().size());

        EntityReport foo = reportFor20160104.getEntityReports().get(FOO_ENTITY);
        assertNotNull(foo);
        assertEquals(0, foo.getIncomingValue().doubleValue());
        assertEquals(PresetInstructions.INSTRUCTION_1_VALUE +
                PresetInstructions.INSTRUCTION_2_VALUE +
                PresetInstructions.INSTRUCTION_3_VALUE,
                foo.getOutgoingValue().doubleValue());

    }

    @Test
    public void testSeveralEntitiesSeveralDays() throws SettlementConfigurationException, SettlementException, ParseException {
        Set<Instruction> instructions = new HashSet<Instruction>() {{
            add(PresetInstructions.INSTRUCTION_1);
            add(PresetInstructions.INSTRUCTION_2);
            add(PresetInstructions.INSTRUCTION_3);
            add(PresetInstructions.INSTRUCTION_4);
            add(PresetInstructions.INSTRUCTION_5);
            add(PresetInstructions.INSTRUCTION_6);
        }};

        SettlementReport report = SettlementReportManager.createReport(instructions);
        Map<Date, DailyReport> dailyReports = report.getDailyReports();

        assertEquals(3, dailyReports.size());
        DailyReport reportFor20160104 = dailyReports.get(INSTRUCTION_DATE);
        assertNotNull(reportFor20160104);

        assertEquals(2, reportFor20160104.getEntityReports().size());

        EntityReport bar = reportFor20160104.getEntityReports().get(BAR_ENTITY);
        assertNotNull(bar);
        assertEquals(0, bar.getIncomingValue().doubleValue());
        assertEquals(PresetInstructions.INSTRUCTION_2_VALUE, bar.getOutgoingValue().doubleValue());

        DailyReport reportFor20160103 = dailyReports.get(formatter.parse("20160103"));
        assertNotNull(reportFor20160103);

        assertEquals(1, reportFor20160103.getEntityReports().size());
        EntityReport foo = reportFor20160103.getEntityReports().get(FOO_ENTITY);
        assertNotNull(foo);
        assertEquals(PresetInstructions.INSTRUCTION_4_VALUE, foo.getIncomingValue().doubleValue());
        assertEquals(0, foo.getOutgoingValue().doubleValue());
    }

    @Test
    public void testAggregateIncomingOutgoing() throws SettlementConfigurationException, SettlementException {
        Set<Instruction> instructions = new HashSet<Instruction>() {{
            add(PresetInstructions.INSTRUCTION_1);
        }};
        SettlementReport report = SettlementReportManager.createReport(instructions);
        Map<Date, DailyReport> dailyReports = report.getDailyReports();

        BigDecimal[] bigDecimals = SettlementReportManager.aggregateIncomingOutgoing(dailyReports.get(INSTRUCTION_DATE));
        assertEquals(2, bigDecimals.length);
        assertEquals(0, bigDecimals[0].doubleValue());
        assertEquals(PresetInstructions.INSTRUCTION_1_VALUE, bigDecimals[1].doubleValue());

        String s = report.toString();
        assertEquals("Settlement Report :\n" +
                "On 04 Jan 2016:\n" +
                "Incoming: USD0.00\n" +
                "Incoming entity ranks: foo\n" +
                "Outgoing: USD10,025.00\n" +
                "Outgoing entity ranks: foo\n", s);
    }

}
