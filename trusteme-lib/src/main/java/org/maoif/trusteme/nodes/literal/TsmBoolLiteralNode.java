package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmBool;

public final class TsmBoolLiteralNode extends TsmNode {

    private final TsmBool value;

    public TsmBoolLiteralNode(TsmBool value) {
        this.value = value;
    }

    public TsmBoolLiteralNode(boolean value) {
        this.value = value ? TsmBool.TRUE : TsmBool.FALSE;
    }

    @Override
    public TsmBool executeTsmBool(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
