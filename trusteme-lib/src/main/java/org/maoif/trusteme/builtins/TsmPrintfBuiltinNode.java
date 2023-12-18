package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;

public class TsmPrintfBuiltinNode extends TsmBuiltinNode {
    protected TsmPrintfBuiltinNode() {
        super("printf");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
