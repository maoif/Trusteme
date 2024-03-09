package org.maoif.trusteme.builtins.string;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmString;

@NodeInfo(shortName = "string-length")
public class TsmStringLength extends TsmBuiltinNode {
    public TsmStringLength() {
        super("string-length");
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

        if (args[1] instanceof TsmString str) {
            return new TsmFixnum(str.get().length());
        }

        throw new RuntimeException("Not a string: " + args[1]);
    }
}
