package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmSymbol;
import org.maoif.trusteme.types.TsmVoid;

/**
 * Introduce a new binding (sym . value) to the current environment.
 */
public class TsmDefineNode extends TsmNode {
    @Child
    private TsmNode valueNode;

    private final TsmSymbol sym;
    private final int slot;

    public TsmDefineNode(int slot, TsmSymbol sym, TsmNode valueNode) {
        this.slot = slot;
        this.sym = sym;
        this.valueNode = valueNode;
    }

//    public int getSlot() {
//        return this.slot;
//    }

//    protected TsmExpr write(VirtualFrame virtualFrame, Object value) {
//        int slot = this.getSlot();
//        if (virtualFrame.getFrameDescriptor().getSlotKind(slot) != FrameSlotKind.Object) {
//            CompilerDirectives.transferToInterpreterAndInvalidate();
//            virtualFrame.getFrameDescriptor().setSlotKind(slot, FrameSlotKind.Object);
//        }
//        virtualFrame.setObject(slot, value);
//
//        return TsmVoid.INSTANCE;
//    }

    @Override
    public TsmVoid executeGeneric(VirtualFrame virtualFrame) {
        Object value = this.valueNode.executeGeneric(virtualFrame);
        virtualFrame.setObject(this.slot, new TsmPair(this.sym, (TsmExpr) value));

        return TsmVoid.INSTANCE;

//        Frame lexicalScope = virtualFrame;
//        while (true) {
//            int num = lexicalScope.getFrameDescriptor().getNumberOfSlots();
//            for (int i = 1; i < num; i++) {
//                Object o = lexicalScope.getObject(i);
//                if (o instanceof TsmPair p && p.car() instanceof TsmSymbol s) {
//                    if (s.get().equals(sym.get())) {
//                        p.setCdr((TsmExpr) value);
//                        return TsmVoid.INSTANCE;
//                    }
//                } else throw new RuntimeException("Bad frame object type");
//            }
//
//            // TODO if lexicalScope is already the top frame and value not found, bug out.
//            lexicalScope = (Frame) lexicalScope.getObject(0);
//        }
    }

    @Override
    public String toString() {
        return String.format("(TsmDefineNode %s %s)", sym, valueNode);
    }
}
