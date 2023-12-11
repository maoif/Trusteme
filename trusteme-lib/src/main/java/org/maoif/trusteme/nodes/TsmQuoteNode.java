package org.maoif.trusteme.nodes;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.types.*;

/**
 * Quote expressions just return the quoted data.
 *
 * TODO can we just use literal nodes?
 */
public class TsmQuoteNode extends TsmNode {
    private final TsmExpr value;

    public TsmQuoteNode(TsmExpr val) {
        this.value = val;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
