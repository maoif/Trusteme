package org.maoif.trusteme.builtins.number.flonum;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmFlonum;

@NodeInfo(shortName = "fl-")
public class TsmFloSub extends TsmBuiltinNode {
    public TsmFloSub() {
        super("fl-");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmFlonum(frame);
    }

    @Override
    public TsmFlonum executeTsmFlonum(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length == 1)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args.length == 2) {
            if (args[1] instanceof TsmFlonum n) {
                return new TsmFlonum(-n.get());
            }

            throw new RuntimeException("Not a flonum: " + args[1]);
        }

        double res;

        if (args[1] instanceof TsmFlonum n) {
            res = n.get();
        } else {
            throw new RuntimeException("Not a flonum: " + args[1]);
        }

        for (int i = 2; i < args.length; i++) {
            if (args[i] instanceof TsmFlonum x) {
                res -= x.get();
            } else {
                throw new RuntimeException("Not a flonum: " + args[i]);
            }
        }

        return new TsmFlonum(res);
    }
}
