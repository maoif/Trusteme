package org.maoif.trusteme.builtins.io;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;

import org.maoif.trusteme.builtins.TsmBuiltinNode;
import org.maoif.trusteme.types.TsmBool;
import org.maoif.trusteme.types.TsmString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@NodeInfo(shortName = "delete-file")
public class TsmDeleteFile extends TsmBuiltinNode {
    public TsmDeleteFile() {
        super("delete-file");
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return executeTsmBool(frame);
    }

    @Override
    public TsmBool executeTsmBool(VirtualFrame frame) {
        // (delete-file file) => #t if successful, #f if not
        Object[] args = frame.getArguments();
        if (args.length == 0)
            throw new RuntimeException("Lexical scope is lost");
        if (args.length != 2)
            throw new RuntimeException("invalid argument count in " + this.NAME);

        if (args[1] instanceof TsmString s) {
            var p = Path.of(s.get());
            if (Files.isRegularFile(p)) {
                try {
                    return TsmBool.get(Files.deleteIfExists(p));
                } catch (IOException e) {
                    return TsmBool.FALSE;
                }
            }

            return TsmBool.FALSE;
        }

        throw new RuntimeException("Not a string: " + args[1]);
    }

}
