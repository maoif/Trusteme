package org.maoif.trusteme.builtins.pair;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;

@NodeInfo(shortName = "list")
public class TsmListBuiltinNode extends TsmBuiltinNode {
    public TsmListBuiltinNode() {
        super("list");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmPair(frame);
    }

    @Override
    public TsmPair executeTsmPair(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length == 1)
            return new TsmPair();

        TsmPair res = new TsmPair();
        res.setCar((TsmExpr) args[1]);

        TsmPair cdr = res;
        for (int i = 2; i < args.length; i++) {
            TsmPair p = new TsmPair((TsmExpr) args[i]);
            cdr.setCdr(p);
            cdr = p;
        }

        return res;
    }
}
