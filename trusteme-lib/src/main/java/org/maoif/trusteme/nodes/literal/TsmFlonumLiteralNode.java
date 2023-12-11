package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmFlonum;

public final class TsmFlonumLiteralNode extends TsmNode {

    private final TsmFlonum value;

    public TsmFlonumLiteralNode(TsmFlonum value) {
        this.value = value;
    }

//    @Override
//    public TsmFlonum executeDouble(VirtualFrame frame) throws UnexpectedResultException {
//        return value;
//    }

    public TsmFlonumLiteralNode(double value) {
        this.value = new TsmFlonum(value);
    }

    @Override
    public TsmFlonum executeTsmFlonum(VirtualFrame frame) throws UnexpectedResultException {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}