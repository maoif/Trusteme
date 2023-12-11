package org.maoif;

public abstract class sExpr {
    // source code string
    protected String src = null;
    // start position of the source that corresponds to this object
    protected int posStart = 0;
    // position after the last character of this object
    protected int posEnd = 0;
    // length of the source text
    protected int length = 0;

    protected sExpr(String src, int posStart, int posEnd) {
        this.src = src;
        this.posStart = posStart;
        this.posEnd = posEnd;
    }

    protected sExpr() { }

    protected sExpr(String src) {
        this.src = src;
    }

    public String getSource() {
        return src.substring(posStart, length);
    }

    public int getLine() {
        // TODO maybe handle other line endings
        int lines = 1;
        char[] chars = src.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == posStart) break;
            if (chars[i] == '\n') lines++;
        }

        return lines;
    }

    public int getPosStart() {
        return posStart;
    }

    public int getSrcLength() {
        return length;
    }

    public void setPosStart(int p) {
        posStart = p;
    }

    public void setPosEnd(int p) {
        posEnd = p;
    }

    /**
     * Remove lexical info.
     */
    public void strip() {
        src = null;
        posStart = 0;
        length = 0;
    }
}