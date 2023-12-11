package org.maoif;


/**
 * End-of-file
 */
public class sEof extends sExpr {

    public sEof() {
        super(null, 0, 0);
    }

    // for explicit #!eof
    public sEof(String src, int pos, int length) {
        super(src, pos, length);
    }

    public sEof(String src, int pos) {
        super(src, pos, pos);
    }

    public sEof(String src) {
        super(src);
    }

    @Override
    public String toString() {
        return "#!eof";
    }
}
