package org.maoif.trusteme;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.nodes.ControlFlowException;

/**
 * When a call happens in a tail context, this exception is thrown.
 */
public class TailCallException extends ControlFlowException {
    public final CallTarget callTarget;
    public final Object[] args;

    public TailCallException(CallTarget callTarget, Object[] args) {
        this.callTarget = callTarget;
        this.args = args;
    }
}
