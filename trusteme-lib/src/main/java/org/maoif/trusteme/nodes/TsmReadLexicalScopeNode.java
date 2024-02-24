package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;

import org.maoif.trusteme.types.TsmSymbol;
import org.maoif.trusteme.types.TsmVoid;

public class TsmReadLexicalScopeNode extends TsmReadArgNode {

    public TsmReadLexicalScopeNode(int argSlot) {
        super(argSlot, null);
    }

    /**
     * Reads the values from argument array and put it into lexical scope.
     * @param frame current call frame
     * @return TsmVoid
     */
    @Override
    public Object executeGeneric(VirtualFrame frame) {
        var lexicalScope = frame.getArguments()[0];
        frame.getFrameDescriptor().setSlotKind(this.argSlot, FrameSlotKind.Object);
        frame.setObject(this.argSlot, lexicalScope);

        return TsmVoid.INSTANCE;
    }

    @Override
    public String toString() {
        return "TsmReadLexicalScopeNode";
    }
}
