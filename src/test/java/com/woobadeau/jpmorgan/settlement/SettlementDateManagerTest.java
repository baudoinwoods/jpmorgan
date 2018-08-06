package com.woobadeau.jpmorgan.settlement;

import com.woobadeau.jpmorgan.exception.SettlementConfigurationException;
import com.woobadeau.jpmorgan.exception.SettlementException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SettlementDateManager.
 */
public class SettlementDateManagerTest {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");

    private static final Date MONDAY;
    private static final Date TUESDAY;
    private static final Date WEDNESDAY;
    private static final Date THURSDAY;
    private static final Date FRIDAY;
    private static final Date SATURDAY;
    private static final Date SUNDAY;
    private static final Date NEXT_MONDAY;

    static {
        try {
            MONDAY = formatter.parse("20180806");
            TUESDAY = formatter.parse("20180807");
            WEDNESDAY = formatter.parse("20180808");
            THURSDAY = formatter.parse("20180809");
            FRIDAY = formatter.parse("20180810");
            SATURDAY = formatter.parse("20180811");
            SUNDAY = formatter.parse("20180812");
            NEXT_MONDAY = formatter.parse("20180813");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Ensures the getInstance is called without initialisation.
     */
    @BeforeAll
    public static void init() throws SettlementConfigurationException {
        SettlementDateManager.getInstance();
    }

    /**
     * Reset the SettlementDateManager setup with production file.
     */
    @BeforeEach
    public void reset() throws SettlementConfigurationException {
        SettlementDateManager.setup(SettlementDateManagerTest.class.getResourceAsStream("/currency_setup.properties"));
    }

    /**
     * Test configuration reads as expected.
     */
    @Test
    public void configurationTest() throws SettlementConfigurationException {
        SettlementDateManager instance = SettlementDateManager.setup(SettlementDateManagerTest.class.getResourceAsStream("/testCurrencySettlement.properties"));
        assertTrue(instance.currencyDaySettlement.containsKey("M_TO_F"));
        assertTrue(instance.currencyDaySettlement.containsKey("S_TO_T"));
        assertTrue(instance.currencyDaySettlement.containsKey("T_AND_S"));

        //Check Fridays
        assertTrue(instance.currencyDaySettlement.get("M_TO_F").contains(Calendar.FRIDAY));
        assertFalse(instance.currencyDaySettlement.get("S_TO_T").contains(Calendar.FRIDAY));
        assertFalse(instance.currencyDaySettlement.get("T_AND_S").contains(Calendar.FRIDAY));
        assertTrue(instance.defaultDaySettlement.contains(Calendar.FRIDAY));

        //Check Sundays
        assertFalse(instance.currencyDaySettlement.get("M_TO_F").contains(Calendar.SUNDAY));
        assertTrue(instance.currencyDaySettlement.get("S_TO_T").contains(Calendar.SUNDAY));
        assertFalse(instance.currencyDaySettlement.get("T_AND_S").contains(Calendar.SUNDAY));
        assertFalse(instance.defaultDaySettlement.contains(Calendar.SUNDAY));

        //Check Saturdays
        assertFalse(instance.currencyDaySettlement.get("M_TO_F").contains(Calendar.SATURDAY));
        assertFalse(instance.currencyDaySettlement.get("S_TO_T").contains(Calendar.SATURDAY));
        assertTrue(instance.currencyDaySettlement.get("T_AND_S").contains(Calendar.SATURDAY));
        assertFalse(instance.defaultDaySettlement.contains(Calendar.SATURDAY));

    }

    /**
     * Test that com.woobadeau.jpmorgan.exception.SettlementConfigurationException is thrown if the days are malformed in the config file
     */
    @Test
    public void configurationFailsDaysMalformed() throws SettlementConfigurationException {
        Assertions.assertThrows(SettlementConfigurationException.class, () -> SettlementDateManager.setup(SettlementDateManagerTest.class.getResourceAsStream("/testCurrencySettlementMalformed.properties")));
    }

    /**
     * Test that if no default currency is configured and currency is not found, com.woobadeau.jpmorgan.exception.SettlementConfigurationException is thrown.
     */
    @Test
    public void currencyNotFound() throws SettlementConfigurationException {
        SettlementDateManager settlementDateManager = SettlementDateManager.setup(SettlementDateManagerTest.class.getResourceAsStream("/testCurrencySettlementNoDefault.properties"));
        Assertions.assertThrows(SettlementConfigurationException.class, () -> settlementDateManager.dateSettled(MONDAY, "XXX"));
    }

    /**
     * Test that monday to friday for non AED / SAR Currencies are considered weekdays => dateSettled returns same date as parameter
     */
    @Test
    public void weekDayDefaultTest() throws SettlementConfigurationException, SettlementException {
        assertCanSettleOnSameDay(MONDAY, "XXX");
        assertCanSettleOnSameDay(TUESDAY, "XXX");
        assertCanSettleOnSameDay(WEDNESDAY, "XXX");
        assertCanSettleOnSameDay(THURSDAY, "XXX");
        assertCanSettleOnSameDay(FRIDAY, "XXX");
        assertCannotSettleOnSameDay(SATURDAY, "XXX");
        assertCannotSettleOnSameDay(SUNDAY, "XXX");
    }

    /**
     * Test that AED Settles Sunday to Thursday
     */
    @Test
    public void aedSettlesSundayToThursday() throws SettlementConfigurationException, SettlementException {
        assertCanSettleOnSameDay(MONDAY, "AED");
        assertCanSettleOnSameDay(TUESDAY, "AED");
        assertCanSettleOnSameDay(WEDNESDAY, "AED");
        assertCanSettleOnSameDay(THURSDAY, "AED");
        assertCannotSettleOnSameDay(FRIDAY, "AED");
        assertCannotSettleOnSameDay(SATURDAY, "AED");
        assertCanSettleOnSameDay(SUNDAY, "AED");
    }

    /**
     * Test that SAR Settles Sunday to Thursday
     */
    @Test
    public void sarSettlesSundayToThursday() throws SettlementConfigurationException, SettlementException {
        assertCanSettleOnSameDay(MONDAY, "SAR");
        assertCanSettleOnSameDay(TUESDAY, "SAR");
        assertCanSettleOnSameDay(WEDNESDAY, "SAR");
        assertCanSettleOnSameDay(THURSDAY, "SAR");
        assertCannotSettleOnSameDay(FRIDAY, "SAR");
        assertCannotSettleOnSameDay(SATURDAY, "SAR");
        assertCanSettleOnSameDay(SUNDAY, "SAR");
    }

    /**
     * Test Default settlement in production configuration is Monday for requests on Saturday and Sunday
     */
    @Test
    public void defaultSettlesMondayForSaturdaySunday() throws SettlementConfigurationException, SettlementException {
        Date dateSettled = SettlementDateManager.getInstance().dateSettled(SATURDAY, "XXX");
        assertEquals(NEXT_MONDAY, dateSettled);
        dateSettled = SettlementDateManager.getInstance().dateSettled(SUNDAY, "XXX");
        assertEquals(NEXT_MONDAY, dateSettled);
    }

    /**
     * Test AED settlement in production configuration is Sunday for requests on Friday and Saturday
     */
    @Test
    public void aedSettlesSundayForFridaySaturday() throws SettlementConfigurationException, SettlementException {
        Date dateSettled = SettlementDateManager.getInstance().dateSettled(FRIDAY, "AED");
        assertEquals(SUNDAY, dateSettled);
        dateSettled = SettlementDateManager.getInstance().dateSettled(SATURDAY, "AED");
        assertEquals(SUNDAY, dateSettled);
    }

    /**
     * Test SAR settlement in production configuration is Sunday for requests on Friday and Saturday
     */
    @Test
    public void sarSettlesSundayForFridaySaturday() throws SettlementConfigurationException, SettlementException {
        Date dateSettled = SettlementDateManager.getInstance().dateSettled(FRIDAY, "SAR");
        assertEquals(SUNDAY, dateSettled);
        dateSettled = SettlementDateManager.getInstance().dateSettled(SATURDAY, "SAR");
        assertEquals(SUNDAY, dateSettled);
    }

    /**
     * Test com.woobadeau.jpmorgan.exception.SettlementException is thrown when no valid settlement day is found.
     */
    @Test
    public void exceptionWhenNoSettlementDayFound() throws SettlementConfigurationException {
        SettlementDateManager settlementDateManager = SettlementDateManager.setup(SettlementDateManagerTest.class.getResourceAsStream("/testCurrencySettlement.properties"));
        Assertions.assertThrows(SettlementException.class, () -> settlementDateManager.dateSettled(MONDAY, "NONE"));
    }

    private void assertCanSettleOnSameDay(Date date, String currency) throws SettlementConfigurationException, SettlementException {
        Date dateSettled = SettlementDateManager.getInstance().dateSettled(date, currency);
        assertEquals(date, dateSettled);
    }

    private void assertCannotSettleOnSameDay(Date date, String currency) throws SettlementConfigurationException, SettlementException {
        Date dateSettled = SettlementDateManager.getInstance().dateSettled(date, currency);
        assertNotEquals(date, dateSettled);
    }
}
