package org.maoif.trusteme.builtins.vector;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmVector;

import java.util.Arrays;

@NodeInfo(shortName = "make-vector")
public class TsmMakeVector extends TsmBuiltinNode {
    public TsmMakeVector() {
        super("make-vector");
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
        if (args.length != 2 && args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmFixnum fix) {
            int n = (int) fix.get();
            if (n < 0) {
                throw new RuntimeException("Length not valid: " + n);
            }

            if (n == 0) {
                return new TsmVector(0);
            }

            TsmExpr e;
            if (args.length == 2) {
                e = new TsmFixnum(0);
            } else {
                e = (TsmExpr) args[2];
            }

            TsmExpr[] v = new TsmExpr[n];
            Arrays.fill(v, e);

            return new TsmVector(v);
        } else {
            throw new RuntimeException("Not a fixnum: " + args[1]);
        }
    }
}
