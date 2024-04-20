package org.maoif.trusteme.builtins.conversions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmChar;
import org.maoif.trusteme.types.TsmFixnum;

@NodeInfo(shortName = "char->integer")
public class TsmChar2Int extends TsmBuiltinNode {
    public TsmChar2Int() {
        super("char->integer");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmFixnum(frame);
    }

    @Override
    public TsmFixnum executeTsmFixnum(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmChar ch) {
            return new TsmFixnum(ch.get());
        }

        throw new RuntimeException("Not a character:" + args[1]);
    }
}
