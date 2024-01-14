package org.maoif.trusteme.types;

public class TsmBool extends TsmExpr {
//    public final boolean value;

    public static final TsmBool TRUE = new TsmBool();
    public static final TsmBool FALSE = new TsmBool();

    private TsmBool() { }

    public boolean isTrue() {
        return this.equals(TRUE);
    }

    public boolean isFalse() {
        return this.equals(FALSE);
    }

    public TsmBool negate() {
        if (this == TRUE) return FALSE;
        else              return TRUE;
    }

    public static TsmBool get(boolean b) {
        if (b) return TRUE;
        else   return FALSE;
    }

    @Override
    public String toString() {
        return this == TsmBool.TRUE ? "#t" : "#f";
    }

    @Override
    public String write() {
        return toString();
    }
}
