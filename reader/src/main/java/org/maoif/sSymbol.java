package org.maoif;

public class sSymbol extends sExpr {
    public static final sSymbol SYM_QUOTE = new sSymbol("quote");
    public static final sSymbol SYM_DEFINE = new sSymbol("define");
    public static final sSymbol SYM_IF     = new sSymbol("if");
    public static final sSymbol SYM_BEGIN  = new sSymbol("begin");
    public static final sSymbol SYM_SET    = new sSymbol("set!");
    public static final sSymbol SYM_LETREC = new sSymbol("letrec");
    public static final sSymbol SYM_LAMBDA = new sSymbol("lambda");

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