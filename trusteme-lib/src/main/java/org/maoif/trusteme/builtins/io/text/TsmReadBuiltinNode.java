package org.maoif.trusteme.builtins.io.text;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.Reader;
import org.maoif.sExpr;
import org.maoif.trusteme.Parser;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.*;


@NodeInfo(shortName = "read")
public class TsmReadBuiltinNode extends TsmBuiltinNode {
    public TsmReadBuiltinNode() {
        super("read");
    }

    protected TsmReadBuiltinNode(String name) {
        super(name);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // (read)
        // (read port)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 1 && args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        TsmPort port = null;
        if (args.length == 2) {
            if (args[1] instanceof TsmPort p) {
                port = p;
            } else {
                throw new RuntimeException("Not a port: " + args[1]);
            }
        } else {
            port = getContext().getCurrentInputPort();
        }

        if (port instanceof TsmTextualInputPort p) {
            if (p.hasPos().isTrue()) {
                // For inputs from a file, read a datum and update the position.
                var val = Reader.readWithNextPosition(p.getCharArray(), (int) p.getPos().get());
                var res = Parser.parseQuoted((sExpr) val[0]);
                p.setPos(new TsmFixnum((Integer) val[1]));

                return res;
            } else {
                // For inputs like stdin, just read the 1st datum in a line.
                TsmExpr line = p.getLine();
                if (line instanceof TsmEof) {
                    return line;
                }

                return Parser.parseQuoted(Reader.read(((TsmString) line).get()));
            }
        } else {
            throw new RuntimeException("Not a textual input port");
        }
    }
}
