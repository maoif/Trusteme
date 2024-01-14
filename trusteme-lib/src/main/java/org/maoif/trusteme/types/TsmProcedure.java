package org.maoif.trusteme.types;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.MaterializedFrame;

/**
 * A TsmProcedure is obtained from evaluating a lambda expression.
 */
public class TsmProcedure extends TsmExpr {
    // encapsulates the target tree (TsmRootNode)
    private final RootCallTarget callTarget;
    // used as the 1st argument in every lambda to implement closure
    private MaterializedFrame lexicalScope;

    public TsmProcedure(RootCallTarget target) {
        this.callTarget = target;
    }

    public RootCallTarget getCallTarget() {
        return this.callTarget;
    }

    public void setLexicalScope(MaterializedFrame frame) {
        this.lexicalScope = frame;
    }

    public MaterializedFrame getLexicalScope() {
        return this.lexicalScope;
    }

    @Override
    public String toString() {
        // TODO add name
        return "#<procedure>";
    }

    @Override
    public String write() {
        return "#<procedure>";
    }
}
