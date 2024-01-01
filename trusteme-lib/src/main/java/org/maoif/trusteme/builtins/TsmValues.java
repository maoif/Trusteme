package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmVector;

/**
 * Return multiple values.
 */
@NodeInfo(shortName = "values")
public class TsmValues extends TsmBuiltinNode {
    public TsmValues() {
        super("values");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");

        TsmExpr[] ret = new TsmExpr[args.length - 1];
        for (int i = 1; i < args.length; i++) {
            ret[i - 1] = (TsmExpr) args[i];
        }

        return new TsmVector(ret);
    }
}
