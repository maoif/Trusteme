package org.maoif;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Stopwatch;
import org.graalvm.polyglot.*;

import org.maoif.trusteme.TrustemeLanguage;
import org.maoif.trusteme.types.TsmExpr;
import org.maoif.trusteme.types.TsmFixnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class TrustemeLauncher {
    private static final String TSM = "tsm";

    private static Context context;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Expected a source file name.");
            System.exit(-1);
        }

        var p = Path.of(args[0]);
        if (Files.exists(p)) {
            if (!Files.isRegularFile(p)) {
                System.err.printf("File %s is not regular file\n", args[0]);
                System.exit(-1);
            }
        } else {
            System.err.printf("File %s not found\n", args[0]);
            System.exit(-1);
        }

        String[] tsmArgs = Arrays.copyOfRange(args, 1, args.length);

        context = Context.newBuilder(TSM)
                .arguments(TSM, tsmArgs)
                .in(System.in).out(System.out).allowAllAccess(true).build();

        // run code
        var entry = String.format("(trusteme-load-r6rs-top-level$7$11930 '\"%s\")", args[0]);
        context.eval(Source.create(TSM, entry));
    }
}
