package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
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
//        virtualFrame.setObject(this.slot, new TsmPair(this.sym, (TsmExpr) value));

        // TODO cache the top frame
        Frame topFrame = getTopFrame(virtualFrame);
        var topEnv = (ConcurrentMap<String, TsmExpr>) topFrame.getObject(1);
        topEnv.put(sym.get(), (TsmExpr) value);

        return TsmVoid.INSTANCE;
    }

    private static Frame getTopFrame(VirtualFrame virtualFrame) {
        Frame f = virtualFrame;
        while (f.getObject(0) != null) {
            if (f.getObject(0) instanceof Frame ff)
                f = ff;
            else throw new RuntimeException(
                    "Bad frame structure, first slot should be materialized frame");
        }

        return f;
    }

    @Override
    public String toString() {
        return String.format("(TsmDefineNode %s %s)", sym, valueNode);
    }
}
