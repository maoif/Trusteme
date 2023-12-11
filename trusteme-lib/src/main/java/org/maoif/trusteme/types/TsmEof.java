package org.maoif.trusteme.types;

public class TsmEof extends TsmExpr {
    public static final TsmEof INSTANCE = new TsmEof();

    private TsmEof() { }

    @Override
    public String toString() {
        return "#<void>";
    }
}
