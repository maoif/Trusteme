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
}
