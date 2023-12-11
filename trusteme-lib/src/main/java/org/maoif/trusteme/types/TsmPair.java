package org.maoif.trusteme.types;

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
}
