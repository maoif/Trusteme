package org.maoif.trusteme.builtins.number.bignum;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBignum;
import org.maoif.trusteme.types.TsmBool;

@NodeInfo(shortName = "bignum?")
public class TsmIsBignum extends TsmBuiltinNode {
    public TsmIsBignum() {
        super("bignum?");
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
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        return TsmBool.get(args[1] instanceof TsmBignum);
    }
}
