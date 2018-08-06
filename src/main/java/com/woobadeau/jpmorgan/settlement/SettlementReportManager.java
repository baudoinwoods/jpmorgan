package com.woobadeau.jpmorgan.settlement;

import com.woobadeau.jpmorgan.exception.SettlementConfigurationException;
import com.woobadeau.jpmorgan.exception.SettlementException;
import com.woobadeau.jpmorgan.transaction.Instruction;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Class to manage the creation of Settlement Reports based on lists of instructions.
 */
public class SettlementReportManager {

    /**
     * Convert a list of com.woobadeau.jpmorgan.transaction.Instruction into a com.woobadeau.jpmorgan.settlement.SettlementReport
     * @param instructions
     * @return a com.woobadeau.jpmorgan.settlement.SettlementReport
     */
    public static SettlementReport createReport(Set<Instruction> instructions) throws SettlementConfigurationException, SettlementException {
        Map<Date, DailyReport> dailyReports = new HashMap<>();
        for (Instruction instruction : instructions) {
            Date settlementDate = SettlementDateManager.getInstance().dateSettled(instruction.getSettlementDate(), instruction.getCurrency());
            DailyReport dailyReport = dailyReports.get(settlementDate);
            if (dailyReport == null) {
                dailyReport = new DailyReport();
                dailyReports.put(settlementDate, dailyReport);
            }
            dailyReport.addInstruction(instruction);
        }
        return new SettlementReport(dailyReports);
    }

    /**
     * Calculates the aggregate of incoming and outgoing values accross entities for a report
     * @param report
     * @return an array containing two BigDecimals. Index 0 is Incoming, Index 1 is Outgoing
     */
    public static BigDecimal[] aggregateIncomingOutgoing(DailyReport report) {
        BigDecimal[] result = new BigDecimal[]{new BigDecimal(0), new BigDecimal(0)};
        for (EntityReport entityReport : report.entityReports.values()) {
            result[0] = result[0].add(entityReport.getIncomingValue());
            result[1] = result[1].add(entityReport.getOutgoingValue());
        }
        return result;
    }

    /**
     * Sorts entities by Incoming value in the Daily Report.
     * @param dailyReport
     * @return a sorted list of Entity name. Entities with similar income will be sorted by alphabetical order.
     */
    public static List<String> sortIncoming(DailyReport dailyReport) {
        return sortEntities(dailyReport, EntityReport::getIncomingValue);
    }

    public static List<String> sortOutgoing(DailyReport dailyReport) {
        return sortEntities(dailyReport, EntityReport::getOutgoingValue);
    }

    private static List<String> sortEntities(DailyReport dailyReport, Function<EntityReport, BigDecimal> elementToCompare) {
        return dailyReport.entityReports.values()
                .stream()
                .sorted(Comparator.comparing(elementToCompare)
                        .thenComparing(EntityReport::getEntity))
                .map(EntityReport::getEntity)
                .collect(Collectors.toList());
    }

}
