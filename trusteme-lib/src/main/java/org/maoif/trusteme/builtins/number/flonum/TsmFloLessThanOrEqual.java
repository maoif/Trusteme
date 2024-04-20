package org.maoif.trusteme.builtins.number.flonum;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmFlonum;

@NodeInfo(shortName = "$fl<=")
public class TsmFloLessThanOrEqual extends TsmBuiltinNode {
    public TsmFloLessThanOrEqual() {
        super("$fl<=");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmBool(frame);
    }

    @Override
    public TsmBool executeTsmBool(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmFlonum x) {
            if (args[2] instanceof TsmFlonum y) {
                return TsmBool.get(x.get() <= y.get());
            }

            throw new RuntimeException("Not a flonum: " + args[2]);
        }

        throw new RuntimeException("Not a flonum: " + args[1]);
    }
}
