package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.TailCallException;
import org.maoif.trusteme.types.TsmProcedure;

/**
 * Represents procedure application.
 */
public class TsmAppNode extends TsmNode {
    @Child
    private TsmNode rator;
    @Children
    private TsmNode[] rands;
    @Child
    public TsmAppDispatchNode dispatchNode = TsmAppDispatchNodeGen.create();

    /**
     * For 0-arity application.
     * @param rator The function
     */
    public TsmAppNode(TsmNode rator) {
        this.rator = rator;
        this.rands = null;
    }

    public TsmAppNode(TsmNode rator, TsmNode[] rands) {
        this.rator = rator;
        this.rands = rands;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        try {
            TsmProcedure proc = rator.executeTsmProcedure(frame);
            Object[] args;

            if (rands == null) {
                // TODO how to reconcile lexical scope and TsmExp?
                args = new Object[1];
                args[0] = proc.getLexicalScope();
            } else {
                CompilerAsserts.compilationConstant(this.rands.length);

                args = new Object[rands.length + 1];
                args[0] = proc.getLexicalScope();
                for (int i = 0; i < rands.length; i++) {
                    args[i + 1] = rands[i].executeGeneric(frame);
                }
            }

            CompilerAsserts.compilationConstant(this.isInTail);
            if (this.isInTail)
                throw new TailCallException(proc.getCallTarget(), args);
            else
                return call(frame, proc.getCallTarget(), args);
        } catch (UnexpectedResultException e) {
            throw new RuntimeException(e);
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

    @Override
    public String toString() {
       String p = String.format("(TsmAppNode %s", rator);
       if (rands != null) {
           StringBuilder sb = new StringBuilder(p);
           for (var n : rands) {
               sb.append(" ");
               sb.append(n.toString());
           }
           sb.append(")");
           return sb.toString();
       } else return p + ")";
    }
}
