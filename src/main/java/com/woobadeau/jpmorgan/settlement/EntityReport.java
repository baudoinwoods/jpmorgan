package com.woobadeau.jpmorgan.settlement;

import java.math.BigDecimal;

/**
 * Class to report daily incoming and outgoing settlements for an entity
 */
class EntityReport {
    private BigDecimal incomingValue = new BigDecimal(0);
    private BigDecimal outgoingValue = new BigDecimal(0);
    private final String entity;

    public EntityReport(String entity) {
        this.entity = entity;
    }

    BigDecimal getIncomingValue() {
        return incomingValue;
    }

    BigDecimal getOutgoingValue() {
        return outgoingValue;
    }

    void addIncomingValue(BigDecimal instructionValue) {
        incomingValue = incomingValue.add(instructionValue);
    }

    void addOutgoingValue(BigDecimal instructionValue) {
        outgoingValue = outgoingValue.add(instructionValue);
    }

    public String getEntity() {
        return entity;
    }
}
