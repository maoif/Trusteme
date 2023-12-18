package org.maoif;

public class sSymbol extends sExpr {
    public static final sSymbol SYM_QUOTE = makeSymbol("quote");
    public static final sSymbol SYM_DEFINE = makeSymbol("define");
    public static final sSymbol SYM_IF     = makeSymbol("if");
    public static final sSymbol SYM_BEGIN  = makeSymbol("begin");
    public static final sSymbol SYM_SET    = makeSymbol("set!");
    public static final sSymbol SYM_LETREC = makeSymbol("letrec");
    public static final sSymbol SYM_LAMBDA = makeSymbol("lambda");

    private String value;

    public sSymbol(String src, int pos, String val, int length) {
        super(src, pos, length);
        this.value = val;
    }

    public sSymbol(String src) {
        super(src);
    }

    public sSymbol(String src, String val) {
        super(src);
        this.value = val;
    }

    public static sSymbol makeSymbol(String value) {
        return new sSymbol(null, value);
    }

    public String get() {
        return this.value;
    }

    public void set(String val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}