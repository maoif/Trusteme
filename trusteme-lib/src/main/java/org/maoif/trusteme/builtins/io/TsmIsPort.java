package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmPort;

@NodeInfo(shortName = "port?")
public class TsmIsPort extends TsmBuiltinNode {
    public TsmIsPort() {
        super("port?");
    }

    @Override
    public TsmBool executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        Object p = args[1];
        return TsmBool.get(p instanceof TsmPort);
    }
}
