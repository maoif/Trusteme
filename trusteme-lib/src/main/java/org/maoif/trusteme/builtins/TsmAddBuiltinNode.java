package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmBignum;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmFlonum;

import java.math.BigInteger;

@NodeInfo(shortName = "+")
//@NodeChild("left")
//@NodeChild("right")
public class TsmAddBuiltinNode extends TsmBuiltinNode {


//    @Children
//    private final TsmNode[] valueNodes;
//
//    public TsmAddBuiltinNode(TsmNode[] values) {
//        this.valueNodes = values;
//    }
    public TsmAddBuiltinNode() {
        super("+");
    }

    // TODO optimize
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // can we do this directly?
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length == 1)
            return new TsmFixnum(0);

        var res = compute(1, 0, args);
        System.out.println("TsmAddBuiltinNode.executeGeneric: " + res.toString());
        return res;
    }

    private static TsmExpr compute(int index, long value, Object[] args) {
        for (int i = index; i < args.length; i++) {
            if (args[i] instanceof TsmFixnum fix)
                try {
                    value = Math.addExact(value, fix.get());
                } catch (ArithmeticException e) {
                    return compute(i, new BigInteger(String.valueOf(value)), args);
                }
            else if (args[i] instanceof TsmFlonum flo) {
                return compute(i, (double) value, args);
            } else if (args[i] instanceof TsmBignum big) {
                return compute(i, new BigInteger(String.valueOf(value)), args);
            } else throw new RuntimeException("Bad argument type");
        }

        return new TsmFixnum(value);
    }

    private static TsmFlonum compute(int index, double value, Object[] args) {
        for (int i = index; i < args.length; i++) {
            if (args[i] instanceof TsmFixnum fix) {
                value += fix.get();
            } else if (args[i] instanceof TsmFlonum flo) {
                value += flo.get();
            } else throw new RuntimeException("Bad argument type");
        }

        return new TsmFlonum(value);
    }

    private static TsmBignum compute(int index, BigInteger value, Object[] args) {
        BigInteger res = value;
        for (int i = index; i < args.length; i++) {
            if (args[i] instanceof TsmFixnum fix) {
                res = res.add(new BigInteger(String.valueOf(fix.get())));
            } else if (args[i] instanceof TsmBignum big) {
                res = res.add(big.get());
            } else throw new RuntimeException("Bad argument type");
        }

        return new TsmBignum(value);
    }

//    @Specialization(rewriteOn = ArithmeticException.class)
//    protected TsmFixnum doLong(TsmFixnum left, TsmFixnum right) {
//        // (+ '1 '2 '3 ...)
//        return new TsmFixnum(Math.addExact(left.get(), right.get()));
//    }
//
//    @Specialization
//    @CompilerDirectives.TruffleBoundary
//    protected TsmBignum doSLBigInteger(TsmBignum left, TsmBignum right) {
//        return new TsmBignum(left.get().add(right.get()));
//    }
//
//    // need this since the specializations are not complete
//    @Fallback
//    protected Object typeError(Object left, Object right) {
//        throw new RuntimeException("type error");
//    }
}
