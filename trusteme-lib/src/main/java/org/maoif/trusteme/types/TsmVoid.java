package org.maoif.trusteme.types;

public class TsmVoid extends TsmExpr {
    public static final TsmVoid INSTANCE = new TsmVoid();

    private TsmVoid() { }

    @Override
    public String toString() {
        return "#<void>";
    }

    @Override
    public String write() {
        return "#<void>";
    }
}