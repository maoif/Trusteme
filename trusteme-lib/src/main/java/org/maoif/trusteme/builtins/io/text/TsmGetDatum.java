package org.maoif.trusteme.builtins.io.text;

import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "get-datum")
public class TsmGetDatum extends TsmReadBuiltinNode {
    public TsmGetDatum() {
        super("get-datum");
    }
}
