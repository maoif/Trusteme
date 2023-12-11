package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmProcedure;

/**
 * Represents a Scheme lambda expression,
 * returns a TsmProcedure object when executed.
 */
@NodeField(name = "procedure", type = TsmProcedure.class)
public abstract class TsmLambdaLiteralNode extends TsmNode {
    public abstract TsmProcedure getProcedure();

    @Override
    public TsmProcedure executeTsmProcedure(VirtualFrame frame) {
        return null;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return null;
    }

}
