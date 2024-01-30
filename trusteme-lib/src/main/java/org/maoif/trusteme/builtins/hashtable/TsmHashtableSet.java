package org.maoif.trusteme.builtins.hashtable;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmHashtable;
import org.maoif.trusteme.types.TsmVoid;

@NodeInfo(shortName = "hashtable-set!")
public class TsmHashtableSet extends TsmBuiltinNode {
    public TsmHashtableSet() {
        super("hashtable-set!");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmVoid(frame);
    }

    @Override
    public TsmVoid executeTsmVoid(VirtualFrame frame) {
        // (hashtable-set! hashtable key obj)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 4)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmHashtable ht) {
            if (args[2] instanceof TsmExpr k) {
                if (args[3] instanceof TsmExpr v) {
                    ht.set(k, v);
                } else {
                    throw new RuntimeException("Not a Trusteme value: " + args[3]);
                }
            } else {
                throw new RuntimeException("Not a Trusteme value: " + args[2]);
            }
        } else {
            throw new RuntimeException("Not a hashtable: " + args[1]);
        }

        return TsmVoid.INSTANCE;
    }
}
