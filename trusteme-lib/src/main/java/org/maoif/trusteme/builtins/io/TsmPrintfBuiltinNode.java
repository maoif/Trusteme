package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.builtins.TsmBuiltinNode;

public class TsmPrintfBuiltinNode extends TsmBuiltinNode {
    protected TsmPrintfBuiltinNode() {
        super("printf");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
