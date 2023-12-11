package org.maoif.trusteme;

import org.maoif.trusteme.nodes.TsmIfNode;
import org.maoif.trusteme.nodes.TsmNode;

import org.maoif.Reader;
import org.maoif.*;

import org.maoif.trusteme.nodes.TsmQuoteNode;
import org.maoif.trusteme.nodes.literal.*;
import org.maoif.trusteme.types.*;

import java.io.File;
import java.util.Optional;

/**
 * The parser parses the core language as defined by psyntax
 * from the Reader output, and produces TsmNodes.
 */
public class Parser {

    public TsmNode parse(File file) {
        return parse(Reader.read(file));
    }

    public TsmNode parse(String str) {
        return parse(Reader.read(str));
    }

    private TsmNode parse(sExpr expr) {
        // the following up to TsmSymbolLiteralNode may be unnecessary
        // because psyntax wraps all literals in quotes
        if (expr instanceof sBool e) {
            return new TsmBoolLiteralNode(e.get());
        } else if (expr instanceof sChar e) {
            return new TsmCharLiteralNode(e.get());
        } else if (expr instanceof sString e) {
            return new TsmStringLiteralNode(e.get());
        } else if (expr instanceof sFixnum e) {
            return new TsmFixnumLiteralNode(e.get());
        } else if (expr instanceof sFlonum e) {
            return new TsmFlonumLiteralNode(e.get());
        } else if (expr instanceof sBignum e) {
            return new TsmBignumLiteralNode(e.get());
        } else if (expr instanceof sSymbol e) {
            return new TsmSymbolLiteralNode(e.get());
        } else if (expr instanceof sNull) {
            return new TsmNullLiteralNode();
        } else if (expr instanceof sPair e) {
            TsmNode res = parseSpecialForm(e);
            if (res == null) return parseApplication(e);
            else             return res;
        } else if (expr instanceof sVector) {
            throw new ParseException("Vector literal must be quoted: %s", expr);
        } else
            throw new ParseException("Unknown expression type: %s", expr);
    }

    private TsmNode parseSpecialForm(sPair p) {
        sExpr head = p.car();
        int len = p.length();

        if (head instanceof sSymbol h) {
            if (symbolEq(h, sSymbol.SYM_DEFINE)) {
                if (len == 3) {
                    sExpr id = list_ref(p, 1);
                    sExpr val = list_ref(p, 2);

                    if (id != null && val != null) {

                    }
                }

                throw new ParseException("Invalid define form: %s", p);
            } else if (symbolEq(h, sSymbol.SYM_QUOTE)) {
                if (len == 2) {
                    sExpr datum = list_ref(p, 1);
                    if (datum != null)
                        return new TsmQuoteNode(parseQuoted(datum));
                }

                throw new ParseException("Invalid quote form: %s", p);
            } else if (symbolEq(h, sSymbol.SYM_IF)) {
                if (len == 4) {
                    sExpr test = list_ref(p, 1);
                    sExpr thenBranch = list_ref(p, 2);
                    sExpr elseBranch = list_ref(p, 3);

                    if (test != null && thenBranch != null && elseBranch != null) {
                        return new TsmIfNode(parse(test), parse(thenBranch), parse(elseBranch));
                    }
                }

                throw new ParseException("Invalid if form: %s", p);
            } else if (symbolEq(h, sSymbol.SYM_BEGIN)) {
                if (len > 1) {
                    throw new UnsupportedOperationException();
                }

                throw new ParseException(
                        "Invalid begin form: %s, at least one expression is required", p);
            } else if (symbolEq(h, sSymbol.SYM_SET)) {
                if (len == 3) {
                    sExpr id = list_ref(p, 1);
                    sExpr val = list_ref(p, 2);

                    if (id != null && val != null) {

                    }
                }

                throw new ParseException("Invalid set! form: %s", p);
            } else if (symbolEq(h, sSymbol.SYM_LAMBDA)) {
                if (len >= 3) {
                    // TODO support dotted arg?
                    sExpr params = list_ref(p, 1);
                    // params could be a single or a list
                    sExpr body = cdr(cdr(p));

                    throw new UnsupportedOperationException();
                }

                throw new ParseException("Invalid lambda form: %s", p);
            } else if (symbolEq(h, sSymbol.SYM_LETREC)) {
                // syntax seems to only produce letrecs of one binding,
                // but we support none and multiple bindings anyway
                if (len >= 3) {
                    sExpr bs = p.cdr();
                    if (bs instanceof sPair bindings) {
                        throw new UnsupportedOperationException();
                    }
                }

                throw new ParseException("Invalid letrec form: %s", p);
            }
        }

        return null;
    }

    private TsmExpr parseQuoted(sExpr expr) {
        if (expr instanceof sBool e) {
            return e.get() ? TsmBool.TRUE : TsmBool.FALSE;
        } else if (expr instanceof sChar e) {
            return new TsmChar(e.get());
        } else if (expr instanceof sString e) {
            return new TsmString(e.get());
        } else if (expr instanceof sFixnum e) {
            return new TsmFixnum(e.get());
        } else if (expr instanceof sFlonum e) {
            return new TsmFlonum(e.get());
        } else if (expr instanceof sBignum e) {
            return new TsmBignum(e.get());
        } else if (expr instanceof sSymbol e) {
            return new TsmSymbol(e.get());
        } else if (expr instanceof sNull) {
            return TsmNull.INSTANCE;
        } else if (expr instanceof sPair e) {
            return new TsmPair(parseQuoted(e.car()), parseQuoted(e.cdr()));
        } else if (expr instanceof sVector e) {
            var vals = e.get();
            TsmExpr[] res = new TsmExpr[vals.length];
            for (int i = 0; i < vals.length; i++) {
                res[i] = parseQuoted(vals[i]);
            }

            return new TsmVector(res);
        } else
            throw new ParseException("Unknown expression type: %s", expr);
    }

    private TsmNode parseApplication(sPair p) {
        // (op)                         -> (SymbolLiteralNode)
        // (op arg ...)                 -> (SymbolLiteralNode TsmNode ...)
        // ((op) arg ...)               -> (TsmAppNode TsmNode ...)
        // ((lambda (...) ...) arg ...) -> (LambdaLiteralNode TsmNode ...)
        // ((((op))) arg ...)           -> (TsmAppNode(TsmAppNode(TsmAppNode)) TsmNode ...)


        return null;
    }

    //=======================================================

    private static boolean symbolEq(sSymbol e1, sSymbol e2) {
        return e1.get().equals(e2.get());
    }

    private static boolean symbolEq(sExpr e1, sExpr e2) {
        if (e1 instanceof sSymbol s1 && e2 instanceof sSymbol s2)
            return s1.get().equals(s2.get());
        else return false;
    }

    private static sExpr car(sExpr e) {
        if (e instanceof sPair p) {
            return p.car();
        } return null;
    }

    private static sExpr cdr(sExpr e) {
        if (e instanceof sPair p) {
            return p.cdr();
        } return null;
    }

    private static sExpr list_ref(sExpr e, int i) {
        if (i == 0) return car(e);

        sExpr res = e;
        for (int j = i; j > 0; j--) {
            res = cdr(e);
            if (res == null) return null;
        }

        return car(res);
    }

    private static sExpr list_to_array(sExpr p) {
        return null;
    }


    private static class ParseException extends RuntimeException {

        public ParseException(String errMsg) {
            super(errMsg);
        }

        public ParseException(String format, Object... args) {
            super(String.format(format, args));
        }

    }

}
