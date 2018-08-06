package com.woobadeau.jpmorgan.settlement;

import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for reporting of settlements over several days.
 */
public class SettlementReport {

    /**
     * Daily reports, keyset ordered by date.
     */
    private final Map<Date, DailyReport> dailyReports = new TreeMap<>(Comparator.comparingLong(Date::getTime));
    private SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy");

    SettlementReport(Map<Date, DailyReport> dailyReports) {
        this.dailyReports.putAll(dailyReports);
    }

    Map<Date, DailyReport> getDailyReports() {
        return dailyReports;
    }

    @Override
    public String toString() {
        StringBuffer stringBuffer = new StringBuffer("Settlement Report :\n");
        for (Map.Entry<Date, DailyReport> entry: dailyReports.entrySet()) {
            stringBuffer.append("On ");
            stringBuffer.append(formatter.format(entry.getKey()));
            stringBuffer.append(":\n");
            stringBuffer.append(entry.getValue().toString());
        }
        return stringBuffer.toString();
    }
}
