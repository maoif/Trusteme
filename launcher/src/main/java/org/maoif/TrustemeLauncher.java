package org.maoif;

import org.maoif.trusteme.TrustemeLanguage;

public class TrustemeLauncher {
    public static void main(String[] args) {
        System.out.println("Hello Trusteme");

        System.out.println();

        // TODO how to access TrustemeLanguage???
        TrustemeLanguage TSM = new TrustemeLanguage();
        System.out.println(TSM.parseOnly("(quote 1)"));
        System.out.println(TSM.parseOnly("(quote 1.12)"));
        System.out.println(TSM.parseOnly("(quote #f)"));
        System.out.println(TSM.parseOnly("(quote #t)"));
        System.out.println(TSM.parseOnly("(quote 3454.3242)"));
        System.out.println(TSM.parseOnly("(quote 9999999999994329499249392)"));

        System.out.println(TSM.parseOnly("(*)"));
        System.out.println(TSM.parseOnly("((lambda (x y z) (+ x y z)) '1 '2 '3)"));
        System.out.println(TSM.parseOnly("(define make-parameter$6$877 '#f)"));
        System.out.println(TSM.parseOnly("(+ '1 '2 '3)"));

        TSM.execute("(+ '1 '2 '3)");
    }
}
