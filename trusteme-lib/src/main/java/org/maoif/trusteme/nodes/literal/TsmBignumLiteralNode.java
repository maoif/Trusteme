package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmBignum;

import java.math.BigInteger;

public final class TsmBignumLiteralNode extends TsmNode {

    private final TsmBignum value;

    public TsmBignumLiteralNode(TsmBignum value) {
        this.value = value;
    }

    public TsmBignumLiteralNode(BigInteger value) {
        this.value = new TsmBignum(value);
    }

    @Override
    public TsmBignum executeTsmBignum(VirtualFrame frame) throws UnexpectedResultException {
        return value;
    }

    @Override
    public TsmBignum executeGeneric(VirtualFrame frame) {
        return value;
    }
}
