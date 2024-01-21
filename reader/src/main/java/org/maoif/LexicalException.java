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
        int start = getErrLineStart();
        int end   = getErrLineEnd(start);
        int pos   = end - start;
        var errLine = String.valueOf(src.toCharArray(), start, pos);

        StringBuilder sb = new StringBuilder();
        sb.append(super.getMessage());
        sb.append("\n");
        sb.append(String.format("in position %d, line:\n\n", pos));
        sb.append(errLine);
        sb.append("\n");
        sb.append(" ".repeat(pos - 1)).append("^");
        sb.append("\n");

        return sb.toString();
    }

    private int getErrLineStart() {
        char[] cs = src.toCharArray();
        int lineStart = 0;
        for (int i = 0; i < cs.length; i++) {
            if (i == pos) break;
            if (cs[i] == '\n') lineStart = i + 1;
        }

        return lineStart;
    }

    private int getErrLineEnd(int lineStart) {
        var cs = src.toCharArray();
        int lineEnd = lineStart;
        for (int i = lineStart; i < cs.length; i++) {
            lineEnd = i;
        }
        // + 1 for the current position
        return lineEnd + 1;
    }

}
