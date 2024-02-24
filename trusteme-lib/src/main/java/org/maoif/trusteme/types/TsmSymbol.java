package org.maoif.trusteme.types;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TsmSymbol extends TsmExpr {
    private static final ConcurrentMap<String, TsmSymbol> _pool =
            new ConcurrentHashMap<>();

    private final String value;

    private TsmSymbol(String val) {
        this.value = val;
    }

    public static TsmSymbol get(String val) {
        if (_pool.containsKey(val)) {
            return _pool.get(val);
        } else {
            var sym = new TsmSymbol((val));
            _pool.put(val, sym);
            return sym;
        }
    }

    public String get() {
        return this.value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public String write() {
        return value;
    }

    @Override
    public boolean isEq(TsmExpr other) {
        return this == other;
    }

    @Override
    public boolean isEqv(TsmExpr other) {
        return isEq(other);
    }

    @Override
    public boolean isEqual(TsmExpr other) {
        return isEq(other);
    }
}
