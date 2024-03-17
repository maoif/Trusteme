package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.Reader;
import org.maoif.trusteme.Parser;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmString;

@NodeInfo(shortName = "eval-core")
public class TsmEvalCore extends TsmBuiltinNode {
    public TsmEvalCore() {
        super("eval-core");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // (eval-core core-expr)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        // TODO optimize, maybe convert TsmExpr to sExpr directly
        String code =  ((TsmExpr) args[1]).write();
        var lang = getContext().getLanguage();

        return lang.parse(code).call(lang.getTopFrame());
    }
}
