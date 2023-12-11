package org.maoif.trusteme.types;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.MaterializedFrame;

/**
 * A TsmProcedure is obtained from evaluating a lambda expression.
 */
public class TsmProcedure extends TsmExpr {
    public final RootCallTarget callTarget;
    // used as the 1st argument in every lambda to implement closure
    private MaterializedFrame lexicalScope;

    public TsmProcedure(RootCallTarget target) {
        this.callTarget = target;
    }

}
