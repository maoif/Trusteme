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

    public int length() {
        if (this.car == TsmNull.INSTANCE) return 0;

        int len = 1;
        TsmExpr next = cdr;
        while (next != TsmNull.INSTANCE) {
            if (next instanceof TsmPair p) {
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

    public boolean isImproper() {
        if (this.car == TsmNull.INSTANCE) return false;

        TsmExpr next = cdr;
        while (next != TsmNull.INSTANCE) {
            if (next instanceof TsmPair p) {
                next = p.cdr();
            } else {
                return true;
            }
        }

        return false;
    }

    public TsmExpr ref(int i) {
        if (i < 0)
            throw new RuntimeException("index " + i + " is not an exact nonnegative integer");
        if (this.car == TsmNull.INSTANCE)
            throw new RuntimeException("pair is null");

        TsmExpr res = this.car;
        TsmExpr next = this.cdr;
        int idx = 0;
        while (idx < i) {
            if (next instanceof TsmPair p) {
                res = p.car;
                next = p.cdr;
                idx++;
            } else {
                throw new RuntimeException(this + " is not a proper list");
            }
        }

        return res;
    }

    public TsmVector toVector() {
        if (this.car == TsmNull.INSTANCE) return new TsmVector(0);

        if (isImproper())
            throw new RuntimeException(this + "is not a proper list");

        TsmVector vec = new TsmVector(this.length());
        vec.set(0, this.car);
        TsmExpr next = this.cdr;
        int i = 1;
        while (next instanceof TsmPair p) {
            vec.set(i, p.car);
            next = p.cdr;
            i++;
        }

        return vec;
    }

    public TsmExpr[] rawArray() {
        return toVector().rawArray();
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
