package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmExpr;

@NodeInfo(shortName = "eqv?")
public class TsmEqv extends TsmBuiltinNode {
    public TsmEqv() {
        super("eqv?");
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

        var a = (TsmExpr) args[1];
        var b = (TsmExpr) args[2];

        return TsmBool.get(a.isEqv(b));
    }
}
