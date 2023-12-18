package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;

public class TsmCarBuiltinNode extends TsmBuiltinNode {


    protected TsmCarBuiltinNode() {
        super("car");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
