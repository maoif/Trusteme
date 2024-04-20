package org.maoif.trusteme.builtins.conversions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmChar;
import org.maoif.trusteme.types.TsmFixnum;

@NodeInfo(shortName = "integer->char")
public class TsmInt2Char extends TsmBuiltinNode {
    public TsmInt2Char() {
        super("integer->char");
    }


    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmChar(frame);
    }

    @Override
    public TsmChar executeTsmChar(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmFixnum i) {
            return new TsmChar((char) i.get());
        }

        throw new RuntimeException("Not a character:" + args[1]);
    }

}
