package org.maoif.trusteme.builtins.io.text;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.*;

import java.io.IOException;

@NodeInfo(shortName = "display")
public class TsmDisplayBuiltinNode extends TsmBuiltinNode {
    public TsmDisplayBuiltinNode() {
        super("display");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmVoid(frame);
    }

    @Override
    public TsmVoid executeTsmVoid(VirtualFrame frame) {
        // (display obj)
        // (display obj textual-output-port)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2 && args.length !=3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        TsmPort port = null;
        if (args.length == 3) {
            if (args[2] instanceof TsmPort p) {
                if (p.isBinaryPort()) {
                    throw new RuntimeException("Expected a textual port");
                }

                if (p.isInputPort()) {
                    throw new RuntimeException("Expected an output port");
                }

                port = p;
            }
        } else {
            port = getContext().getCurrentOutputPort();
        }

        TsmExpr a = (TsmExpr) args[1];

        if (port instanceof TsmTextualOutputPort p) {
            try {
                p.get().write(a.toString());
                p.get().flush();
            } catch (IOException e) {
                throw new RuntimeException("Failed to write data");
            }
        } else if (port instanceof TsmStringOutputPort sp) {
            sp.putString(a.toString());
        } else {
            throw new RuntimeException("Not a textual output port: " + port);
        }

        return TsmVoid.INSTANCE;
    }
}
