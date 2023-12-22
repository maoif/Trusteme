package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.types.TsmExpr;
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
    private IndirectCallNode callNode = IndirectCallNode.create();

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

            if (rands == null) {
                // TODO how to reconcile lexical scope and TsmExp?
                Object[] args = new Object[1];
                args[0] = proc.getLexicalScope();
                return callNode.call(proc.getCallTarget(), args);

            } else {
                CompilerAsserts.compilationConstant(this.rands.length);

                Object[] args = new Object[rands.length + 1];
                args[0] = proc.getLexicalScope();
                for (int i = 0; i < rands.length; i++) {
                    args[i + 1] = rands[i].executeGeneric(frame);
                }

                return callNode.call(proc.getCallTarget(), args);
            }
        } catch (UnexpectedResultException e) {
            throw new RuntimeException(e);
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
