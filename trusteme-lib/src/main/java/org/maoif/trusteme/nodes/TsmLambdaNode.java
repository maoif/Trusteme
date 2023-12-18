package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmProcedure;

/**
 * Represents a Scheme lambda expression,
 * returns a TsmProcedure object when executed.
 */
//@NodeField(name = "procedure", type = TsmProcedure.class)
public class TsmLambdaNode extends TsmNode {
    private TsmProcedure proc;

    public TsmLambdaNode(TsmProcedure proc) {
        this.proc = proc;
    }

    @Override
    public TsmProcedure executeTsmProcedure(VirtualFrame frame) {
        // create closure
        proc.setLexicalScope(frame.materialize());
        return this.proc;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        proc.setLexicalScope(frame.materialize());
        return this.proc;
    }

    @Override
    public String toString() {
        return "TsmLambdaNode";
    }

}
