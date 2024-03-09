package org.maoif.trusteme.builtins.string;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmChar;
import org.maoif.trusteme.types.TsmVoid;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmString;

import java.util.Arrays;

public class TsmStringSet extends TsmBuiltinNode {
    public TsmStringSet() {
        super("string-set!");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmVoid(frame);
    }

    @Override
    public TsmVoid executeTsmVoid(VirtualFrame frame) {
        // (string-set! str n char)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 4)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmString str) {
            if (args[2] instanceof TsmFixnum n) {
                if (args[3] instanceof TsmChar c) {
                    int i = (int) n.get();
                    String s = str.get();

                    if (i < 0 || i >= s.length()) {
                        throw new RuntimeException(String.format("Invalid index %d for string %s", i, s));
                    }

                    var ss = s.toCharArray();
                    ss[i] = c.get();
                    str.replace(String.valueOf(ss));

                    return TsmVoid.INSTANCE;
                } else {
                    throw new RuntimeException("Not a character:" + args[3]);
                }
            } else {
                throw new RuntimeException("Not a fixnum:" + args[2]);
            }
        } else {
            throw new RuntimeException("Not a string:" + args[1]);
        }
    }
}
