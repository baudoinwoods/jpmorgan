package com.woobadeau.jpmorgan.exception;

public class InstructionParsingException extends Exception {
    public InstructionParsingException(String message) {
        super(message);
    }

    public InstructionParsingException(String message, Exception cause) {
        super(message, cause);
    }
}
