package org.maoif.trusteme.types;

public class TsmEof extends TsmExpr {
    public static final TsmEof INSTANCE = new TsmEof();

    private TsmEof() { }

    @Override
    public String toString() {
        return "#!eof";
    }

    @Override
    public String write() {
        return toString();
    }

    @Override
    public boolean isEq(TsmExpr other) {
        return isEqual(other);
    }

    @Override
    public boolean isEqv(TsmExpr other) {
        return isEqual(other);
    }

    @Override
    public boolean isEqual(TsmExpr other) {
        return this == INSTANCE;
    }
}
