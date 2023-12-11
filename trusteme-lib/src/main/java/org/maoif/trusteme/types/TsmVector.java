package org.maoif.trusteme.types;

public class TsmVector extends TsmExpr {
    private TsmExpr[] values;

    public TsmVector(TsmExpr[] values) {
        this.values = values;
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

}
