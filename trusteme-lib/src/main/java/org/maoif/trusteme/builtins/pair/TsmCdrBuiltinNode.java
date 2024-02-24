package org.maoif.trusteme.builtins.pair;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmNull;
import org.maoif.trusteme.types.TsmPair;

public class TsmCdrBuiltinNode extends TsmBuiltinNode {
    public TsmCdrBuiltinNode() {
        super("cdr");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmPair p && p.car() != TsmNull.INSTANCE) {
            return p.cdr();
        } else throw new RuntimeException("Not a pair: " + args[1]);
    }
}
