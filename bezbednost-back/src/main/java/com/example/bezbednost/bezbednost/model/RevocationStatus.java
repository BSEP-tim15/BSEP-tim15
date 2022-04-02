package com.example.bezbednost.bezbednost.model;

public enum RevocationStatus {

    VALID("Valid"), UNKNOWN("Unknown"), REVOKED("Revoked");
    private String message;

    RevocationStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
