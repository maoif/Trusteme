package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmChar;

public final class TsmCharLiteralNode extends TsmNode {

    private final TsmChar value;

    public TsmCharLiteralNode(TsmChar value) {
        this.value = value;
    }

    public TsmCharLiteralNode(char value) {
        this.value = new TsmChar(value);
    }

    @Override
    public TsmChar executeTsmChar(VirtualFrame frame) {
        return value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
