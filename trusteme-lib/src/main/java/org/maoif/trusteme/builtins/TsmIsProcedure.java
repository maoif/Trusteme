package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmProcedure;

@NodeInfo(shortName = "procedure?")
public class TsmIsProcedure extends TsmBuiltinNode {
    public TsmIsProcedure() {
        super("procedure?");
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

        return TsmBool.get(args[1] instanceof TsmProcedure);
    }
}
