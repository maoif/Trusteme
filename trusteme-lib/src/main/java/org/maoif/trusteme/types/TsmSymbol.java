package org.maoif.trusteme.types;

public class TsmSymbol extends TsmExpr {
    private final String value;

    public TsmSymbol(String val) {
        this.value = val;
    }

    public String get() {
        return this.value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String write() {
        return value;
    }
}
