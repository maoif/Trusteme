package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.types.TsmSymbol;

public class TsmBadLetrecInitNode extends TsmNode {
    private TsmSymbol lhs;

    public TsmBadLetrecInitNode(TsmSymbol lhs) {
        this.lhs = lhs;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        throw new RuntimeException("Bad letrec init expression for " + lhs);
    }
}
