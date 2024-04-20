package org.maoif;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.*;

/**
 * r6rs compatible Scheme reader
 *
 * TODO line comments, block comments and expr comments
 * TODO decimal point in any radix
 * TODO nan.0
 * TODO hex value char
 * TODO \x<hex>; in string
 * TODO ... as in macros
 * TODO number exactness: #i, #e, ...
 * TODO complex number, rational number
 * TODO box, eof, bytevector, graph
 */
public class Reader {

    /**
     * Read one datum from input file.
     * @param file input source file
     * @return one datum read from input file
     */
    public static sExpr read(File file) {
        try {
            return read(Files.readString(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Read one datum from input string.
     * @param str input string
     * @return one datum read from input string
     */
    public static sExpr read(String str) {
        return read(new SourceBuffer(str));
    }

    /**
     * Read all data in a string.
     * @param str input string
     * @return a list of data read
     */
    public static List<sExpr> readAll(String str) {
        var src = new SourceBuffer(str);
        List<sExpr> res = new LinkedList<>();
        sExpr e = read(src);
        while (!(e instanceof sEof)) {
            res.add(e);
            e = read(src);
        }

        return res;
    }

    public static Object[] readWithNextPosition(char[] cs, int pos) {
        var src = new SourceBuffer(cs, pos);
        var res = read(src);
        var next = src.getPos();
        return new Object[] {res, next};
    }

    /**
     * Read all data from a file.
     * @param file input source file
     * @return a list of data read
     */
    public static List<sExpr> readAll(File file) {
        try {
            String str = Files.readString(file.toPath());
            return readAll(str);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from " + file);
        }
    }


    /**
     * Read one datum from the SourceBuffer.
     * After reading, the position of the SourceBuffer points to
     * the next character in the buffer.
     * @param src SourceBuffer
     * @return sExpr read from the buffer
     */
    private static sExpr read(SourceBuffer src) {
        src.skipWhitespaces();

        if (src.peek(';')) {
            do {
                do {
                    src.read();
                } while (src.peek() != '\n' && src.peek() != (char) -1);

                src.skipWhitespaces();

            } while (src.peek(';'));
        }

        // TODO maybe put the #-prefixed ifs together
        // TODO where to advance()?
        // TODO where to handle the exception?

        try {
            if (src.peek('(') || src.peek('[')) {
                return readPair(src, src.peek() == '(' ? ')' : ']');
            } else if (src.peek("#t") || src.peek("#T")) {
                return new sBool(src.getSource(), src.getPos(), src.advance(2), true);
            } else if (src.peek("#f") || src.peek("#F")) {
                return new sBool(src.getSource(), src.getPos(), src.advance(2), false);
            } else if (src.peek("#\\")) {
                return readChar(src);
            } else if (src.peek("#(")) {
                return readVector(src);
            } else if (src.peek("'")) {
                return makeQuoted(src, "'", "quote");
            } else if (src.peek("`")) {
                return makeQuoted(src, "`", "quasiquote");
            } else if (src.peek(",@")) {
                return makeQuoted(src, ",@", "unquote-splicing");
            } else if (src.peek(",")) {
                return makeQuoted(src, ",", "unquote");
            } else if (src.peek("#'")) {
                return makeQuoted(src, "#'", "syntax");
            } else if (src.peek("#`")) {
                return makeQuoted(src, "#`", "quasisyntax");
            } else if (src.peek("#,@")) {
                return makeQuoted(src, "#,@", "unsyntax-splicing");
            } else if (src.peek("#,")) {
                return makeQuoted(src, "#,", "unsyntax");
            } else if (src.peek("\"")) {
                return readString(src);
            } else if (isSymbolStart(src.peek())) {
                char maybeSign = src.read();
                if ((maybeSign == '+' || maybeSign == '-') && (Character.isDigit(src.peek()))) {
                    src.unread();
                    return readNumber(src);
                }

                src.unread();
                return readSymbol(src);
            } else if (Character.isDigit(src.peek()) || src.peek("#d") || src.peek("#D")) {
                return readNumber(src);
            } else if (src.peek("#b") || src.peek("#B")) {
                return readNumber(src);
            } else if (src.peek("#o") || src.peek("#O")) {
                return readNumber(src);
            } else if (src.peek("#x") || src.peek("#X")) {
                return readNumber(src);
            } else if (src.peek() == (char) -1 || src.peek("#!eof")) {
                return new sEof(src.getSource(), src.getPos());
            } else {
                throw new LexicalException(src.getSource(), src.getPos(),
                        "Unknown token: " + src.peek());
            }
        } catch (LexicalException le) {
            le.printStackTrace();
            // What to do here?
            return null;
        }
    }

    private static sChar readChar(SourceBuffer src) throws LexicalException {
        sChar c = new sChar(src.getSource());
        c.setPosStart(src.getPos());
        // skip "#\"
        src.advance(2);

        // handle special chars
        for (var kv : specialCharsMap.entrySet()) {
            if (src.peek(kv.getKey())) {
                c.set(kv.getValue());
                c.setPosEnd(src.advance(kv.getKey().length()));

                char cc = src.peek();
                if (cc == (char) -1) return c;
                // allow things like #\A(), (#\A)
                if (!Character.isWhitespace(cc) && !(cc == ')' || cc == ']' || cc == '(' || cc == '['))
                    throw new LexicalException(src.getSource(), src.getPos(), "bad token after char");

                return c;
            }
        }

        c.set(src.read());
        c.setPosEnd(src.getPos());
        char cc = src.peek();
        if (cc == (char) -1) return c;
        if (!Character.isWhitespace(cc) && !(cc == ')' || cc == ']' || cc == '(' || cc == '['))
            throw new LexicalException(src.getSource(), src.getPos(), "bad token after char");

        return c;
    }

    private static sString readString(SourceBuffer src) throws LexicalException {
        sString str = new sString(src.getSource());
        str.setPosStart(src.getPos());
        // skip '"'
        src.advance(1);

        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = src.read();

            if (c == (char) -1) {
                throw new LexicalException(src.getSource(), src.getPos(),
                        "no matching right quote when reading string");
            }

            // TODO maybe allow them
            if (c == '\r') {
                throw new LexicalException(src.getSource(), src.getPos(),
                        "return (\\r) not allowed in string");
            }

            if (c == '\n') {
                throw new LexicalException(src.getSource(), src.getPos(),
                        "linefeed (\\n) not allowed in string");
            }

            // escaped
            if (c == '\\') {
                char cc = src.read();

                if (cc == (char) -1) {
                    throw new LexicalException(src.getSource(), src.getPos(),
                            "");
                }

                switch (cc) {
                    case '\\' -> sb.append('\\');
                    case 't' ->  sb.append('\t');
                    case 'n' ->  sb.append('\n');
                    case 'v' ->  sb.append('\u000b');
                    case '"' ->  sb.append('"');
                    case 'f' ->  sb.append('\f');
                    case 'a' ->  sb.append('\u0007');
                    case 'b' ->  sb.append('\b');
                    default -> throw new LexicalException(src.getSource(), src.getPos(),
                            "unknown escaped sequence: \\%s", cc);
                }

                continue;
            }

            if (c == '"') break;

            sb.append(c);
        }

        str.set(sb.toString());
        str.setPosEnd(src.getPos());

        return str;
    }

    private static sSymbol readSymbol(SourceBuffer src) throws LexicalException {
        sSymbol sym = new sSymbol(src.getSource());
        sym.setPosStart(src.getPos());

        StringBuilder sb = new StringBuilder();
        while (true) {
            // use peek() in case we have things like (x)
            char c = src.peek();
            if (isSymbolChar(c)) sb.append(src.read());
            else break;
        }
        sym.setPosEnd(src.getPos());
        sym.set(sb.toString());

        return sym;
    }

    private static sNumber readNumber(SourceBuffer src) throws LexicalException {
        int posStart = src.getPos();
        boolean neg = false;
        int radix = 10;
        // digits, whitespaces (stop), decimal point, others (error)

        char c = src.peek();
        if (c == '-') {
            neg = true;
            src.advance(1);
        } else if (c == '+') {
            src.advance(1);
        } else if (src.peek('#')) {
            src.advance(1);
            switch (src.peek()) {
                case 'b', 'B' -> {
                    radix = 2;
                    src.advance(1);
                }
                case 'o', 'O' -> {
                    radix = 8;
                    src.advance(1);
                }
                case 'd', 'D' -> {
                    src.advance(1);
                }
                case 'x', 'X' -> {
                    radix = 16;
                    src.advance(1);
                }
                default -> throw new LexicalException(src.getSource(), src.getPos(),
                        "bad radix");
            }

            if (src.peek('+')) {
                src.advance(1);
            } else if (src.peek('-')) {
                neg = true;
                src.advance(1);
            }
        }

        // TODO maybe detect decimal point first
        String res = readNumberString(src, neg, radix);
        try {
            long val = Long.parseLong(res, radix);
            sFixnum num = new sFixnum(src.getSource());
            num.set(val);
            num.setPosStart(posStart);
            num.setPosEnd(src.getPos());

            return num;
        } catch (NumberFormatException nfe) {
            try {
                // TODO handle radix other than 10
                if (radix == 10) {
                    double val = Double.parseDouble(res);
                    sFlonum num = new sFlonum(src.getSource());
                    num.set(val);
                    num.setPosStart(posStart);
                    num.setPosEnd(src.getPos());

                    return num;
                }

                return null;
            } catch (NumberFormatException nfe1) {
                try {
                    BigInteger val = new BigInteger(res, radix);
                    sBignum num = new sBignum(src.getSource());
                    num.set(val);
                    num.setPosStart(posStart);
                    num.setPosEnd(src.getPos());

                    return num;
                } catch (NumberFormatException nfe2) {
                    throw new LexicalException(src.getSource(), src.getPos(),
                            "failed to parse number");
                }
            }
        }
    }

    private static String readNumberString(SourceBuffer src, boolean neg, int radix) throws LexicalException {
        StringBuilder sb = new StringBuilder();
        boolean gotDecimalPoint = false;

        if (neg) sb.append('-');

        while (true) {
            char c = src.peek();
            if (Character.digit(c, radix) != -1) {
                sb.append(c);
                src.advance(1);
            } else if (c == '.') {
                if (!gotDecimalPoint) {
                    gotDecimalPoint = true;
                    sb.append('.');
                    src.advance(1);
                }
                else throw new LexicalException(src.getSource(), src.getPos(),
                        "got two decimal points in number string");
            } else if (Character.isWhitespace(c)) {
                // so skipWhitespaces() will work
                src.advance(1);
                break;
            } else if (c == (char) -1) {
                break;
            } else if (c == ')' || c == ']' || c == '(' || c == '[') {
                // allow things like (123), '(123())
                break;
            } else throw new LexicalException(src.getSource(), src.getPos(),
                    "bad number string");
        }

        return sb.toString();
    }


    private static sPair readPair(SourceBuffer src, char rightCloser) throws LexicalException {
        sPair result = new sPair(src.getSource());
        result.setPosStart(src.getPos());

        // skip '('
        src.advance(1);

        // used for building the next list element
        sPair p = result;

        while (true) {
            src.skipWhitespaces();

            int oldPos = src.getPos();
            char c = src.read();
            // Peek a whitespace so "..." can be read correctly.
            if (c == '.' && Character.isWhitespace(src.peek())) {
                if (p.car() == sNull.INSTANCE)
                    throw new LexicalException(src.getSource(), oldPos,
                            "at least one item is required before the dot");

                src.skipWhitespaces();
                sExpr ee = read(src);
                src.skipWhitespaces();

                // now must be list end
                if (src.read() == rightCloser) {
                    result.setPosEnd(src.getPos());
                    p.setCdr(ee);

                    break;
                } else {
                    throw new LexicalException(src.getSource(), src.getPos(),
                            "more than one datum after dot");
                }
            } else if (c == rightCloser) {
                // TODO maybe return sNull?
                result.setPosEnd(oldPos);
                break;
            } else if (c == (char) -1) {
                throw new LexicalException(src.getSource(), oldPos,
                        "incomplete list");
            } else if (c == (rightCloser == ')' ? ']' : ')')) {
                throw new LexicalException(src.getSource(), oldPos,
                        "bad right closer for list");
            } else {
                src.unread();
                sExpr e = read(src);
                if (e instanceof sEof)
                    throw new LexicalException(src.getSource(), src.getPos(),
                            "expected right closer for list");

                if (p.car() == sNull.INSTANCE) {
                    p.setCar(e);
                } else {
                    p.setCdr(new sPair());
                    p = (sPair) p.cdr();
                    p.setCar(e);
                }
            }
        }

        return result;
    }

    private static sVector readVector(SourceBuffer src) throws LexicalException {
        sVector vec = new sVector(src.getSource());
        vec.setPosStart(src.getPos());

        // skip "#("
        src.advance(2);

        List<sExpr> es = new LinkedList<>();
        while (true) {
            src.skipWhitespaces();

            if (src.peek(')')) {
                vec.setPosEnd(src.advance(1));
                vec.set(es.toArray(new sExpr[0]));

                return vec;
            } else if (src.peek('.')) {
                throw new LexicalException(src.getSource(), src.getPos(),
                        "dot not allowed in vector");
            } else {
                var e = read(src);
                if (e instanceof sEof)
                    throw new LexicalException(src.getSource(), src.getPos(),
                            "expected right closer for vector");

                es.add(e);
            }
        }
    }

    private static sPair makeQuoted(SourceBuffer src, String abbrev, String quoteType) {
        // result = (quoteType . p) = (quoteType . (<datum> . sNull))
        // p = (<datum> . sNull)
        sPair result = new sPair(src.getSource());
        result.setPosStart(src.getPos());

        sSymbol sym = new sSymbol(src.getSource(), quoteType);
        sym.setPosStart(src.getPos());
        sym.setPosEnd(src.advance(abbrev.length()));

        result.setCar(sym);

        // should we allow whitespace after the quote?
        src.skipWhitespaces();

        sPair p = new sPair(src.getSource());
        p.setPosStart(src.getPos());

        sExpr e = read(src);

        p.setCar(e);
        result.setCdr(p);
        result.setPosEnd(src.getPos());

        return result;
    }

    private static Set<String> specialChars = Set.of(
            "nul", "alarm", "backspace", "tab", "linefeed", "newline", "vtab",
            "page", "return", "esc", "space", "delete");

    private static Map<String, Character> specialCharsMap = new HashMap<>(Map.of(
            "nul", '\0', "alarm", '\u0007', "backspace", '\b', "tab", '\t',
            "linefeed", '\n', "newline", '\n', "vtab", '\u000b',
            "page", '\u000c', "return", '\r',  "esc", '\u001b'));

    static {
        specialCharsMap.putAll(Map.of("space", ' ', "delete", '\u007f'));
    }

    private static Set<Character> symbolChars = Set.of(
            '!', '$', '%', '&', '*','/', ':', '<', '=', '>',
            '?', '^', '_', '~', '+', '-', '.', '@');

    private static Set<Character> escapedChars = Set.of(
            'a', 'b', 't', 'n', 'v', 'f', 'r', '"', '\\');

    private static Boolean oneOf(Set<Character> charSet, char c) {
        return charSet.contains(c);
    }

    private static Boolean isSymbolStart(char c) {
        return Character.isAlphabetic(c) || oneOf(symbolChars, c);
    }

    private static Boolean isSymbolChar(char c) {
        return Character.isAlphabetic(c) || oneOf(symbolChars, c) || Character.isDigit(c);
    }

}
