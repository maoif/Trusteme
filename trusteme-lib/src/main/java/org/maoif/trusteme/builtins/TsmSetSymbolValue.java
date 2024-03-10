package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmVoid;
import org.maoif.trusteme.types.TsmSymbol;

@NodeInfo(shortName = "set-symbol-value!")
public class TsmSetSymbolValue extends TsmBuiltinNode {
    public TsmSetSymbolValue() {
        super("set-symbol-value!");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmVoid(frame);
    }

    @Override
    public TsmVoid executeTsmVoid(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmSymbol sym) {
            getContext().getTopEnv().put(sym.get(), (TsmExpr) args[2]);

            return TsmVoid.INSTANCE;
        }

        throw new RuntimeException("Not a symbol: " + args[1]);
    }
}
