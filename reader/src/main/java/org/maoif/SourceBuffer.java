package org.maoif;

public class SourceBuffer {

    private final String source;
    private final char[] src;
    private int pos = 0;
    private int line = 1;

    public SourceBuffer(String str) {
        source = str;
        src = str.toCharArray();
    }

    public SourceBuffer(char[] cs, int pos) {
        this.source = String.valueOf(cs);
        this.src = cs;
        this.pos = pos;
    }

    public boolean peek(String str) {
        if (pos + str.length() <= src.length) {
            return String.valueOf(src, pos, str.length()).equals(str);
        }
        return false;
    }

    public boolean peek(char c) {
        if (pos < src.length) {
            return src[pos] == c;
        }
        return false;
    }

    public char peek() {
        if (pos < src.length) return src[pos];
        return (char) -1;
    }

    public char read() {
        if (pos < src.length) return src[pos++];
        return (char) -1;
    }

    public void unread() {
        pos--;
    }

    /**
     * Advance current position by `chars`.
     * @param chars number of characters to advance.
     */
    public int advance(int chars) {
        if (pos + chars > src.length)
            throw new RuntimeException(String.format(
                    "Position (%d) out of source buffer length (%d)", pos, src.length));
        return pos += chars;
    }

    public void skipWhitespaces() {
        while (pos < src.length && Character.isWhitespace(src[pos])) {
            if (src[pos] == '\n') line++;
            pos++;
        }
    }

    public int getPos() {
        return pos;
    }

    public int getLine() {
        return line;
    }

    public String getSource() {
        return source;
    }
}
