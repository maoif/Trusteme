package org.maoif.trusteme.types;

public class TsmNull extends TsmExpr {
    public static final TsmNull INSTANCE = new TsmNull();

    private TsmNull() { }

    @Override
    public String write() {
        return "()";
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
