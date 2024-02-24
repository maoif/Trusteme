package org.maoif.trusteme.types;

import java.util.Arrays;

public class TsmVector extends TsmExpr {
    private TsmExpr[] values;

    public TsmVector(TsmExpr[] values) {
        this.values = values;
    }

    public TsmVector(int len) {
        this.values = new TsmExpr[len];
    }

    public TsmVector(int len, TsmExpr init) {
        this.values = new TsmExpr[len];
        Arrays.fill(this.values, init);
    }

    public int length() {
        return this.values.length;
    }

    public TsmExpr ref(int i) {
        if (i < 0 || i >= values.length)
            throw new RuntimeException("index out of range");

        return values[i];
    }

    public void set(int i, TsmExpr v) {
        if (i < 0 || i >= values.length)
            throw new RuntimeException("index out of range");

        values[i] = v;
    }

    public TsmPair toList() {
        return null;
    }

    public TsmExpr[] rawArray() {
        return this.values;
    }

    @Override
    public String toString() {
        if (values == null) return "#()";

        StringBuilder sb = new StringBuilder("#(");
        for (int i = 0; i < values.length; i++) {
            if (i == values.length - 1)
                sb.append(values[i].toString());
            else {
                sb.append(values[i].toString());
                sb.append(" ");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    @Override
    public String write() {
        if (values == null) return "#()";

        StringBuilder sb = new StringBuilder("#(");
        for (int i = 0; i < values.length; i++) {
            if (i == values.length - 1)
                sb.append(values[i].write());
            else {
                sb.append(values[i].write());
                sb.append(" ");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    @Override
    public boolean isEq(TsmExpr other) {
        if (other instanceof TsmVector vec) {
            if (this.values.length == 0 && vec.values.length == 0) {
                return true;
            } else {
                return this == other;
            }
        }

        return false;
    }

    @Override
    public boolean isEqv(TsmExpr other) {
        return isEq(other);
    }

    @Override
    public boolean isEqual(TsmExpr other) {
        // TODO handle cycles
        if (other instanceof TsmVector vec) {
            if (vec.length() == this.length()) {
                for (int i = 0; i < this.length(); i++) {
                    if (!this.values[i].isEqual(vec.values[i])) {
                        return false;
                    }
                }

                return true;
            }
        }

        return false;
    }
}
