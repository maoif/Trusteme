package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmPort;

@NodeInfo(shortName = "current-output-port")
public class TsmCurrentOutputPort extends TsmBuiltinNode {
    public TsmCurrentOutputPort() {
        super("current-output-port");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmPort(frame);
    }

    @Override
    public TsmPort executeTsmPort(VirtualFrame frame) {
        return getContext().getCurrentOutputPort();
    }
}
