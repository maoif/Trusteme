package org.maoif.trusteme.builtins.hashtable;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmEqHashtable;
import org.maoif.trusteme.types.TsmHashtable;

@NodeInfo(shortName = "make-eq-hashtable")
public class TsmMakeEqHashtable extends TsmBuiltinNode {

    public TsmMakeEqHashtable() {
        super("make-eq-hashtable");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmHashtable(frame);
    }

    @Override
    public TsmHashtable executeTsmHashtable(VirtualFrame frame) {
        return new TsmEqHashtable();
    }
}
