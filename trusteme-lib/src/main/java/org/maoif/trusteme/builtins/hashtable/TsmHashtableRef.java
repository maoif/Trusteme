package org.maoif.trusteme.builtins.hashtable;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmHashtable;
import org.maoif.trusteme.types.TsmExpr;

@NodeInfo(shortName = "hashtable-ref")
public class TsmHashtableRef extends TsmBuiltinNode {
    public TsmHashtableRef() {
        super("hashtable-ref");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // (hashtable-ref hashtable key default)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 4)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmHashtable ht) {
            if (args[2] instanceof TsmExpr k) {
                if (ht.contains(k)) {
                    return ht.ref(k);
                } else {
                    return args[3];
                }
            } else {
                throw new RuntimeException("Not a Trusteme value: " + args[2]);
            }
        }

        throw new RuntimeException("Not a hashtable: " + args[1]);
    }
}
