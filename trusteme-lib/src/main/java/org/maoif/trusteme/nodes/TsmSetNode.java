package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.CallTarget;

import org.maoif.trusteme.TailCallException;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmSymbol;
import org.maoif.trusteme.types.TsmVoid;

import java.util.concurrent.ConcurrentMap;

/**
 * (set! var val)
 *
 * TODO The difference between set! and define is that the former operate on
 * already existing binding, while the latter introduces new binding.
 */
public class TsmSetNode extends TsmNode {
    @Child
    private TsmNode valueNode;
    @Child
    public TsmAppDispatchNode dispatchNode = TsmAppDispatchNodeGen.create();

    private TsmSymbol sym;

    public TsmSetNode(TsmSymbol sym, TsmNode valueNode) {
        this.sym = sym;
        this.valueNode = valueNode;
    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        Object value;
        try {
            value = this.valueNode.executeGeneric(virtualFrame);
        } catch (TailCallException e) {
            value = call(virtualFrame, e.callTarget, e.args);
        }

        Frame lexicalScope = virtualFrame;
        while (true) {
            int num = lexicalScope.getFrameDescriptor().getNumberOfSlots();
            for (int i = 1; i < num; i++) {
                Object o = lexicalScope.getObject(i);
                if (o instanceof TsmPair p && p.car() instanceof TsmSymbol s) {
                    if (s.get().equals(sym.get())) {
                        p.setCdr((TsmExpr) value);
                        return TsmVoid.INSTANCE;
                    }
                }
            }

            Object prevFrame = lexicalScope.getObject(0);
            if (prevFrame == null) {
                // we are at top frame
                var topEnv = (ConcurrentMap<String, TsmExpr>) lexicalScope.getObject(1);
                TsmExpr v = topEnv.get(sym.get());
                if (v == null) {
                    throw new RuntimeException("Unbound identifier: " + sym.get());
                } else {
                    topEnv.put(sym.get(), (TsmExpr) value);
                    return TsmVoid.INSTANCE;
                }
            } else {
                lexicalScope = (Frame) prevFrame;
            }
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
        return String.format("(TsmSetNode %s %s)", sym, valueNode);
    }
}
