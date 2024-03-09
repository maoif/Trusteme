package org.maoif.trusteme.builtins.conversions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBignum;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmFlonum;
import org.maoif.trusteme.types.TsmString;

@NodeInfo(shortName = "number->string")
public class TsmNumber2String extends TsmBuiltinNode {
    public TsmNumber2String() {
        super("number->string");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmString(frame);
    }

    @Override
    public TsmString executeTsmString(VirtualFrame frame) {
        // (number->string num)
        // (number->string num radix)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2 && args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        int radix = 10;
        if (args.length == 3) {
            if (args[2] instanceof TsmFixnum r) {
                var n = r.get();
                if (n > 32 || n < 2) {
                    throw new RuntimeException("Invalid radix: " + n);
                }
                radix = (int) n;

            } else {
                throw new RuntimeException("Invalid radix: " + args[2]);
            }
        }

        if (args[1] instanceof TsmFixnum n) {
            return new TsmString(Long.toString(n.get(), radix));
        } else if (args[1] instanceof TsmFlonum n) {
            return new TsmString(Double.toString(n.get()));
        } else if (args[1] instanceof TsmBignum n) {
            return new TsmString(n.get().toString(radix));
        }

        throw new RuntimeException("Not a number: " + args[1]);
    }
}
