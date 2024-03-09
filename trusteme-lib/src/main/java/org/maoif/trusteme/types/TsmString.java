package org.maoif.trusteme.types;

public class TsmString extends TsmExpr {
    public String value;

    public TsmString(String val) {
        this.value = val;
    }

    public void replace(String val) {
        this.value = val;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public String get() {
        return this.value;
    }

    @Override
    public String write() {
        return "\"" + this.value + "\"";
    }

    @Override
    public boolean isEq(TsmExpr other) {
        return this == other;
    }

    @Override
    public boolean isEqv(TsmExpr other) {
        return isEqual(other);
    }

    @Override
    public boolean isEqual(TsmExpr other) {
        if (other instanceof TsmString s) {
            return s.get().equals(this.value);
        } else {
            return false;
        }
    }
}
