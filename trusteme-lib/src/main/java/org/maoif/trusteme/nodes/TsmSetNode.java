package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.VirtualFrame;
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

    private TsmSymbol sym;
//    private int slot;

    public TsmSetNode(TsmSymbol sym, TsmNode valueNode) {
//        // TODO sym useful?
//        this.slot = slot;
        this.sym = sym;
        this.valueNode = valueNode;
    }

//    public int getSlot() {
//        return this.slot;
//    }

    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        Object value = this.valueNode.executeGeneric(virtualFrame);
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
//                else throw new RuntimeException("Bad frame object type");
            }

            Object prevFrame = lexicalScope.getObject(0);
            if (prevFrame == null) {
                // we are at top frame
                var topEnv = (ConcurrentMap<String, TsmExpr>) lexicalScope.getObject(1);
                TsmExpr v = topEnv.get(sym.get());
                if (v == null) throw new RuntimeException("Unbound identifier: " + sym.get());
                else {
                    topEnv.put(sym.get(), (TsmExpr) value);
                    return TsmVoid.INSTANCE;
                }
            } else {
                lexicalScope = (Frame) prevFrame;
            }
        }
    }

    @Override
    public String toString() {
        return String.format("(TsmSetNode %s %s)", sym, valueNode);
    }
}
