package org.maoif;

import java.util.LinkedList;
import java.util.List;

public class sPair extends sExpr {

    // TODO maybe '() should not use sPair

    private sExpr car = sNull.INSTANCE;
    private sExpr cdr = sNull.INSTANCE;

    public sPair(String src, int pos, int length) {
        super(src, pos, length);
    }

    public sPair(String src) {
        super(src);
    }

    public sPair() {
        super();
    }

    public sPair(String src, int pos, sExpr car, sExpr cdr, int length) {
        super(src, pos, length);
        this.car = car;
        this.cdr = cdr;
    }

    public sExpr car() {
        return this.car;
    }

    public sExpr cdr() {
        return this.cdr;
    }

    public void setCar(sExpr e) {
        this.car = e;
    }

    public void setCdr(sExpr e) {
        this.cdr = e;
    }

    public int length() {
        if (car instanceof sNull) return 0;

        int len = 1;
        sExpr next = cdr;
        while (!(next instanceof sNull)) {
            if (next instanceof sPair p) {
                len++;
                next = p.cdr();
            } else {
                throw new RuntimeException(String.format(
                        "cannot compute length for improper list: %s", this
                ));
            }
        }

        return len;
    }

    public int improperLength() {
        if (car instanceof sNull) return 0;

        int len = 1;
        sExpr next = cdr;
        while (!(next instanceof sNull)) {
            if (next instanceof sPair p) {
                len++;
                next = p.cdr();
            } else break;
        }

        return len;
    }

    @Override
    public String toString() {
        if (car instanceof sNull)  return "()";

        List<String> strs = new LinkedList<>();
        sExpr obj = this;
        while (true) {
            if (obj instanceof sPair p) {
                strs.add(p.car().toString());
                obj = p.cdr();
            } else if (obj instanceof sNull) {
                break;
            } else {
                strs.add(".");
                strs.add(obj.toString());
                break;
            }
        }

        return "(" + String.join(" ", strs) + ")";
    }
}