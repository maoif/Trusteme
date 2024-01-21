package org.maoif.trusteme.builtins.pair;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmVoid;

@NodeInfo(shortName = "set-cdr!")
public class TsmSetCdr extends TsmBuiltinNode {

    public TsmSetCdr() {
        super("set-cdr!");
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
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmPair p) {
            p.setCdr((TsmExpr) args[2]);
        } else {
            throw new RuntimeException("Not a pair: " + args[1]);
        }

        return TsmVoid.INSTANCE;
    }
}
