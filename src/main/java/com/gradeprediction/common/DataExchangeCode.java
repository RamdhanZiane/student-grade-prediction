package com.gradeprediction.common;

public enum DataExchangeCode {

    GRADES_RECEIVED(1, "Grades received by the server successfully"),
    NOT_VALID_DATA(2, "The sent data is not well formatted"),
    DATA_ALREADY_RECEIVED(3, "The school already submitted the grades");

    private final int code;
    private final String description;

    private DataExchangeCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }
}
