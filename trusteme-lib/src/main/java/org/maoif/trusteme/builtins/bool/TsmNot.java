package org.maoif.trusteme.builtins.bool;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBool;

@NodeInfo(shortName = "not")
public class TsmNot extends TsmBuiltinNode {
    public TsmNot() {
        super("not");
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

        if (args[1] != TsmBool.FALSE) {
            return TsmBool.FALSE;
        } else {
            return TsmBool.TRUE;
        }
    }
}
