package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmStringOutputPort;

@NodeInfo(shortName = "$open-string-output-port")
public class TsmOpenStringOutputPort extends TsmBuiltinNode {

    public TsmOpenStringOutputPort() {
        super("$open-string-output-port");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 1)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        return new TsmStringOutputPort();
    }
}
