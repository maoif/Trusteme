package org.maoif.trusteme.builtins;

import com.oracle.truffle.api.dsl.GenerateNodeFactory;
import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.nodes.TsmNode;

@NodeInfo(description = "Parent of all builtin nodes.")
@GenerateNodeFactory
public abstract class TsmBuiltinNode extends TsmNode  {
    public final String NAME;

    protected TsmBuiltinNode(String name) {
        this.NAME = name;
    }
}
