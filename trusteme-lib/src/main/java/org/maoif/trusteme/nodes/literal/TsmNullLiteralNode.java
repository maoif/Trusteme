package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmNull;

public class TsmNullLiteralNode extends TsmNode {

    @Override
    public TsmNull executeTsmNull(VirtualFrame frame) {
        return TsmNull.INSTANCE;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return TsmNull.INSTANCE;
    }
}
