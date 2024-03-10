package org.maoif.trusteme.builtins.number.bignum;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBignum;

@NodeInfo(shortName = "$big*")
public class TsmBigMul extends TsmBuiltinNode {
    public TsmBigMul() {
        super("$big*");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmBignum(frame);
    }

    @Override
    public TsmBignum executeTsmBignum(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmBignum x) {
            if (args[2] instanceof TsmBignum y) {
                return new TsmBignum(x.get().multiply(y.get()));
            }

            throw new RuntimeException("Not a bignum: " + args[2]);
        }

        throw new RuntimeException("Not a bignum: " + args[1]);
    }
}
