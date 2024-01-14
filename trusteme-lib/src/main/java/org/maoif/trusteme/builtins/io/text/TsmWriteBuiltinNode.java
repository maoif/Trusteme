package org.maoif.trusteme.builtins.io.text;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.*;

import java.io.IOException;

@NodeInfo(shortName = "write")
public class TsmWriteBuiltinNode extends TsmBuiltinNode  {
    public TsmWriteBuiltinNode() {
        super("write");
    }

    protected TsmWriteBuiltinNode(String name) {
        super(name);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmVoid(frame);
    }

    @Override
    public TsmVoid executeTsmVoid(VirtualFrame frame) {
        // (write datum)
        // (write port datum)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2 && args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        TsmPort port = null;
        TsmExpr datum = (TsmExpr) args[2];
        if (args.length == 3) {
            if (args[1] instanceof TsmPort p) {
                port = p;
            } else {
                throw new RuntimeException("Not a port: " + args[1]);
            }
        } else {
            port = getContext().getCurrentOutputPort();
        }

        if (port instanceof TsmTextualOutputPort p) {
            try {
                p.get().write(datum.write());
            } catch (IOException e) {
                throw new RuntimeException("Failed to write data");
            }
        } else {
            throw new RuntimeException("Not a textual output port");
        }

        return TsmVoid.INSTANCE;
    }
}
