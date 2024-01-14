package org.maoif.trusteme.types;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;


public class TsmTextualInputPort extends TsmPort {
    private final TsmString _filename;

    // port impl without position
    private BufferedReader _in;
    private final boolean _isStream;

    // port impl from files, with position
    private char[] _chars;
    private int _index = 0;

    public TsmTextualInputPort(TsmString file) {
        super(true, false);

        try {
            var r = new FileReader(file.get(), StandardCharsets.UTF_8);
            readAllChars(r, file.value);
            this._isStream = false;
            this._filename = new TsmString(file.get());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Failed to open file " + file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public TsmTextualInputPort(InputStream in) {
        super(true, false);

        this._in = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        this._isStream = true;
        this._filename = new TsmString("in");
    }

    private void readAllChars(Reader r, String who) {
        ArrayList<Character> cs = new ArrayList<>();
        int c;
        try {
            while ((c = r.read()) != -1) {
                cs.add((char) c);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to open input file: " + who);
        }

        this._chars = new char[cs.size()];
        for (int i = 0; i < cs.size(); i++) {
            this._chars[i] = cs.get(i);
        }

        try {
            r.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to open input file: " + who);
        }
    }

    public char[] getCharArray() {
        return this._chars;
    }

    @Override
    public String toString() {
        return String.format("#<port %s>", this._filename.get());
    }

    @Override
    public TsmExpr getChar() {
        if (this._isStream) {
            try {
                int c = this._in.read();
                if (c == -1) {
                    return TsmEof.INSTANCE;
                }

                return new TsmChar((char) c);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read char from port: " + this);
            }
        } else {
            if (this._index >= this._chars.length) {
                return TsmEof.INSTANCE;
            }

            return new TsmChar(this._chars[this._index++]);
        }
    }

    @Override
    public TsmVoid putChar(TsmChar c) {
        throw new RuntimeException("Not implemented");
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
        if (this._isStream) {
            return TsmBool.FALSE;
        } else {
            return TsmBool.TRUE;
        }
    }

    @Override
    public TsmFixnum getPos() {
        if (this._isStream) {
            throw new RuntimeException("Not implemented");
        } else {
            return new TsmFixnum(this._index);
        }
    }

    @Override
    public TsmVoid setPos(TsmFixnum pos) {
        if (this._isStream) {
            throw new RuntimeException("Not implemented");
        } else {
            var p = pos.get();
            if (p < 0 || p >= this._chars.length) {
                this._index = this._chars.length;
            } else {
                this._index = (int) p;
            }
        }

        return TsmVoid.INSTANCE;
    }

    @Override
    public TsmExpr getLine() {
        if (this._isStream) {
            try {
                String s = this._in.readLine();
                if (s == null) {
                    return TsmEof.INSTANCE;
                } else {
                    return new TsmString(s);
                }
            } catch (IOException e) {
                return TsmEof.INSTANCE;
            }
        } else {
            if (this._index >= this._chars.length) return TsmEof.INSTANCE;
            StringBuilder sb = new StringBuilder();
            for (int i = this._index; i < this._chars.length; i++) {
                char c = this._chars[i];
                if (c == '\n') {
                    this._index = i + 1;
                    break;
                } else {
                    sb.append(c);
                }
            }

            return new TsmString(sb.toString());
        }
    }

    @Override
    public TsmExpr getStringN(TsmFixnum n) {
        if (this._isStream) {
            StringBuilder sb = new StringBuilder();
            int c;
            int j = (int) n.get();
            try {
                while ((c = this._in.read()) != -1 && j > 0) {
                    j--;
                    sb.append((char) c);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read from  " + this);
            }

            return new TsmString(sb.toString());
        } else {
            if (this._index >= this._chars.length) return TsmEof.INSTANCE;
            // if n + index >= length, just return all remaining ones
            int i = this._index;
            int j = (int) n.get();
            while (i < this._chars.length && j > 0) {
                i++;
                j--;
            }

            var res = new TsmString(String.valueOf(this._chars, this._index, i - this._index));
            this._index = i;
            return res;
        }
    }

    @Override
    public TsmExpr getStringAll() {
        if (this._isStream) {
            StringBuilder sb = new StringBuilder();
            int c;
            try {
                while ((c = this._in.read()) != -1) {
                    sb.append((char) c);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read from  " + this);
            }

            return new TsmString(sb.toString());
        } else {
            if (this._index >= this._chars.length) return TsmEof.INSTANCE;
            return new TsmString(String.valueOf(
                    this._chars, this._index, this._chars.length - this._index));
        }
    }

    @Override
    public void close() {
        if (this._isStream) {
            try {
                this._in.close();
            } catch (IOException e) {
                throw new RuntimeException("Failed to close " + this);
            }
        }
    }
}
