package org.maoif.trusteme.types;

import java.io.*;

public class TsmTextualOutputPort extends TsmPort {
    private final BufferedWriter _out;
    private final TsmString _filename;

    public TsmTextualOutputPort(TsmString file) {
        super(false, false);

        try {
            this._out  = new BufferedWriter(new FileWriter(file.get()));
            this._filename = file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to open file " + file);
        }
    }

    public TsmTextualOutputPort(OutputStream out) {
        super(false, false);

        this._out  = new BufferedWriter(new OutputStreamWriter(out));
        this._filename = new TsmString("out");
    }

    public BufferedWriter get() {
        return _out;
    }

    @Override
    public String toString() {
        return String.format("#<port %s>", this._filename.get());
    }

    @Override
    public TsmChar getChar() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmVoid putChar(TsmChar c) {
        try {
            this._out.write(c.get());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write to port " + this);
        }

        return TsmVoid.INSTANCE;
    }

    @Override
    public TsmFixnum getU8() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmVoid putU8(TsmFixnum f) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmBool hasPos() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmFixnum getPos() {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public TsmVoid setPos(TsmFixnum pos) {
        throw new RuntimeException("Not implemented");
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
    public void close() {
        try {
            this._out.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close port " + this);
        }
    }
}
