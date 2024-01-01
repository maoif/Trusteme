package org.maoif.trusteme.builtins.number;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.builtins.TsmBuiltinNode;

@NodeInfo(shortName = "=")
public class TsmNumberEqualBuiltinNode extends TsmBuiltinNode {
    public TsmNumberEqualBuiltinNode() {
        super("=");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }
}
