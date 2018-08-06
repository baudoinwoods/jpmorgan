package com.woobadeau.jpmorgan.settlement;

import com.woobadeau.jpmorgan.exception.SettlementConfigurationException;
import com.woobadeau.jpmorgan.exception.SettlementException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Manage settlement date calculations based on business rules.<br/>
 * By Default loads /currency_setup.properties to load currency configuration.<br/>
 * Can be setup with a different configuration file through call of com.woobadeau.jpmorgan.settlement.SettlementDateManager#setup(java.lang.String).
 */
public class SettlementDateManager {

    private static final String CURRENCY_SETUP_PROPERTIES = "/currency_setup.properties";
    private static SettlementDateManager instance;

    /**
     * Mapping currencies to days of week when settlement is possible
     */
    Map<String, Set<Integer>> currencyDaySettlement = new HashMap<>();

    /**
     * Default days for settlement
     */
    Set<Integer> defaultDaySettlement = null;

    private SettlementDateManager(InputStream currencyResourceStream) throws SettlementConfigurationException {
        Properties prop = new Properties();

        try (InputStream in = currencyResourceStream) {
            prop.load(in);
            loadCurrencyDaySettlement(prop);
        } catch (IOException e) {
            throw new SettlementConfigurationException("Error loading property file "+ currencyResourceStream, e);
        }
    }

    /**
     * Load configuration from property file with format:
     * #USD: settles Monday to Friday
     * currency.settlementDays.USD=1,2,3,4,5
     * #AED: settles Sunday to Thursday
     * currency.settlementDays.AED=0,1,2,3,4
     * #All currencies not specified: settles Monday to Friday
     * currency.default=1,2,3,4,5
     * @param properties the properties object
     */
    private void loadCurrencyDaySettlement(Properties properties) throws SettlementConfigurationException {
        Set<String> propertyNames = properties.stringPropertyNames();
        for (String key : propertyNames) {
            if (key.startsWith("currency.settlementDays")) {
                String currency = key.replace("currency.settlementDays.", "");
                String daysString = properties.getProperty(key);
                Set<Integer> settlementDays = parseSettlementDays(currency, daysString);
                currencyDaySettlement.put(currency, settlementDays);
            } else if ("currency.default".equals(key)) {
                defaultDaySettlement = parseSettlementDays("default", properties.getProperty(key));
            }
        }
    }

    /**
     * Adds entry to com.woobadeau.jpmorgan.settlement.SettlementDateManager#currencyDaySettlement
     * @param currency to be added
     * @param daysString String formatted as comma separated daysOfWeek values.
     */
    private Set<Integer> parseSettlementDays(String currency, String daysString) throws SettlementConfigurationException {
        String[] days = daysString.split(",");
        Set<Integer> settlementDays = new HashSet<>();
        for (String day : days) {
            try {
                settlementDays.add(Integer.parseInt(day.trim()));
            } catch (NumberFormatException e) {
                throw new SettlementConfigurationException("Cannot parse value for day " + day + " of currency " + currency, e);
            }
        }
        return settlementDays;
    }

    /**
     * @return the singleton instance
     */
    public static SettlementDateManager getInstance() throws SettlementConfigurationException {
        if (instance == null) {
            InputStream currencyResourceFile = SettlementReportManager.class.getResourceAsStream(CURRENCY_SETUP_PROPERTIES);
            instance = new SettlementDateManager(currencyResourceFile);
        }
        return instance;
    }

    /**
     * Modify the singleton instance to use the new resource file as configuration for currency setup
     * @param currencyResourceFile
     * @return
     */
    public static SettlementDateManager setup(InputStream currencyResourceFile) throws SettlementConfigurationException {
        instance = new SettlementDateManager(currencyResourceFile);
        return instance;
    }

    /**
     * Calculates the day of settlement depending on date attempted and currency.
     * @param date the date the customer wishes to settle their operation
     * @param currency the transaction's currency
     * @return the final date on which transaction will be settled.
     */
    public Date dateSettled(Date date, String currency) throws SettlementException, SettlementConfigurationException {
        Set<Integer> settlementDays = currencyDaySettlement.get(currency);
        if (settlementDays == null) {
            if (defaultDaySettlement == null) {
                throw new SettlementConfigurationException("Currency "+currency+" unknown and no default settlement dates configured");
            }
            settlementDays = defaultDaySettlement;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //Set the time to 00:00:00.000
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        for (int i = 0; i < 7; i ++) {
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (settlementDays.contains(dayOfWeek)) {
                return calendar.getTime();
            }
            calendar.add(Calendar.DAY_OF_WEEK, 1);
        }
        throw new SettlementException("No valid settlement day found for currency "+currency);
    }

}
