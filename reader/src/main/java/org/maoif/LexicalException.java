package org.maoif;

public class LexicalException extends Exception {
    private final String src;
    private final int pos;


    public LexicalException(String src, int pos, String errMsg) {
        super(errMsg);
        this.src = src;
        this.pos = pos;
    }

    public LexicalException(String src, int pos, String format, Object... args) {
        super(String.format(format, args));
        this.src = src;
        this.pos = pos;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.getMessage());
        sb.append("\n");
        sb.append(String.format("in position %d, line:\n\n", pos));
        sb.append(getErrLine());

        return sb.toString();
    }

    private String getErrLine() {
        int linePos = getPosOfLine();
        return String.valueOf(src.toCharArray(), linePos, pos - linePos);
    }

    private int getPosOfLine() {
        char[] cs = src.toCharArray();
        int lineStart = 0;
        for (int i = 0; i < cs.length; i++) {
            if (i == pos) break;
            if (cs[i] == '\n') lineStart = i + 1;
        }

        return lineStart;
    }
}
