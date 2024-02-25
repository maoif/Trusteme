package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.types.*;

/**
 * Read arguments from the current frame, pack them into a list,
 * and save them into the lexical scope.
 */
public class TsmReadDotArgNode extends TsmReadArgNode {

    // the index that corresponds to the dot argument
    @CompilerDirectives.CompilationFinal
    private final int argIndex;

    public TsmReadDotArgNode(int argIndex, int argSlot, TsmSymbol sym) {
        super(argSlot, sym);
        this.argIndex = argIndex;
    }

    /**
     * Reads the values from argument array and put it into lexical scope.
     * @param frame current call frame
     * @return TsmVoid
     */
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var args = frame.getArguments();
        // Lexical scope only.
        if (args.length == 1) {
            frame.getFrameDescriptor().setSlotKind(this.argSlot, FrameSlotKind.Object);
            frame.setObject(this.argSlot, new TsmPair(this.sym, TsmNull.INSTANCE));

            return TsmNull.INSTANCE;
        }

        int i = this.argIndex;
        TsmPair res = new TsmPair((TsmExpr) args[i++]);
        TsmPair next = res;

        while (i < args.length) {
            next.setCdr(new TsmPair((TsmExpr) args[i++]));
            next = (TsmPair) next.cdr();
        }

        frame.getFrameDescriptor().setSlotKind(this.argSlot, FrameSlotKind.Object);
        frame.setObject(this.argSlot, new TsmPair(this.sym, res));

        return TsmVoid.INSTANCE;
    }

    @Override
    public String toString() {
        return String.format("(TsmReadDotArgNode %s %d)", sym, argSlot);
    }
}
