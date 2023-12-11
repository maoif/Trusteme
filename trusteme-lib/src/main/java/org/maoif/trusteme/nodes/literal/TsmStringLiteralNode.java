package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.strings.TruffleString;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmString;

public final class TsmStringLiteralNode extends TsmNode {

    private final TsmString value;

    public TsmStringLiteralNode(TsmString value) {
        this.value = value;
    }

    public TsmStringLiteralNode(String value) {
        this.value = new TsmString(value);
    }

    @Override
    public TsmString executeTsmString(VirtualFrame frame) {
        return value;
    }

    @Override
    public TsmString executeGeneric(VirtualFrame frame) {
        return value;
    }
}
