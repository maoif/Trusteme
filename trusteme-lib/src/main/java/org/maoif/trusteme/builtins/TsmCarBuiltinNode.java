package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.types.TsmPair;

public class TsmCarBuiltinNode extends TsmBuiltinNode {


    public TsmCarBuiltinNode() {
        super("car");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmPair p) {
            return p.car();
        } else throw new RuntimeException("Not a pair");
    }
}
