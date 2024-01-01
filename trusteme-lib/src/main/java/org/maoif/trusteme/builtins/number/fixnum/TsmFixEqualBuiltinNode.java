package org.maoif.trusteme.builtins.number.fixnum;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmFixnum;

public class TsmFixEqualBuiltinNode extends TsmBuiltinNode {
    public TsmFixEqualBuiltinNode() {
        super("fx=");
    }

    @Override
    public TsmBool executeTsmBool(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length == 1)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        long res;
        if (args[1] instanceof TsmFixnum n)
            res = n.get();
        else throw new RuntimeException("Type error: not a fixnum: " + args[1]);

        for (int i = 2; i < args.length; i++) {
            if (args[i] instanceof TsmFixnum nn) {
                if (res != nn.get()) return TsmBool.FALSE;
            } else throw new RuntimeException("Type error: not a fixnum: " + args[i]);
        }

        return TsmBool.TRUE;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmBool(frame);
    }
}
