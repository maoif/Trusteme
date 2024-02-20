package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.types.TsmProcedure;

/**
 * Represents a Scheme lambda expression,
 * returns a TsmProcedure object when executed.
 */
//@NodeField(name = "procedure", type = TsmProcedure.class)
public class TsmLambdaNode extends TsmNode {
    private RootCallTarget callTarget;

    public TsmLambdaNode(RootCallTarget callTarget) {
        this.callTarget = callTarget;
    }

    @Override
    public TsmProcedure executeTsmProcedure(VirtualFrame frame) {
        return (TsmProcedure) executeGeneric(frame);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // create closure
        var proc = new TsmProcedure(this.callTarget);
        proc.setLexicalScope(frame.materialize());
        return proc;
    }

    @Override
    public String toString() {
        return "TsmLambdaNode";
    }

}
