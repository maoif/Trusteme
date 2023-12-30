package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.dsl.Cached;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.IndirectCallNode;
import com.oracle.truffle.api.nodes.Node;

/**
 * Node that dispatches the procedure call.
 * Use `TsmAppDispatchNodeGen.create()` to use this class.
 */
public abstract class TsmAppDispatchNode extends Node {
    // The specialized methods need to follow this method's signature.
    public abstract Object executeDispatch(VirtualFrame frame, CallTarget callTarget, Object[] args);

    // Cache several direct call nodes for performance.
    @Specialization(limit = "3",
            guards = "callTarget == cachedCallTarget")
    protected Object doDirect(VirtualFrame frame, CallTarget callTarget, Object[] arguments,
                                     @Cached("callTarget") CallTarget cachedCallTarget,
                                     @Cached("create(callTarget)") DirectCallNode callNode) {
        return callNode.call(arguments);
    }

    // Fall back to indirect call if number of called procedures exceeds the limit above.
    @Specialization(replaces = "doDirect")
    protected Object doIndirect(VirtualFrame frame, CallTarget callTarget, Object[] arguments,
                                       @Cached("create()") IndirectCallNode callNode) {
        return callNode.call(callTarget, arguments);
    }
}
