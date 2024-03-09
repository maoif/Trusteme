package org.maoif.trusteme.builtins.string;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmString;

@NodeInfo(shortName = "string-append")
public class TsmStringAppend extends TsmBuiltinNode {
    public TsmStringAppend() {
        super("string-append");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmString(frame);
    }

    @Override
    public TsmString executeTsmString(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");

        if (args.length == 1) {
            return new TsmString("");
        }

        for (int i = 1; i < args.length; i++) {
            if (!(args[i] instanceof TsmString)) {
                throw new RuntimeException("Not a string: " + args[i]);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            sb.append(((TsmString) args[i]).get());
        }

        return new TsmString(sb.toString());
    }
}
