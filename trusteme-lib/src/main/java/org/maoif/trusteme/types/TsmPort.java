package org.maoif.trusteme.types;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;

public abstract class TsmPort extends TsmExpr {

    private final boolean isInputPort;
    private final boolean isBinaryPort;

    protected TsmPort(boolean isIn, boolean isBin) {
        this.isInputPort = isIn;
        this.isBinaryPort = isBin;
    }

    public abstract TsmExpr getChar();
    public abstract TsmVoid putChar(TsmChar c);
    public abstract TsmExpr getU8();
    public abstract TsmVoid putU8(TsmFixnum f);

    public abstract TsmBool hasPos();
    public abstract TsmFixnum getPos();
    public abstract TsmVoid setPos(TsmFixnum pos);

    public abstract TsmExpr getLine();
    public abstract TsmExpr getStringN(TsmFixnum n);
    public abstract TsmExpr getStringAll();

    public abstract void close();

    public boolean isInputPort() {
        return this.isInputPort;
    }

    public boolean isBinaryPort() {
        return this.isBinaryPort;
    }

    @Override
    public String toString() {
        return "#<port>";
    }

    @Override
    public String write() {
        return "#<port>";
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
