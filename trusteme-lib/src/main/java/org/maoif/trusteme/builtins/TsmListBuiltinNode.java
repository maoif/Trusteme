package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;

public class TsmListBuiltinNode extends TsmBuiltinNode {
    protected TsmListBuiltinNode() {
        super("list");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
