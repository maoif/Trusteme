package org.maoif.trusteme.builtins.vector;

import com.oracle.truffle.api.frame.VirtualFrame;

import com.oracle.truffle.api.nodes.NodeInfo;
import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmFixnum;
import org.maoif.trusteme.types.TsmVector;

@NodeInfo(shortName = "vector-ref")
public class TsmVectorRef extends TsmBuiltinNode {
    public TsmVectorRef() {
        super("vector-ref");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        // (vector-ref vec index)
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 3)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmVector vec) {
            if (args[2] instanceof TsmFixnum index) {
                return vec.ref((int) index.get());
            } else {
                throw new RuntimeException("Not a valid index: " + args[2]);
            }
        } else {
            throw new RuntimeException("Not a vector: " + args[1]);
        }
    }
}
