package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.types.TsmVoid;

@NodeInfo(shortName = "void")
public class TsmVoidBuiltinNode extends TsmBuiltinNode {
    public TsmVoidBuiltinNode() {
        super("void");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmVoid(frame);
    }

    @Override
    public TsmVoid executeTsmVoid(VirtualFrame frame) {
        return TsmVoid.INSTANCE;
    }
}
