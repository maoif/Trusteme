package org.maoif;

public class sVoid extends sExpr {
    public sVoid() {
        super(null, 0, 0);
    }

    // for explicit #<void>
    public sVoid(String src, int pos, int length) {
        super(src, pos, length);
    }

    public sVoid(String src, int pos) {
        super(src, pos, pos);
    }

    public sVoid(String src) {
        super(src);
    }

    @Override
    public String toString() {
        return "#<void>";
    }
}
