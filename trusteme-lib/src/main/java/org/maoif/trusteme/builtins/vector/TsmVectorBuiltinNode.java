package org.maoif.trusteme.builtins.vector;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmVector;

@NodeInfo(shortName = "vector")
public class TsmVectorBuiltinNode extends TsmBuiltinNode {
    public TsmVectorBuiltinNode() {
        super("vector");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmVector(frame);
    }

    @Override
    public TsmVector executeTsmVector(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length == 1)
            return new TsmVector(0);

        TsmVector v = new TsmVector(args.length - 1);
        for (int i = 1; i < args.length; i++) {
            v.rawArray()[i - 1] = (TsmExpr) args[i];
        }

        return v;
    }
}
