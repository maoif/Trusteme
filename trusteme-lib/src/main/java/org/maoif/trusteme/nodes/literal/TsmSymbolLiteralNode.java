package org.maoif.trusteme.nodes.literal;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.maoif.trusteme.nodes.TsmNode;
import org.maoif.trusteme.types.TsmSymbol;

public class TsmSymbolLiteralNode extends TsmNode  {
    public final TsmSymbol value;

    public TsmSymbolLiteralNode(TsmSymbol val) {
        this.value = val;
    }

    public TsmSymbolLiteralNode(String val) {
        this.value = new TsmSymbol(val);
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // TODO maybe return the value bound to this symbol?
        return value;
    }
}
