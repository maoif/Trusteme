package org.maoif.trusteme.builtins.vector;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmVector;

@NodeInfo(shortName = "vector-length")
public class TsmVectorLength extends TsmBuiltinNode {

    public TsmVectorLength() {
        super("vector-length");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        TsmExpr v = (TsmExpr) args[1];
        if (v instanceof TsmVector vv) {
            return new TsmFixnum(vv.length());
        } else throw new RuntimeException(v + " is not a vector");
    }
}
