package org.maoif.trusteme.types;

public class TsmNull extends TsmExpr {
    public static final TsmNull INSTANCE = new TsmNull();

    private TsmNull() { }

    @Override
    public String write() {
        return "()";
    }
}
