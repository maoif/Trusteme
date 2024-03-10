package org.maoif.trusteme.builtins.conversions;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBignum;
import org.maoif.trusteme.types.TsmFixnum;

import java.math.BigInteger;

@NodeInfo(shortName = "$fix->big")
public class TsmFix2Big extends TsmBuiltinNode {
    public TsmFix2Big() {
        super("$fix->big");
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
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmFixnum n) {
            return new TsmBignum(BigInteger.valueOf(n.get()));
        }

        throw new RuntimeException("Not a fixnum: " + args[1]);
    }
}
