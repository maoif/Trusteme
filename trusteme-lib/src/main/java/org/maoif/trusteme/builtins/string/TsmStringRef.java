package org.maoif.trusteme.builtins.string;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmChar;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmString;

public class TsmStringRef extends TsmBuiltinNode {
    public TsmStringRef() {
        super("string-ref");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmChar(frame);
    }

    @Override
    public TsmChar executeTsmChar(VirtualFrame frame) {
        // (string-ref str n)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmString str) {
            if (args[2] instanceof TsmFixnum n) {
                int i = (int) n.get();
                String s = str.get();

                if (i < 0 || i >= s.length()) {
                    throw new RuntimeException(String.format("Invalid index %d for string %s", i, s));
                }

                return new TsmChar(s.charAt(i));
            } else {
                throw new RuntimeException("Not a fixnum:" + args[2]);
            }
        } else {
            throw new RuntimeException("Not a string:" + args[1]);
        }
    }
}
