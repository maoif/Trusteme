package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmPair;
import org.maoif.trusteme.types.TsmSymbol;

import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;

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
                    if (s.get().equals(sym.get())) {
                        return p.cdr();
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
                    return v;
                }
            } else {
                lexicalScope = (Frame) prevFrame;
            }
        }
    }

    @Override
    public String toString() {
        return String.format("(TsmSymbolNode %s)", sym);
    }
}


