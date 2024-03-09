package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmPort;
import org.maoif.trusteme.types.TsmTextualOutputPort;
import org.maoif.trusteme.types.TsmVoid;

@NodeInfo(shortName = "current-error-port")
public class TsmCurrentErrorPort extends TsmBuiltinNode {
    public TsmCurrentErrorPort() {
        super("current-error-port");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 1 && args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args.length == 1) {
            return getContext().getCurrentErrorPort();
        }

        if (args[1] instanceof TsmPort p) {
            if (p instanceof TsmTextualOutputPort top) {
                getContext().setCurrentErrorPort(top);

                return TsmVoid.INSTANCE;
            } else {
                throw new RuntimeException("Not a textual output port: " + args[1]);
            }
        } else {
            throw new RuntimeException("Not a port: " + args[1]);
        }
    }

}
