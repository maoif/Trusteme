package org.maoif.trusteme.types;

import java.util.LinkedList;
import java.util.List;

public class TsmPair extends TsmExpr {
    private TsmExpr car;
    private TsmExpr cdr;

    public TsmPair() {
        this.car = TsmNull.INSTANCE;
        this.cdr = TsmNull.INSTANCE;
    }

    public TsmPair(TsmExpr car) {
        this.car = car;
        this.cdr = TsmNull.INSTANCE;
    }

    public TsmPair(TsmExpr car, TsmExpr cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public TsmExpr car() {
        return this.car;
    }

    public TsmExpr cdr() {
        return this.cdr;
    }

    public void setCar(TsmExpr val) {
        this.car = val;
    }

    public void setCdr(TsmExpr val) {
        this.cdr = val;
    }

    @Override
    public String toString() {
        if (car == TsmNull.INSTANCE)  return "()";

        List<String> strs = new LinkedList<>();
        TsmExpr obj = this;
        while (true) {
            if (obj instanceof TsmPair p) {
                strs.add(p.car().toString());
                obj = p.cdr();
            } else if (obj == TsmNull.INSTANCE) {
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
