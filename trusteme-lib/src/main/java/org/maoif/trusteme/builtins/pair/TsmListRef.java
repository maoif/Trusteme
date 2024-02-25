package org.maoif.trusteme.builtins.pair;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmNull;
import org.maoif.trusteme.types.TsmPair;

@NodeInfo(shortName = "list-ref")
public class TsmListRef extends TsmBuiltinNode {
    public TsmListRef() {
        super("list-ref");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        var arg = args[1];
        if (arg == TsmNull.INSTANCE) {
            throw new RuntimeException("List is empty");
        }

        if (arg instanceof TsmPair list) {
            TsmFixnum index = (TsmFixnum) args[2];

            return list.ref((int) index.get());
        } else {
            throw new RuntimeException("Not a list: " + arg);
        }
    }
}