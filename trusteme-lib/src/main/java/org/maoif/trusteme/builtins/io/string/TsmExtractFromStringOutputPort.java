package org.maoif.trusteme.builtins.io.string;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmString;
import org.maoif.trusteme.types.TsmStringOutputPort;

@NodeInfo(shortName = "$string-output-port-extract")
public class TsmExtractFromStringOutputPort extends TsmBuiltinNode {
    public TsmExtractFromStringOutputPort() {
        super("$string-output-port-extract");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmString(frame);
    }

    @Override
    public TsmString executeTsmString(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmStringOutputPort p) {
            String s = p.extract();

            return new TsmString(s);
        }

        throw new RuntimeException("Not a string output port: " + args[1]);
    }
}
