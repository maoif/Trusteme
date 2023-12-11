package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmFixnum;

public final class TsmFixnumLiteralNode extends TsmNode {

    private final TsmFixnum value;

    public TsmFixnumLiteralNode(TsmFixnum value) {
        this.value = value;
    }

    public TsmFixnumLiteralNode(long value) {
        this.value = new TsmFixnum(value);
    }

    @Override
    public TsmFixnum executeTsmFixnum(VirtualFrame frame) throws UnexpectedResultException {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
