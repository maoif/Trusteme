package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.*;

@NodeInfo(shortName = "eq?")
public class TsmEq extends TsmBuiltinNode {
    public TsmEq() {
        super("eq?");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmBool(frame);
    }

    @Override
    public TsmBool executeTsmBool(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        var a = args[1];
        var b = args[2];

        if (a instanceof TsmBool x) {
            if (b instanceof TsmBool y) {
                return TsmBool.get(x == y);
            } else {
                return TsmBool.FALSE;
            }
        } else if (a instanceof TsmChar x) {
            if (b instanceof TsmChar y) {
                return TsmBool.get(x.get() == y.get());
            } else {
                return TsmBool.FALSE;
            }
        } else if (a instanceof TsmFixnum x) {
            if (b instanceof TsmFixnum y) {
                return TsmBool.get(x.get() == y.get());
            } else {
                return TsmBool.FALSE;
            }
        } else if (a instanceof TsmFlonum x) {
            if (b instanceof TsmFlonum y) {
                return TsmBool.get(x.get() == y.get());
            } else {
                return TsmBool.FALSE;
            }
        } else if (a instanceof TsmBignum x) {
            if (b instanceof TsmBignum y) {
                return TsmBool.get(x.get().equals(y.get()));
            } else {
                return TsmBool.FALSE;
            }
        } else {
            return TsmBool.get(args[1] == args[2]);
        }
    }
}
