package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmPort;

@NodeInfo(shortName = "current-error-port")
public class TsmCurrentErrorPort extends TsmBuiltinNode {
    public TsmCurrentErrorPort() {
        super("current-error-port");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmPort(frame);
    }

    @Override
    public TsmPort executeTsmPort(VirtualFrame frame) {
        return getContext().getCurrentErrorPort();
    }
}
