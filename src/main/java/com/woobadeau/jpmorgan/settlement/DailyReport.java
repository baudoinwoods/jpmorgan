package com.woobadeau.jpmorgan.settlement;

import com.woobadeau.jpmorgan.transaction.Instruction;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

/**
 * Class to report daily settlements for several entities
 */
class DailyReport {
    private static final NumberFormat CURRENCY_FORMAT;

    static {
        CURRENCY_FORMAT = NumberFormat.getCurrencyInstance();
        CURRENCY_FORMAT.setCurrency(Currency.getInstance("USD"));
    }
    //Ignoring case for entity names
    final Map<String, EntityReport> entityReports = new TreeMap<>(Comparator.comparing(String::toUpperCase));

    void addInstruction(Instruction instruction) {
        String entity = instruction.getEntity();
        EntityReport entityReport = entityReports.get(entity);
        if (entityReport == null) {
            entityReport = new EntityReport(entity);
            entityReports.put(entity, entityReport);
        }
        BigDecimal instructionValue = instruction.getPricePerUnit().multiply(instruction.getForex()).multiply(new BigDecimal(instruction.getUnits()));
        switch (instruction.getTransactionDirection()) {
            case S:
                entityReport.addIncomingValue(instructionValue);
                break;
            case B:
                entityReport.addOutgoingValue(instructionValue);
                break;
        }
    }

    Map<String, EntityReport> getEntityReports() {
        return entityReports;
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        BigDecimal[] totals = SettlementReportManager.aggregateIncomingOutgoing(this);
        result.append("Incoming: ");
        result.append(CURRENCY_FORMAT.format(totals[0]));
        result.append("\nIncoming entity ranks: ");
        List<String> sortedByIncoming = SettlementReportManager.sortIncoming(this);
        result.append(String.join(", ", sortedByIncoming));
        result.append("\nOutgoing: ");
        result.append(CURRENCY_FORMAT.format(totals[1]));
        result.append("\nOutgoing entity ranks: ");
        List<String> sortedByOutgoing = SettlementReportManager.sortOutgoing(this);
        result.append(String.join(", ", sortedByOutgoing));
        result.append("\n");
        return result.toString();
    }

}
