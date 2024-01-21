package org.maoif.trusteme.builtins.number.fixnum;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmFixnum;

@NodeInfo(shortName = "fxmod")
public class TsmFixModBuiltinNode extends TsmBuiltinNode {
    public TsmFixModBuiltinNode() {
        super("fxmod");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmFixnum(frame);
    }

    @Override
    public TsmFixnum executeTsmFixnum(VirtualFrame frame) {
        // (fxmod fixnum1 fixnum2)
        // The result has the same sign as fixnum2.
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmFixnum x) {
            if (args[2] instanceof TsmFixnum y) {
                return new TsmFixnum(Math.floorMod(x.get(), y.get()));
            } else {
                throw new RuntimeException("Type error: not a fixnum: " + args[2]);
            }
        } else {
            throw new RuntimeException("Type error: not a fixnum: " + args[1]);
        }
    }
}
