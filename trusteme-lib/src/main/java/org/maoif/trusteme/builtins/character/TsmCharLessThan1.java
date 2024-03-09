package org.maoif.trusteme.builtins.character;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmChar;

@NodeInfo(shortName = "$char<1?")
public class TsmCharLessThan1 extends TsmBuiltinNode {
    public TsmCharLessThan1() {
        super("$char<1?");
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

        if (args[1] instanceof TsmChar c1) {
            if (args[2] instanceof TsmChar c2) {
                return TsmBool.get(c1.get() < c2.get());
            } else {
                throw new RuntimeException("Not a character: " + args[2]);
            }
        } else {
            throw new RuntimeException("Not a character: " + args[1]);
        }
    }
}
