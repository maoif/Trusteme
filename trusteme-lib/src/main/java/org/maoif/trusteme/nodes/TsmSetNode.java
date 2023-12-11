package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.nodes.literal.TsmSymbolLiteralNode;
import org.maoif.trusteme.types.TsmExpr;

/**
 * (set! var val)
 */
public class TsmSetNode extends TsmNode {
    @Child
    private TsmSymbolLiteralNode sym;
    @Child
    private TsmNode val;

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // sym.executeXXX(frame);
        // Here sym, when executed, should return the
        // location where we can put in the new value
        var value = val.executeGeneric(frame);
        return null;
    }
}
