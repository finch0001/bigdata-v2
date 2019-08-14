package com.yee.bigdata.common.io;

public final class Printer {
    public static final String lineSeparator = System.getProperty("line.separator");;
    private final StringBuilder builder = new StringBuilder();

    public void print(String string) {
        builder.append(string);
    }

    public void println(String string) {
        builder.append(string);
        builder.append(lineSeparator);
    }

    public void println() {
        builder.append(lineSeparator);
    }

    public String toString() {
        return builder.toString();
    }
}

