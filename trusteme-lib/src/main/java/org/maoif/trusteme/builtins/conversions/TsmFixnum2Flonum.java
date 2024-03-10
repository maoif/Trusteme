package org.maoif.trusteme.builtins.conversions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmFlonum;

@NodeInfo(shortName = "fixnum->flonum")
public class TsmFixnum2Flonum extends TsmBuiltinNode {
    public TsmFixnum2Flonum() {
        super("fixnum->flonum");
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
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmFixnum n) {
            return new TsmFlonum((double) n.get());
        }

        throw new RuntimeException("Not a fixnum: " + args[1]);
    }
}
