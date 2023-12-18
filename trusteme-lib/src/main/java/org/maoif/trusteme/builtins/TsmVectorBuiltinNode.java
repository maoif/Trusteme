package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;

public class TsmVectorBuiltinNode extends TsmBuiltinNode {
    protected TsmVectorBuiltinNode() {
        super("vector");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
