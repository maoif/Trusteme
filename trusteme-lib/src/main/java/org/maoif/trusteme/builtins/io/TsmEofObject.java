package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmEof;

@NodeInfo(shortName = "eof-object")
public class TsmEofObject extends TsmBuiltinNode {
    public TsmEofObject() {
        super("eof-object");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmEof(frame);
    }

    @Override
    public TsmEof executeTsmEof(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 1)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        return TsmEof.INSTANCE;
    }
}
