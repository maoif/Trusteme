package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmSymbol;

// TODO optimize this, e.g., cache the result

/**
 * Symbol nodes return the bound value when executed.
 */
public class TsmSymbolNode extends TsmNode {
    private TsmSymbol sym;
    // TODO slot is not used
    private int slot;

    public int getSlot() {
        return this.slot;
    }

    public TsmSymbolNode(int slot, TsmSymbol sym) {
        this.slot = slot;
        this.sym = sym;
    }

    // TODO specialize, e.g., TsmProcedure
    // TODO cache
    @Override
    public Object executeGeneric(VirtualFrame virtualFrame) {
        Frame lexicalScope = virtualFrame;
        while (true) {
            int num = lexicalScope.getFrameDescriptor().getNumberOfSlots();
            for (int i = 1; i < num; i++) {
                Object o = lexicalScope.getObject(i);
                if (o instanceof TsmPair p && p.car() instanceof TsmSymbol s) {
                    if (s.get().equals(sym.get())) return p.cdr();
                } else throw new RuntimeException("Bad frame object type");
            }

            // TODO if lexicalScope is already the top frame and value not found, bug out.
            System.out.println("TsmSymbolNode.executeGeneric sym: " + sym.get());
            System.out.println(lexicalScope.getObject(0));

            lexicalScope = (Frame) lexicalScope.getObject(0);
        }

//        Frame frame = virtualFrame;
//        Object value = frame.getObject(slot);
//        while (value == null) {
//            frame = this.getLexicalScope(frame);
//            if (frame == null) {
//                throw new RuntimeException("Unknown variable: " + sym);
//            }
//
//            value = frame.getObject(slot);
//        }
//
//        return value;
    }

    private Frame getLexicalScope(Frame frame) {
        // the 1st arg always stores the environment (materialized frame)
        return (Frame) frame.getArguments()[0];
    }

    @Override
    public String toString() {
        return String.format("(TsmSymbolNode %s)", sym);
    }
}


