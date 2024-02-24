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

    @Override
    public boolean isEq(TsmExpr other) {
        return this == INSTANCE;
    }

    @Override
    public boolean isEqv(TsmExpr other) {
        return isEq(other);
    }

    @Override
    public boolean isEqual(TsmExpr other) {
        return isEq(other);
    }
}