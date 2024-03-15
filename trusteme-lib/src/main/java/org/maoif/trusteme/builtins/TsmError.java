package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.types.TsmString;
import org.maoif.trusteme.types.TsmSymbol;
import org.maoif.trusteme.types.TsmVoid;

import java.util.Arrays;

/**
 * Signals an error condition, usually just terminates the program.
 */
@NodeInfo(shortName = "error")
public class TsmError extends TsmBuiltinNode {
    public TsmError() {
        super("error");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // (error who message irritant1 ...)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length < 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        var who = args[1];
        var msg = args[2];
        var irrts = new Object[args.length - 3];
        System.arraycopy(args, 3, irrts, 0, args.length - 3);
        if (who instanceof TsmSymbol w && msg instanceof TsmString msgStr) {
            var m = String.format("who: %s, msg: %s, irritant: %s",
                    w.get(), msgStr.get(), Arrays.toString(irrts));
            throw new RuntimeException(m);
        }

        CompilerDirectives.shouldNotReachHere();
        return TsmVoid.INSTANCE;
    }
}
