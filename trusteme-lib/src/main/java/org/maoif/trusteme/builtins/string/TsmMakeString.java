package org.maoif.trusteme.builtins.string;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmChar;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmString;

@NodeInfo(shortName = "make-string")
public class TsmMakeString extends TsmBuiltinNode {
    public TsmMakeString() {
        super("make-string");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmString(frame);
    }

    @Override
    public TsmString executeTsmString(VirtualFrame frame) {
        // (make-string k)
        // (make-string k char)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2 && args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        int len = 0;
        char c;

        if (args[1] instanceof TsmFixnum n) {
            len = (int) n.get();
        } else {
            throw new RuntimeException("Not a fixnum: " + args[1]);
        }

        if (args.length == 2) {
            c = '\0';
        } else {
            if (args[2] instanceof TsmChar cc) {
                c = cc.get();
            } else {
                throw new RuntimeException("Not a character: " + args[2]);
            }
        }

        if (len == 0) {
            return new TsmString("");
        } else {
            return new TsmString(String.valueOf(c).repeat(len));
        }
    }
}
