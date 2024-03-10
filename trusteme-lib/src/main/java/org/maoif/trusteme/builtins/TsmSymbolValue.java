package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.types.TsmSymbol;

@NodeInfo(shortName = "symbol-value")
public class TsmSymbolValue extends TsmBuiltinNode {
    public TsmSymbolValue() {
        super("symbol-value");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmSymbol sym) {
            var v = getContext().getTopEnv().get(sym.get());
            if (v == null) {
                throw new RuntimeException("Variable " + sym + " is not bound");
            }

            return v;
        }

        throw new RuntimeException("Not a symbol: " + args[1]);
    }
}
