package org.maoif.trusteme.builtins.number.fixnum;

import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmFixnum;

public class TsmFixMulBuiltinNode extends TsmBuiltinNode {
    public TsmFixMulBuiltinNode() {
        super("fx*");
    }

    @Override
    public TsmFixnum executeTsmFixnum(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length == 1)
            return new TsmFixnum(1);

        long res = 1;
        for (int i = 1; i < args.length; i++) {
            if (args[i] instanceof TsmFixnum n) {
                try {
                    res = Math.multiplyExact(res, n.get());
                } catch (ArithmeticException e) {
                    throw new RuntimeException("Fixnum operation out of range");
                }
            } else throw new RuntimeException("Type error: not a fixnum: " + args[i]);
        }

        return new TsmFixnum(res);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmFixnum(frame);
    }
}
