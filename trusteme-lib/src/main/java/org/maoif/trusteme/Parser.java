package org.maoif.trusteme;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlotKind;
import org.maoif.trusteme.nodes.*;

import org.maoif.Reader;
import org.maoif.*;

import org.maoif.trusteme.types.*;

import java.io.File;
import java.util.*;

/**
 * The parser parses the core language as defined by psyntax
 * from the Reader output, and produces TsmNodes.
 */
public class Parser {

    private final TrustemeLanguage language;
    private final Stack<FrameDescriptor.Builder> frameDescriptorBuilders;

    public Parser(TrustemeLanguage language, FrameDescriptor.Builder builder) {
        this.language = language;
        this.frameDescriptorBuilders = new Stack<>();
        this.frameDescriptorBuilders.push(builder);
    }

    public TsmNode parse(File file) {
        return parse(Reader.read(file));
    }

    public TsmNode parse(String str) {
        return parse(Reader.read(str));
    }

    public TsmNode parse(sExpr expr) {
        if (expr instanceof sPair e) {
            TsmNode res = parseSpecialForm(e);
            if (res == null) return parseApplication(e);
            else             return res;
        } else if (expr instanceof sSymbol sym) {
            return new TsmSymbolNode(-1, TsmSymbol.get(sym.get()));
        } else if (expr instanceof sBool e) {
            throw new ParseException("Bool literal must be quoted: %s", expr);
        } else if (expr instanceof sChar e) {
            throw new ParseException("Char literal must be quoted: %s", expr);
        } else if (expr instanceof sString e) {
            throw new ParseException("String literal must be quoted: %s", expr);
        } else if (expr instanceof sFixnum e) {
            throw new ParseException("Fixnum literal must be quoted: %s", expr);
        } else if (expr instanceof sFlonum e) {
            throw new ParseException("Flonum literal must be quoted: %s", expr);
        } else if (expr instanceof sBignum e) {
            throw new ParseException("Bignum literal must be quoted: %s", expr);
        } else if (expr instanceof sVector) {
            throw new ParseException("Vector literal must be quoted: %s", expr);
        } else if (expr instanceof sNull) {
            throw new ParseException("Null literal must be quoted: %s", expr);
        } else throw new ParseException("Unknown expression type: %s", expr);
    }

    private TsmNode parseSpecialForm(sPair p) {
        sExpr head = p.car();
        int len = p.length();
        if (len == 0) throw new ParseException("invalid form: %s", p);

        if (head instanceof sSymbol h) {
            if (symbolEq(h, sSymbol.SYM_DEFINE)) {
                if (len == 3) {
                    sExpr id = list_ref(p, 1);
                    sExpr val = list_ref(p, 2);

                    if (id != null && val != null && id instanceof sSymbol s) {
                        TsmSymbol sym = TsmSymbol.get(s.get());
                        return new TsmDefineNode(frameDescriptorBuilders.peek().addSlots(1, FrameSlotKind.Object),
                                sym, parse(val));
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
                        return new TsmIfNode(parse(test), parse(thenBranch), parse(elseBranch))
                                .setTail();
                    }
                }

                throw new ParseException("Invalid if form: %s", p);
            } else if (symbolEq(h, sSymbol.SYM_BEGIN)) {
                if (len > 1) {
                    if (p.cdr() instanceof sPair pp) {
                        sExpr[] bodyE = list_to_array(pp);
                        TsmNode[] bodyNodes = new TsmNode[bodyE.length];
                        for (int i = 0; i < bodyE.length; i++) {
                            bodyNodes[i] = parse(bodyE[i]);
                        }

                        return new TsmBeginNode(bodyNodes).setTail();
                    }
                }

                throw new ParseException("Invalid begin form; %s", p);
            } else if (symbolEq(h, sSymbol.SYM_SET)) {
                if (len == 3) {
                    sExpr id = list_ref(p, 1);
                    sExpr val = list_ref(p, 2);

                    if (id != null && val != null && id instanceof sSymbol s) {
                        TsmSymbol sym = TsmSymbol.get(s.get());
                        return new TsmSetNode(sym, parse(val));
                    }
                }

                throw new ParseException("Invalid set! form: %s", p);
            } else if (symbolEq(h, sSymbol.SYM_LAMBDA)) {
                if (len >= 3) {
                    sExpr params = list_ref(p, 1);
                    // params could be a single or a list
                    sExpr body = cdr(cdr(p));

                    if (params != null && body != null && body instanceof sPair bs && bs.length() >= 1) {
                        int firstSlot = -1;
                        int slotCount = 0;
                        int dotArgSlot = -1;
                        TsmSymbol[] paramNames = null;
                        var builder = FrameDescriptor.newBuilder();

                        frameDescriptorBuilders.push(builder);

                        // for the 1st lexical scope object
                        int lexicalSlot = builder.addSlots(1, FrameSlotKind.Object);

                        if (params instanceof sSymbol s) {
                            // (lambda args body...)
                            dotArgSlot = builder.addSlots(1, FrameSlotKind.Object);
                            paramNames = new TsmSymbol[1];
                            paramNames[0] = (TsmSymbol) parseQuoted(params);
                        } else if (params instanceof sPair pp) {
                            // (lambda (args ...) body...)
                            if (isImproper(pp)) {
                                // (lambda (a b c . d) ...)
                                sExpr[] ppp = improper_list_to_array(pp);
                                if (!allSymbols(ppp))
                                    throw new ParseException("Invalid lambda arguments: %s", p);

                                paramNames = new TsmSymbol[ppp.length];
                                for (int i = 0; i < ppp.length; i++)
                                    paramNames[i] = (TsmSymbol) parseQuoted(ppp[i]);

                                slotCount = pp.improperLength();
                                firstSlot = builder.addSlots(slotCount, FrameSlotKind.Object);
                                dotArgSlot = builder.addSlots(1, FrameSlotKind.Object);
                            } else {
                                // (lambda (a b c) ...)
                                sExpr[] ppp = list_to_array(pp);
                                if (!allSymbols(ppp))
                                    throw new ParseException("Invalid lambda arguments: %s", p);

                                slotCount = ppp.length;
                                if (slotCount > 0) {
                                    firstSlot = builder.addSlots(slotCount, FrameSlotKind.Object);

                                    paramNames = new TsmSymbol[ppp.length];
                                    for (int i = 0; i < ppp.length; i++)
                                        paramNames[i] = (TsmSymbol) parseQuoted(ppp[i]);
                                }
                            }
                        }



                        sExpr[] bodyE = list_to_array(bs);
                        TsmNode[] bodyNodes = new TsmNode[bodyE.length];
                        for (int i = 0; i < bodyE.length; i++) {
                            bodyNodes[i] = parse(bodyE[i]);
                        }
                        bodyNodes[bodyNodes.length - 1].setTail();

                        // TODO passing `language` arg will result in exception
                        TsmRootNode root = TsmRootNode.create(null, frameDescriptorBuilders.peek().build(),
                                lexicalSlot, firstSlot, slotCount, dotArgSlot, paramNames, bodyNodes);

                        frameDescriptorBuilders.pop();

                        return new TsmLambdaNode(new TsmProcedure(root.getCallTarget()));
                    }
                }

                throw new ParseException("Invalid lambda form: %s", p);
            } else if (symbolEq(h, sSymbol.SYM_LETREC)) {
                if (len >= 3) {
                    /*
                     * (letrec ([v0 e0] [v1 e1] ...) body*) ->
                     * ((lambda (x0 x1 ...)
                     *   (begin
                     *     ;; checkNodes
                     *     ;; install error detection code in case v's are used in initializing the bindings
                     *     (set! v0 (lambda () (undefined-var 'v0)))
                     *     ...
                     *     ;; initNodes
                     *     (set! v0 e0)
                     *     (set! v1 e1)
                     *     body*))
                     *
                     * letrec uses a new lexical scope to store the recursive bindings,
                     * to have a new scope, we generate a dummy lambda application.
                     */

                    List<TsmNode> checkNodes = new ArrayList<>();
                    List<TsmNode> initNodes = new ArrayList<>();
                    List<TsmNode> bodyNodes = new ArrayList<>();
                    List<sSymbol> sNames = new ArrayList<>();

                    sExpr bs = p.cdr();
                    if (bs instanceof sPair bindingsAndBody) {
                        if (car(bindingsAndBody) instanceof sPair bindings) {
                            var builder = FrameDescriptor.newBuilder();
                            frameDescriptorBuilders.push(builder);

                            // disallow empty bindings
                            if (bindings.length() > 0) {
                                // process each binding
                                sExpr bds = bindings;

                                while (!(bds instanceof sNull)) {
                                    sExpr binding = car(bds);
                                    sExpr v = car(binding);
                                    sExpr e = car(cdr(binding));

                                    if (v instanceof sSymbol lhs) {
                                        sNames.add(lhs);

                                        var errorNode = new TsmAppNode(
                                                new TsmSymbolNode(-1, TsmSymbol.get("undefined-var")),
                                                new TsmNode[]{ new TsmQuoteNode(TsmSymbol.get(lhs.get())) });

                                        var checkNode = new TsmSetNode(TsmSymbol.get(lhs.get()),
                                                buildLambdaNode(FrameDescriptor.newBuilder(),
                                                        new ArrayList<>(), new TsmNode[]{ errorNode }));
                                        checkNodes.add(checkNode);

                                        // generate (set! v0 e0)
                                        var rhs = parse(e);
                                        var initNode = new TsmSetNode(TsmSymbol.get(lhs.get()), rhs);

                                        initNodes.add(initNode);
                                    } else {
                                        throw new ParseException("Invalid identifier: %s", v);
                                    }

                                    bds = cdr(bds);
                                }

                                // handle body
                                if (cdr(bindingsAndBody) instanceof sPair body) {
                                    sExpr expr = body;
                                    while (!(expr instanceof sNull)) {
                                        bodyNodes.add(parse(car(expr)));
                                        expr = cdr(expr);
                                    }
                                }

                                checkNodes.addAll(initNodes);
                                checkNodes.addAll(bodyNodes);
                                checkNodes.get(checkNodes.size() - 1).setTail();
                                var dummyLambda =  buildLambdaNode(builder, sNames, checkNodes.toArray(new TsmNode[0]));

                                List<TsmQuoteNode> dummyArgs = sNames.stream()
                                        .map(n -> new TsmQuoteNode(TsmBool.FALSE))
                                        .toList();

                                frameDescriptorBuilders.pop();

                                return new TsmAppNode(dummyLambda, dummyArgs.toArray(new TsmNode[0]));
                            }
                        }
                    }
                }

                throw new ParseException("Invalid letrec form: %s", p);
            }
        }

        return null;
    }

    private TsmLambdaNode buildLambdaNode(FrameDescriptor.Builder builder, List<sSymbol> names, TsmNode[] body) {
        if (body.length == 0) {
            throw new ParseException("Empty body not allowed");
        }

        int lexicalSlot = builder.addSlots(1, FrameSlotKind.Object);
        int firstSlot   = builder.addSlots(names.size(), FrameSlotKind.Object);
        TsmSymbol[] paramNames = new TsmSymbol[names.size()];
        for (int i = 0; i < names.size(); i++) {
            paramNames[i] = TsmSymbol.get(names.get(i).get());
        }

        var root = TsmRootNode.create(null, builder.build(),
                lexicalSlot, firstSlot, names.size(), -1, paramNames, body);

        return new TsmLambdaNode(new TsmProcedure(root.getCallTarget()));
    }

    public static TsmExpr parseQuoted(sExpr expr) {
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
            return TsmSymbol.get(e.get());
        } else if (expr instanceof sNull) {
            return TsmNull.INSTANCE;
        } else if (expr instanceof sEof) {
            return TsmEof.INSTANCE;
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

        var operator = parse(car(p));
        if (p.length() == 1)
            return new TsmAppNode(operator);
        else {
            sExpr args = p.cdr();
            if (args instanceof sPair as) {
                sExpr[] rands = list_to_array(as);
                TsmNode[] operands = new TsmNode[rands.length];
                for (int i = 0; i < rands.length; i++) {
                    operands[i] = parse(rands[i]);
                }

                return new TsmAppNode(operator, operands);
            } else throw new ParseException("Invalid call expression: %s", p);
        }
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
            res = cdr(res);
            if (res == null) return null;
        }

        return car(res);
    }

    private static sExpr[] list_to_array(sPair p) {
        if (p.car() == sNull.INSTANCE) return new sExpr[0];

        List<sExpr> res = new LinkedList<>();
        res.add(p.car());

        sExpr cdr = p.cdr();
        while (!(cdr instanceof sNull)) {
            if (cdr instanceof sPair pp) {
                res.add(pp.car());
                cdr = pp.cdr();
            } else throw new ParseException("improper list not allowed");
        }

        return res.toArray(new sExpr[0]);
    }

    private static sExpr[] improper_list_to_array(sPair p) {
        if (p.car() == sNull.INSTANCE)
            throw new ParseException("improper list should at least have 2 items");

        List<sExpr> res = new LinkedList<>();
        res.add(p.car());

        sExpr cdr = p.cdr();
        while (!(cdr instanceof sNull)) {
            if (cdr instanceof sPair pp) {
                res.add(pp.car());
                cdr = pp.cdr();
            } else {
                res.add(cdr);
                break;
            }
        }

        return res.toArray(new sExpr[0]);
    }

    private static boolean isImproper(sPair p) {
        sExpr cdr = p.cdr();
        while (true) {
            if (cdr instanceof sPair pp) cdr = pp.cdr();
            else if (cdr instanceof sNull) break;
            else return true;
        }

        return false;
    }

    private static boolean allSymbols(sExpr[] es) {
        for (var e : es)
            if (!(e instanceof sSymbol)) return false;
        return true;
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
