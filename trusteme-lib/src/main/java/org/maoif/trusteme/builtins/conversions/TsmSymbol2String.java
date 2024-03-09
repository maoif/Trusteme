package org.maoif.trusteme.builtins.conversions;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmString;
import org.maoif.trusteme.types.TsmSymbol;

@NodeInfo(shortName = "symbol->string")
public class TsmSymbol2String extends TsmBuiltinNode {
    public TsmSymbol2String() {
        super("symbol->string");
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

        if (args[1] instanceof TsmSymbol sym) {
            return new TsmString(sym.get());
        }

        throw new RuntimeException("Not a symbol: " + args[1]);
    }
}
