package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.TailCallException;
import org.maoif.trusteme.nodes.TsmAppDispatchNode;
import org.maoif.trusteme.nodes.TsmAppDispatchNodeGen;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmProcedure;
import org.maoif.trusteme.types.TsmVector;

@NodeInfo(shortName = "call-with-values")
public class TsmCallWithValues extends TsmBuiltinNode {
    @Child
    private TsmAppDispatchNode dispatchNode1;
    @Child
    private TsmAppDispatchNode dispatchNode2;
    @Child
    private TsmAppDispatchNode dispatchNode3;

    public TsmCallWithValues() {
        super("call-with-values");
        dispatchNode1 = TsmAppDispatchNodeGen.create();
        dispatchNode2 = TsmAppDispatchNodeGen.create();
        dispatchNode3 = TsmAppDispatchNodeGen.create();
    }

    // (call-with-values proc0 proc1)
    // the number of returned values from proc0 should
    // match the arity of proc1
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        TsmExpr p1 = (TsmExpr) args[1];
        TsmExpr p2 = (TsmExpr) args[2];

        // Producer and consumer are in evaluated form
        // because we are in an AppNode.
        if (p1 instanceof TsmProcedure producer) {
            if (p2 instanceof TsmProcedure consumer) {
                Object[] pArgs = new Object[1];
                pArgs[0] = producer.getLexicalScope();

                try {
                    dispatchNode1.executeDispatch(frame, producer.getCallTarget(), pArgs);
                } catch (TailCallException tailCallException) {
                    Object ret = dispatchNode2.executeDispatch(
                            frame, tailCallException.callTarget, tailCallException.args);

                    // As shown in ValuesNode, multiple return values are stored in a TsmVector.
                    if (ret instanceof TsmVector cArgs) {
                        Object[] newCArgs = new Object[1 + cArgs.length()];
                        System.arraycopy(cArgs.rawArray(), 0, newCArgs, 1, cArgs.length());
                        newCArgs[0] = consumer.getLexicalScope();

                        return dispatchNode3.executeDispatch(frame, consumer.getCallTarget(), newCArgs);
                    } else {
                        throw new RuntimeException("bad multiple return value format: " + ret);
                    }
                }
            } else {
                throw new RuntimeException(p2 + " is not a procedure");
            }
        } else {
            throw new RuntimeException(p1 + " is not a procedure");
        }

        throw CompilerDirectives.shouldNotReachHere();
    }

}
