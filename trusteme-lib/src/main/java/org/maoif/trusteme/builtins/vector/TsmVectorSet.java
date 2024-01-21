package org.maoif.trusteme.builtins.vector;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmVector;
import org.maoif.trusteme.types.TsmVoid;

@NodeInfo(shortName = "vector-set!")
public class TsmVectorSet extends TsmBuiltinNode {
    public TsmVectorSet() {
        super("vector-set!");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmVoid(frame);
    }

    @Override
    public TsmVoid executeTsmVoid(VirtualFrame frame) {
        // (vector-set! vec index val)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 4)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmVector vec) {
            if (args[2] instanceof TsmFixnum index) {
                if (args[3] instanceof TsmExpr val) {
                    vec.set((int) index.get(), val);
                } else {
                    throw new RuntimeException("Not a Trusteme value: " + args[3]);
                }
            } else {
                throw new RuntimeException("Not a valid index: " + args[2]);
            }
        } else {
            throw new RuntimeException("Not a vector: " + args[1]);
        }

        return TsmVoid.INSTANCE;
    }
}
