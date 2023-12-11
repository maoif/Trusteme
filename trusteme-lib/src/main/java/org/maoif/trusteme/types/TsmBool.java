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
}
