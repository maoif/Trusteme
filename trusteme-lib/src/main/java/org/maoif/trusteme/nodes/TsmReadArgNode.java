package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmSymbol;
import org.maoif.trusteme.types.TsmVoid;

/**
 * Read arguments from the current frame and save them into the lexical scope.
 */
public class TsmReadArgNode extends TsmNode {
    protected final TsmSymbol sym;
    protected final int argSlot;

    public TsmReadArgNode(int argSlot, TsmSymbol sym) {
        this.argSlot = argSlot;
        this.sym = sym;
    }

    /**
     * Reads the values from argument array and put it into lexical scope.
     * @param frame current call frame
     * @return TsmVoid
     */
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var arg = frame.getArguments()[this.argSlot];
        if (arg instanceof TsmExpr e) {
            // When we are here, we were just after a function call,
            // the frame at the call site was materialized,
            // and the slot length is equal to the number of arguments minus 1 here,
            // because of how we created this in the Parser.

            // args: lexical_scope arg1  arg2  ...
            // slots:              slot0 slot1 ...
            frame.getFrameDescriptor().setSlotKind(this.argSlot, FrameSlotKind.Object);
            frame.setObject(this.argSlot, new TsmPair(this.sym, e));
        } else throw new RuntimeException("Bad argument type");

        return TsmVoid.INSTANCE;
    }

    @Override
    public String toString() {
        return String.format("(TsmReadArgNode %s %d)", sym, argSlot);
    }
}
