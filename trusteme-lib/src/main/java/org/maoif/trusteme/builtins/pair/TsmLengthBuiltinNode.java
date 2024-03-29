package org.maoif.trusteme.builtins.pair;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmNull;
import org.maoif.trusteme.types.TsmPair;

@NodeInfo(shortName = "length")
public class TsmLengthBuiltinNode extends TsmBuiltinNode {
    public TsmLengthBuiltinNode() {
        super("length");
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

        if (args[1] == TsmNull.INSTANCE) {
            return new TsmFixnum(0);
        }

        if (args[1] instanceof TsmPair p) {
            return new TsmFixnum(p.length());
        } else {
            throw new RuntimeException("Not a list: " + args[1]);
        }
    }
}
