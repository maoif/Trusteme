package org.maoif.trusteme.builtins.conversions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmString;
import org.maoif.trusteme.types.TsmSymbol;

@NodeInfo(shortName = "string->symbol")
public class TsmString2Symbol extends TsmBuiltinNode {
    public TsmString2Symbol() {
        super("string->symbol");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmSymbol(frame);
    }

    @Override
    public TsmSymbol executeTsmSymbol(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmString str) {
            return TsmSymbol.get(str.get());
        }

        throw new RuntimeException("Not a string: " + args[1]);
    }
}
