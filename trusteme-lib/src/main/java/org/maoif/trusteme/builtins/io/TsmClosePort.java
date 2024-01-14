package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmPort;
import org.maoif.trusteme.types.TsmVoid;

@NodeInfo(shortName = "close-port")
public class TsmClosePort extends TsmBuiltinNode {
    public TsmClosePort() {
        super("close-port");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmVoid(frame);
    }

    @Override
    public TsmVoid executeTsmVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        Object p = args[1];
        if (p instanceof TsmPort port) {
            port.close();
        } else {
            throw new RuntimeException("Not a port");
        }

        return TsmVoid.INSTANCE;
    }
}
