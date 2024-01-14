package org.maoif.trusteme.builtins.io.text;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmPort;
import org.maoif.trusteme.types.TsmString;
import org.maoif.trusteme.types.TsmTextualInputPort;

@NodeInfo(shortName = "open-input-file")
public class TsmOpenInputFile extends TsmBuiltinNode {
    public TsmOpenInputFile() {
        super("open-input-file");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmPort(frame);
    }

    @Override
    public TsmPort executeTsmPort(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmString file) {
            return new TsmTextualInputPort(file);
        } else {
            throw new RuntimeException("Not a string: " + args[1]);
        }
    }
}
