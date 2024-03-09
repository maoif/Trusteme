package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.types.TsmNull;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmString;

import java.util.Arrays;

@NodeInfo(shortName = "command-line")
public class TsmCommandLine extends TsmBuiltinNode {
    public TsmCommandLine() {
        super("command-line");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 1)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        // TODO a better 1st arg?
        TsmPair res = new TsmPair(new TsmString("trusteme"));
        TsmPair next = res;

        String[] appArgs = getContext().getEnv().getApplicationArguments();
        for (String appArg : appArgs) {
            TsmPair p = new TsmPair(new TsmString(appArg));
            next.setCdr(p);
            next = p;
        }

        return res;
    }

}
