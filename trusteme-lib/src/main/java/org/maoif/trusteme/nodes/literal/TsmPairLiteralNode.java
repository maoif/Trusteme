package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;

public final class TsmPairLiteralNode extends TsmNode {

    private final TsmPair value;

    public TsmPairLiteralNode(TsmPair value) {
        this.value = value;
    }

    public TsmPairLiteralNode(TsmExpr car) {
        this.value = new TsmPair(car);
    }

    public TsmPairLiteralNode(TsmExpr car, TsmExpr cdr) {
        this.value = new TsmPair(car, cdr);
    }

    @Override
    public TsmPair executeTsmPair(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
