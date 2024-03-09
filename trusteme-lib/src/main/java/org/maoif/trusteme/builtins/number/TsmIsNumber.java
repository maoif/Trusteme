package org.maoif.trusteme.builtins.number;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBignum;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmFlonum;

@NodeInfo(shortName = "number?")
public class TsmIsNumber extends TsmBuiltinNode {
    public TsmIsNumber() {
        super("number?");
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

        var o = args[1];
        return TsmBool.get(o instanceof TsmFixnum || o instanceof TsmFlonum || o instanceof TsmBignum);
    }
}
