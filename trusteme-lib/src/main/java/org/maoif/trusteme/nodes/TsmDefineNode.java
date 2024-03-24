package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CallTarget;

import org.maoif.trusteme.TailCallException;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmSymbol;
import org.maoif.trusteme.types.TsmVoid;

import java.util.concurrent.ConcurrentMap;

/**
 * Introduce a new binding (sym . value) to the current environment.
 */
public class TsmDefineNode extends TsmNode {
    @Child
    private TsmNode valueNode;
    @Child
    public TsmAppDispatchNode dispatchNode = TsmAppDispatchNodeGen.create();

    private final TsmSymbol sym;
    private final int slot;

    public TsmDefineNode(int slot, TsmSymbol sym, TsmNode valueNode) {
        this.slot = slot;
        this.sym = sym;
        this.valueNode = valueNode;
    }

    @Override
    public TsmVoid executeGeneric(VirtualFrame virtualFrame) {
        Object value;
        try {
            value = this.valueNode.executeGeneric(virtualFrame);
        } catch (TailCallException e) {
            value = call(virtualFrame, e.callTarget, e.args);
        }

        Frame topFrame = getContext().getLanguage().getTopFrame();
        var topEnv = (ConcurrentMap<String, TsmExpr>) topFrame.getObject(1);
        topEnv.put(sym.get(), (TsmExpr) value);

        return TsmVoid.INSTANCE;
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
        return String.format("(TsmDefineNode %s %s)", sym, valueNode);
    }
}
