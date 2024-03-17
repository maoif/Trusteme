package org.maoif.trusteme.builtins.number;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBignum;
import org.maoif.trusteme.types.TsmFixnum;

import java.math.BigInteger;

@NodeInfo(shortName = "mod")
public class TsmMod extends TsmBuiltinNode {
    public TsmMod() {
        super("mod");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmFixnum x) {
            if (args[2] instanceof TsmFixnum y) {
                return new TsmFixnum(Math.floorMod(x.get(), y.get()));
            } else if (args[2] instanceof TsmBignum y) {
                var res = BigInteger.valueOf(x.get()).mod(y.get());
                try {
                    return new TsmFixnum(res.longValueExact());
                } catch (ArithmeticException e) {
                    return new TsmBignum(res);
                }
            }

            throw new RuntimeException("Not an integer: " + args[2]);
        } else if (args[1] instanceof TsmBignum x) {
            if (args[2] instanceof TsmFixnum y) {
                var res = x.get().mod(BigInteger.valueOf(y.get()));
                try {
                    return new TsmFixnum(res.longValueExact());
                } catch (ArithmeticException e) {
                    return new TsmBignum(res);
                }
            } else if (args[2] instanceof TsmBignum y) {
                var res = x.get().mod(y.get());
                try {
                    return new TsmFixnum(res.longValueExact());
                } catch (ArithmeticException e) {
                    return new TsmBignum(res);
                }
            }

            throw new RuntimeException("Not an integer: " + args[2]);
        }

        throw new RuntimeException("Not an integer: " + args[1]);
    }

}
