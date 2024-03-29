package org.maoif.trusteme.builtins.pair;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmNull;
import org.maoif.trusteme.types.TsmPair;

@NodeInfo(shortName = "list?")
public class TsmIsList extends TsmBuiltinNode  {
    public TsmIsList() {
        super("list?");
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

        if (args[1] == TsmNull.INSTANCE) {
            return TsmBool.TRUE;
        }

        return TsmBool.get(args[1] instanceof TsmPair p &&
                p.lengthOptional().isPresent());
    }
}
