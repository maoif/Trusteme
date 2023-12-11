package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmVector;

public final class TsmVectorLiteralNode extends TsmNode {
    private final TsmVector value;

    public TsmVectorLiteralNode(TsmVector value) {
        this.value = value;
    }

    @Override
    public TsmVector executeTsmVector(VirtualFrame frame) {
        // TODO
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}