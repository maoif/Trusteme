package org.maoif;

public class sNull extends sExpr {
    public static final sNull INSTANCE = new sNull();

    public sNull() {
        super(null, 0, 0);
    }

    public sNull(String src, int pos, int length) {
        super(src, pos, length);
    }

    public sNull(String src) {
        super(src);
    }
}