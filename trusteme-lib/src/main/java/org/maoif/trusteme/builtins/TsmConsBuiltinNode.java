package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;
public class TsmConsBuiltinNode extends TsmBuiltinNode {
    public TsmConsBuiltinNode() {
        super("cons");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        return new TsmPair((TsmExpr) args[1], (TsmExpr) args[2]);
    }
}
