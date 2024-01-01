package org.maoif.trusteme.builtins.vector;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmVector;

@NodeInfo(shortName = "vector?")
public class TsmIsVector extends TsmBuiltinNode {
    public TsmIsVector() {
        super("vector?");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        return args[1] instanceof TsmVector;
    }
}
