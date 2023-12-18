package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.dsl.NodeChild;

@NodeChild("opnd")
public abstract class TsmCdrBuiltinNode extends TsmBuiltinNode {
    protected TsmCdrBuiltinNode() {
        super("cdr");
    }
}
