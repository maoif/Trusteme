package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.TailCallException;
import org.maoif.trusteme.nodes.TsmAppDispatchNode;
import org.maoif.trusteme.nodes.TsmAppDispatchNodeGen;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmProcedure;

@NodeInfo(shortName = "apply")
public class TsmApplyBuiltinNode extends TsmBuiltinNode {
    @Child
    private TsmAppDispatchNode dispatchNode = TsmAppDispatchNodeGen.create();

    public TsmApplyBuiltinNode() {
        super("apply");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // (apply proc args ... arg-list)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length <= 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmProcedure proc) {
            Object rest = args[args.length - 1];
            if (rest instanceof TsmPair p) {
                if (p.isImproper()) {
                    throw new RuntimeException("Not a proper list: " + rest);
                }

                Object[] procArgs = new Object[1 + args.length - 3 + p.length()];
                TsmExpr[] restArgs = p.rawArray();
                procArgs[0] = proc.getLexicalScope();
                System.arraycopy(args, 2, procArgs, 1, args.length - 3);
                System.arraycopy(restArgs, 0, procArgs, 1 + args.length - 3, restArgs.length);

                return call(frame, proc.getCallTarget(), procArgs);
            } else {
                throw new RuntimeException("Not a pair: " + rest);
            }
        } else {
            throw new RuntimeException("Expected a procedure, but got " + args[1]);
        }
    }

    private Object call(VirtualFrame frame, CallTarget callTarget, Object[] args) {
        while (true) {
            try {
                return this.dispatchNode.executeDispatch(frame, callTarget, args);
            } catch (TailCallException e) {
                callTarget = e.callTarget;
                args = e.args;
            }
        }
    }
}
