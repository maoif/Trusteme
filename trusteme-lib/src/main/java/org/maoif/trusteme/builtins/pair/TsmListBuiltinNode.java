package org.maoif.trusteme.builtins.pair;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.builtins.TsmBuiltinNode;

public class TsmListBuiltinNode extends TsmBuiltinNode {
    protected TsmListBuiltinNode() {
        super("list");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
