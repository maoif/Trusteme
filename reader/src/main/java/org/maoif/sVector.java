package org.maoif;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class sVector extends sExpr {

    private sExpr[] values = null;

    public sVector(String src, int pos, long val, int length) {
        super(src, pos, length);
    }

    public sVector(String src) {
        super(src);
    }

    public sExpr[] get() {
        return this.values;
    }

    public void set(sExpr[] vals) {
        this.values = vals;
    }

    @Override
    public String toString() {
        if (values == null)  return "#()";
        var l =  Stream.of(values).map(Object::toString).collect(Collectors.toList());
        return "#(" + String.join(" ", l) + ")";
    }
}