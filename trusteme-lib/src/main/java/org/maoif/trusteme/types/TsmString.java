package org.maoif.trusteme.types;

public class TsmString extends TsmExpr {
    public final String value;

    public TsmString(String val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String get() {
        return this.value;
    }

    @Override
    public String write() {
        return "\"" + this.value + "\"";
    }
}
