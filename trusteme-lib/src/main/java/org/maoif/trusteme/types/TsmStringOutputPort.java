package org.maoif.trusteme.types;

public class TsmStringOutputPort extends TsmPort {
    private StringBuffer _sb = new StringBuffer();
    private int _pos = 0;

    public TsmStringOutputPort() {
        super(false, false);
    }

    @Override
    public TsmVoid putChar(TsmChar c) {
        _sb.insert(_pos++, c.get());

        return TsmVoid.INSTANCE;
    }

    public void putString(String s) {
        _sb.insert(_pos, s);
        _pos += s.length();
    }

    public String extract() {
        _pos = 0;
        String s = _sb.toString();
        _sb = new StringBuffer();

        return s;
    }

    @Override
    public TsmExpr getChar() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmExpr getU8() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmVoid putU8(TsmFixnum f) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmBool hasPos() {
        return TsmBool.TRUE;
    }

    @Override
    public TsmFixnum getPos() {
        return new TsmFixnum(_pos);
    }

    @Override
    public TsmVoid setPos(TsmFixnum pos) {
        var p = pos.get();
        if (p < 0 || p > _sb.length()) {
            throw new RuntimeException("Position out of bound: " + p );
        }
        _pos = (int) p;

        return TsmVoid.INSTANCE;
    }

    @Override
    public TsmExpr getLine() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmExpr getStringN(TsmFixnum n) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmExpr getStringAll() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void close() { }

}
