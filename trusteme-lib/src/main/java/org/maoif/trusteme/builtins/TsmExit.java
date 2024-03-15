package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmVoid;

@NodeInfo(shortName = "exit")
public class TsmExit extends TsmBuiltinNode {
    public TsmExit() {
        super("exit");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // (exit)
        // (exit n)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 1 && args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args.length == 1) {
            System.exit(0);
        } else {
            if (args[1] instanceof TsmFixnum n) {
                System.exit((int) n.get());
            } else {
                throw new RuntimeException("Invalid exit status: " + args[1]);
            }
        }

        // cannot reach here
        CompilerDirectives.shouldNotReachHere();
        return TsmVoid.INSTANCE;
    }
}
