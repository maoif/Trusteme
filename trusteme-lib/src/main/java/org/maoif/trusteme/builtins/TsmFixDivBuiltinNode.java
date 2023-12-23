package org.maoif.trusteme.builtins;


import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.types.TsmFixnum;

public class TsmFixDivBuiltinNode extends TsmBuiltinNode {
    public TsmFixDivBuiltinNode() {
        super("fx/");
    }

    @Override
    public TsmFixnum executeTsmFixnum(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length == 1)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        long res;
        if (args[1] instanceof TsmFixnum n)
            res = n.get();
        else throw new RuntimeException("Type error: not a fixnum: " + args[1]);

        if (args.length == 2) return new TsmFixnum(Math.floorDiv(1, res));

        for (int i = 2; i < args.length; i++) {
            if (args[i] instanceof TsmFixnum nn) {
                try {
                    res = Math.floorDiv(res, nn.get());
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
