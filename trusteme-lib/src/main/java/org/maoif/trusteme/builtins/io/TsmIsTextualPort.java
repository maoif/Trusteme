package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmPort;

@NodeInfo(shortName = "textual-port?")
public class TsmIsTextualPort extends TsmBuiltinNode {
    public TsmIsTextualPort() {
        super("textual-port?");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmPort p) {
            return TsmBool.get(!p.isBinaryPort());
        } else {
            throw new RuntimeException("Not a port: " + args[1]);
        }
    }
}
