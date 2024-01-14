package org.maoif.trusteme.builtins.io.text;

import com.oracle.truffle.api.nodes.NodeInfo;

@NodeInfo(shortName = "put-datum")
public class TsmPutDatum extends TsmWriteBuiltinNode {
    public TsmPutDatum() {
        super("put-datum");
    }
}
