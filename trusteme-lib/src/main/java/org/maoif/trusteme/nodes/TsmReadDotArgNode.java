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
        TsmPair res = new TsmPair();

        int i = this.argIndex;
        while (i < args.length) {
            if (res.car() == TsmNull.INSTANCE)
                res.setCar((TsmExpr) args[i++]);
            else
                res.setCdr(new TsmPair((TsmExpr) args[i++]));
        }

        frame.getFrameDescriptor().setSlotKind(this.argSlot, FrameSlotKind.Object);
        frame.setObject(this.argSlot, res);

        return TsmVoid.INSTANCE;
    }
}
