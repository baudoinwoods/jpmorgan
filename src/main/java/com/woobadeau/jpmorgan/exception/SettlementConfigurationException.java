package com.woobadeau.jpmorgan.exception;

public class SettlementConfigurationException extends Exception {
    public SettlementConfigurationException(String message, Exception cause) {
        super(message, cause);
    }

    public SettlementConfigurationException(String message) {
        super(message);
    }
}
