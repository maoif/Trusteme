package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "vector")
public class TsmVectorBuiltinNode extends TsmBuiltinNode {
    protected TsmVectorBuiltinNode() {
        super("vector");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
