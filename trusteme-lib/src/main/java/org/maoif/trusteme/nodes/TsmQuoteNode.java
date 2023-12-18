package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.types.*;

/**
 * Quote expressions just return the quoted data.
 */
public class TsmQuoteNode extends TsmNode {
//    public static enum QuoteKind {
//        BOOL,
//        CHAR,
//        STRING,
//        FIXNUM,
//        FLONUM,
//        BIGNUM,
//        PAIR,
//        VECTOR
//    }
//
//    protected abstract QuoteKind getKind();

    private final TsmExpr value;

    public TsmQuoteNode(TsmExpr val) {
        this.value = val;
    }

//    @Specialization(guards = "isFixnum")
//    protected TsmFixnum quoteFixnum(VirtualFrame frame, TsmFixnum value) {
//        return value;
//    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }

//    protected boolean isFixnum() {
//        return this.getKind() == QuoteKind.FIXNUM;
//    }


    @Override
    public String toString() {
        return String.format("(TsmQuoteNode %s)", value);
    }
}
