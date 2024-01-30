package org.maoif.trusteme.types;

import java.util.HashMap;

public class TsmEqHashtable extends TsmHashtable {
    private HashMap<TsmExpr, TsmExpr> _map = new HashMap<>();

    public TsmEqHashtable() { }

    @Override
    public void set(TsmExpr key, TsmExpr value) {
        this._map.put(key, value);
    }

    @Override
    public TsmExpr ref(TsmExpr key) {
        return this._map.get(key);
    }

    @Override
    public boolean contains(TsmExpr key) {
        return this._map.containsKey(key);
    }

    @Override
    public int size() {
        return this._map.size();
    }

    @Override
    public String write() {
        return "#<eq hashtable>";
    }

    @Override
    public String toString() {
        return write();
    }
}
