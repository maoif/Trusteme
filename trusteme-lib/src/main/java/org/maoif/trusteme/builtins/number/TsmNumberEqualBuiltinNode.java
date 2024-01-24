package org.maoif.trusteme.builtins.number;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBignum;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmFlonum;

@NodeInfo(shortName = "=")
public class TsmNumberEqualBuiltinNode extends TsmBuiltinNode {
    public TsmNumberEqualBuiltinNode() {
        super("=");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length == 1)
            throw new RuntimeException("invalid argument count in " + this.NAME);
        if (args.length == 2 && args[1] instanceof TsmFixnum)
            return TsmBool.TRUE;

        for (int i = 1; i < args.length - 1; i++) {
            var x = args[i];
            var y = args[i + 1];
            boolean res = false;

            if (x instanceof TsmFixnum a) {
                if (y instanceof TsmFixnum b) {
                    res = a.get() == b.get();
                } else if (y instanceof TsmFlonum b) {
                    res = (double) a.get() == b.get();
                } else if (y instanceof TsmBignum b) {
                    return TsmBool.FALSE;
                } else {
                    throw new RuntimeException("Not a number: " + y);
                }
            } else if (x instanceof TsmFlonum a) {
                if (y instanceof TsmFixnum b) {
                    res = a.get() == (double) b.get();
                } else if (y instanceof TsmFlonum b) {
                    res = a.get() == b.get();
                } else if (y instanceof TsmBignum b) {
                    return TsmBool.FALSE;
                } else {
                    throw new RuntimeException("Not a number: " + y);
                }
            } else if (x instanceof TsmBignum a) {
                if (y instanceof TsmBignum b) {
                    res = a.get().equals(b.get());
                } else {
                    return TsmBool.FALSE;
                }
            } else {
                throw new RuntimeException("Not a number: " + x);
            }

            if (!res) {
                return TsmBool.FALSE;
            }
        }

        return TsmBool.TRUE;
    }
}
