package org.maoif.trusteme.builtins.number.flonum;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmFlonum;

@NodeInfo(shortName = "fl+")
public class TsmFloAdd extends TsmBuiltinNode {
    public TsmFloAdd() {
        super("fl+");
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
            return new TsmFlonum(0.0);

        double res = 0;
        for (int i = 1; i < args.length; i++) {
            if (args[i] instanceof TsmFlonum n) {
                res += n.get();
            } else {
                throw new RuntimeException("Not a flonum: " + args[i]);
            }
        }

        return new TsmFlonum(res);
    }
}
