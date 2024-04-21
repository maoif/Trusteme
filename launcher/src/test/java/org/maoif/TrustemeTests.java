package org.maoif;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
public class TrustemeTests {
    private static final String TSM = "tsm";
    private static final String[] fakeArgs = {"arg0", "arg1", "arg2"};

    private static Context context = Context.newBuilder(TSM)
            .arguments(TSM, fakeArgs)
            .in(System.in).out(System.out).allowAllAccess(true).build();

    private Value run(String str) {
        return context.eval(Source.create(TSM, str));
    }

    private void test(String str) {
        var res = run(str);
        if (res.toString().equals("#f")) {
            fail(String.format("Test failed:\n\t%s\n\t-> %s", str, res));
        }
    }

    private void testNot(String str) {
        var res = run(str);
        if (!res.toString().equals("#f")) {
            fail(String.format("Test failed:\n\t%s\n\t-> %s", str, res));
        }
    }

    @Test
    void testArithmetics() {
        test("(= (quote 3454.3242) '3454.3242)");
        test("(fx= (quote 10) '10)");
        test("(fx= '#b-101 '-5)");
        test("(= (+ '1 '2 '3) '6)");
    }

    @Test
    void testNumberComparisons() {
        test("(= '1 (fx*))");
        test("(= '1008 (fx* '42 '24))");
        test("(= '-1008 (fx* '-42 '24))");
        test("(= '317346835 (fx* '-5567 '#x-dead))");
        //        runAndPrint("(fx* '999 '9999 '9999999 '999999)"); // out of range
        test("(= '8 (fx/ '32 '2 '2))");
        //        runAndPrint("(fx=)"); // error
        test("(fx= '1)");
        test("(fx= '666 '666 '666)");
        testNot("(fx= '1 '2 '3 '6 '5)");

        test("(fx< '1)");
        test("(fx< '-664 '-663 '-662)");
        test("(fx< '664 '665 '666)");
        testNot("(fx< '666 '666 '666)");
        testNot("(fx< '1 '2 '3 '6 '5)");
        testNot("(fx< '1 '2 '3 '6 '6)");

        test("(fx<= '1)");
        test("(fx<= '666 '666 '666)");
        test("(fx<= '1 '2 '3 '6 '6)");

        test("(= '1 (fx/ '5 '3))");
        test("(= '1 (fxmod '-47 '2))");
        test("(= '0 (fxmod '-46 '2))");

        test("(= (fl+) '0.0)");
        test("(= (fl*) '1.0)");
        test("(= (fl+ '1.0 '3.14) (+ '1.0 '3.14))");
        test("(= (fl* '2.0 '3.14) '6.28)");
        test("(= (fl- '2.0) '-2.0)");
        test("(= (fl/ '2.0) '0.5)");
        test("(= (+) '0)");
        test("(= (*) '1)");
        test("(= '15 (+ '1 '2 '3 '4 '5))");
        test("(= (* '1 '2 '3 '4 '5) '120)");
        test("(= (+ '1.0 '3.14 '9) '13.14)");
        test("(= (/ '2.0) (fl/ '1.0 '2.0))");
        test("(= '-13 (- '1 '2 '3 '4 '5))");
        test("(= '25 (/ '50 '2))");
        test("(= '0.0 (- '2.0 '2.0))");

        test("(= '#b101 '#b101)");
        test("(= '-2.4 '-2.4)");
        test("(= '1.0 '1.0 '1)");
        testNot("(= '#xffffffffffffff '#xffffffffffffff '#xffffffffffffff '1)");
    }

    @Test
    void testPairs() {
        test("(equal? (car '('1 '2 '3))  ''1)");
        test("(equal? (car '('42 . '24)) ''42)");
        test("(equal? (cdr '('1 '2 '3)) '('2 '3))");
        test("(equal? (cdr '('42 . '24)) ''24)");
        test("(eq? (car (cons '1 '#f)) '1)");
        test("(equal? (cons '1 (cons '2 '())) '(1 2))");

        test("(pair? (cons '1 '2))");
        test("(pair? '(1 2 3))");
        test("(pair? '(1 2 . 3))");

        test("(not (pair? '#f))");
        test("(eq? '() (cddr '(1 2)))");

        run("(define ppp '(1 . 2))");
        test("(equal? ppp (cons '1 '2))");
        testNot("(eq? ppp (cons '1 '2))");
        run("(set-car! ppp '11)");
        run("(set-cdr! ppp '22)");
        test("(equal? ppp (cons '11 '22))");
        testNot("(eq? ppp (cons '11 '22))");
    }

    @Test
    void testLists() {
        testNot("(null? '1)");
        test("(null? '())");
        testNot("(null? '#(1 2 3))");
        testNot("(null? (lambda (x) x))");
        test("(eq? (list) '())");
        test("(list? (list '1 '#t '#f '2 '#(1 2 3)))");
        test("(list? '(1 2 3))");
        testNot("(list? '(1 2 . 3))");

        test("(eq? (length '()) '0)");
        test("(eq? '3 (length '(1 2 3)))");
        test("(= '5 (length '('#t '#f '12 '#xff '())))");
        //        runAndPrint("(length '('#t '#f '12 '#xff '() . bla))");
        // this is strange, but correct (= 7)
        test("(eq? '7 (length '('#t '#f '12 '#xff '() . 'bla)))");

        run("(define ll '(#t 1 2 3 . 4))");
        test("(eq? '#t (list-ref ll '0))");
        test("(eq? '1  (list-ref ll '1))");
        test("(eq? '2  (list-ref ll '2))");
        test("(eq? '3  (list-ref ll '3))");
        //        runAndPrint("(list-ref ll '4)"); // error

        // important for testing append with no args
        test("(apply append '())");
        test("(append)");
        test("(equal? (append '())     '())");
        test("(equal? (append '() '1)  '1)");
        test("(equal? (append '() '#f) '#F)");
        test("(equal? (append '(1) '1) '(1 . 1))");
        test("(equal? (append '(1) '(2) '(3))     '(1 2 3))");
        test("(equal? (append '(1) '(2) '(3 . 4)) '(1 2 3 . 4))");
        test("(equal? (append '(1) '(2) '(3) '4)  '(1 2 3 . 4))");
        test("(equal? (append '(2 4 6) '1)        '(2 4 6 . 1))");
        test("(equal? (append '(2 4 6) '() '() '())  '(2 4 6))");
        test("(equal? (append '(2 4 6) '() '(1) '()) '(2 4 6 1))");
        test("(equal? (append '(2 4 6) '() '() '(1)) '(2 4 6 1))");
        test("""
(equal?
  (append '(2 4 6) '(1 3 5) '(#f #t) '(#(1 2 3) #(1 2 3) #(1 2 3)))
  '(2 4 6 1 3 5 #f #t #(1 2 3) #(1 2 3) #(1 2 3)))
""");

        run("(define bb1 '(id e* ...))");
        run("(define bb2 '(#'(e* ...)))");
        test("(equal? bb1 '(id e* ...))");
        test("(equal? bb2 '((syntax (e* ...))))");
        test("(eq? bb1 bb1)");
        testNot("(eq? bb1 bb2)");

        test("(equal? '(6 . 66) (assoc '6 '((1 . 11) (6 . 66) (7 . 77))))");
        test("(equal? '(6 66 666) (assoc '6 '((1 . 11) (6 66 666) (7 . 77))))");

        test("(equal? '() (reverse '()))");
        test("(equal? '(3 2 1) (reverse '(1 2 3)))");

        // higher-order procedures
        test("(eq? '() (map odd? '()))");
        testNot("(exists odd? '())");
        test("(for-all odd? '())");

        run("(define lll1 '(1 2 3 4 5))");
        run("(define lll2 '(1 2 3 4 5))");
        run("(define lll3 '(1 2 3 4 5))");

        test("(equal? '(3 6 9 12 15) (map (lambda (x y z) (fx+ x y z)) lll1 lll2 lll3))");
        test("(equal? (map cons '(1 2) '(1 2)) '((1 . 1) (2 . 2)))");
        test("(exists (lambda (x)  (fx= '0 (fxmod x '2))) '(1 3 5 7 10))");
        testNot("(exists (lambda (x)  (fx= '0 (fxmod x '2))) '(1 3 5 7 11))");
        test("(for-all odd? '(1 3 5 7 11))");
        test("(equal? '(#f #t #f #t #f #t #f #t #f #t) (map (lambda (x) (fx= '0 (fxmod x '2))) '(1 2 3 4 5 6 7 8 9 10)))");

        test("(equal? (memp (lambda (x) (= x '1)) '(2 3 4 1)) '(1))");
        test("(equal? (memp odd? '(2 4 6 1 2 3 4)) '(1 2 3 4))");

        test("(equal? (memp odd? '(2 4 6 7)) '(7))");
        test("(equal? '(7 9) (memq '7 '(1 2 7 9)))");
        testNot("(memq '7 '())");
    }

    @Test
    void testVectors() {
        test("(equal? '#('1 '2 '3) (vector ''1 ''2 ''3))");
        test("(eq? (vector) '#())");
        test("(vector? (vector))");
        test("(vector? (vector '1 '#t '#f '2 '#(1 2 3)))");

        run("(define v (list->vector '(1 2 3 4 5 #t #f (nested list))))");
        test("(vector? v)");
        test("(fx= (vector-length v) '8)");
        test("(equal? (vector-ref v '7) '(nested list))");
        test("(vector-ref v '5)");
        run("(vector-set! v '5 '(bla bla))");
        test("(equal? '(bla bla) (vector-ref v '5))");

        test("(equal? (make-vector '0) '#())");
        test("(equal? (make-vector '3) '#( 0 0 0 ))");
        test("(equal? (make-vector '3 '1) '#(1 1 1))");

        test("(eq? '... (vector-ref '#(... ...) '0))");
        test("(eq? '... (vector-ref '#(... ...) '1))");
    }

    @Test
    void testHashtables() {
        run("(define ht (make-eq-hashtable))");
        run("(hashtable-set! ht '1 '11)");
        run("(hashtable-set! ht 'k 'v)");
        run("(hashtable-set! ht 'l '(#f #t))");
        // TODO handle primitive types
        // test("(eq? '11 (hashtable-ref ht '1 'def))");
        test("(eq? 'v (hashtable-ref ht 'k 'def))");
        test("(equal? '(#f #t) (hashtable-ref ht 'l 'def))");
        test("(eq? 'def (hashtable-ref ht '42 'def))");
    }

    @Test
    void testEqualities() {
        // equivalence predicates
        test("(eq? '() '())");
        test("(eq? '#() '#())");
        testNot("(eq? '(1) '())");
        testNot("(eq? '#() '#(#f))");
        test("(eq? '2 '2)");
        test("(eq? '#\\a '#\\a)");
        testNot("(eq? '3.14 '3.14)");
        test("(eq? 's 's)");

        test("(equal? '() '())");
        run("(define equal-list '(equal? '(1 2 odd?) '(1 2 odd?)))");
        test("(equal? '(1 2 3) '(1 2 3))");
        testNot("(equal? '#f '(1 2 3))");
        testNot("(equal? '(1 2 3) '(1 2 4))");
        testNot("(equal? '(1 2 odd?) '(1 2))");
        test("(equal? '(1 2 odd?) '(1 2 odd?))");
        test("(equal? (list '1 '2 odd? equal-list) (list '1 '2 odd? equal-list))");
        test("(equal? pair? pair?)");
        testNot("(equal? pair? odd?)");

        test("(equal? '(1 2 . 3) '(1 2  .  3))");
        test("(equal? '(1 2 . odd?) '(1 2 . odd?))");
        testNot("(equal? '(1 2 3) '(1 2  .  3))");
        testNot("(equal? '(1 2 . odd?) '(1 2 odd?))");

        test("(equal? '#() '#())");
        test("(equal? '#(1 2 3) '#(1 2 3))");
        testNot("(equal? '#f '#(1 2 3))");
        testNot("(equal? '#(1 2 3) '#(1 2 4))");
        testNot("(equal? '#(1 2 3) '#(1 2))");
    }

    @Test
    void defsAndUpdates() {
        run("(define xyz '42)");
        test("(= xyz '42)");
        run("(set! xyz '666)");
        test("(= xyz '666)");
    }

    @Test
    void functionDefs() {
        // function definition and recursion
        run("(define fact \n (lambda (x) \n (if (fx= x '1) \nx \n(fx* x (fact (fx- x '1))))))");
        test("(eq? '3628800 (fact '10))");
        test("(eq? (fact (fact '3)) '720)");

        run("""
(define fib
  ( lambda ( x ) 
    (if (fx< x '2) 
        '1
        (fx+ (fib (fx- x '1))
             (fib (fx- x '2))))))
""");
        test("(= (fib '4) '5)");
        test("(= (fib '6) '13)");
        test("(= (fib '10) '89)");
        test("(= (fib '20) '10946)");
        test("(= (fib '30) '1346269)");
    }

    @Test
    void testChars() {
        test("(char=? '#\\a)");
        test("(char=? '#\\a '#\\a)");
        testNot("(char=? '#\\a '#\\b)");
        test("(char<=? '#\\a '#\\a)");
        testNot("(char<? '#\\a '#\\a)");
        testNot("(char<? '#\\a '#\\a '#\\b '#\\d '#\\z)");
        test("(char<=? '#\\a '#\\a '#\\b '#\\d '#\\d '#\\z)");
        test("(char<=? '#\\a '#\\b)");
        testNot("(char>? '#\\a '#\\a)");
        test("(char>=? '#\\a '#\\a)");
        testNot("(char<=? '#\\a '#\\a '#\\b '#\\Z)");
        test("(char<=? '#\\Z '#\\a '#\\a '#\\b)");
        testNot("(char? '\"\")");
        testNot("(char? '\"a\")");
        test("(char? '#\\a)");
    }

    @Test
    void testStrings() {
        test("(equal? (make-string '0 '#\\a) '\"\")");
        test("(equal? '\"a\" (make-string '1 '#\\a))");
        test("(equal? '\"aaaaa\" (make-string '5 '#\\a))");

        run("(define mkstr (make-string '6 '#\\a))");
        test("(equal? mkstr '\"aaaaaa\")");
        run("(string-set! mkstr '0 '#\\A)");
        run("(string-set! mkstr '4 '#\\E)");
        test("(equal? mkstr '\"AaaaEa\")");

        test("(equal? (string) '\"\")");
        test("(equal? '\"a\" (string '#\\a))");
        test("(equal? '\"abc\" (string '#\\a '#\\b '#\\c))");

        test("(equal? '\"\" (string-append))");
        test("(equal? '\"123#$%\" (string-append '\"\" '\"123\" '\"#$%\"))");
        test("(equal? '\"123#$%abcABC\" (string-append '\"\" '\"123\" '\"#$%\" '\"abcABC\"))");
    }

    @Test
    void testApplications() {
        test("(eq? '10 ((lambda (w x y z) (+ w x y z)) '1 '2 '3 '4))");
        test("(eq? '10 ((lambda (w) ((lambda (x) ((lambda (y) ((lambda (z) (+ w x y z)) '4)) '3)) '2)) '1))");
        test("""
                (eq? '10 
                ((lambda (w) 
                   ((lambda (x) 
                     ((lambda (y) 
                       ((lambda (z) (+ w x y z)) 
                         '4)) 
                      '3)) 
                    '2)) 
                 '1)
                )""");

        test("(equal? ((lambda args args) '1 '2 '#t) '(1 2 #t))");

        run("""
(define huge-times-20
  (lambda (t$58$527 t$58$528 t$58$525 t$58$526 t$58$523 t$58$524
                           t$58$521 t$58$522 t$58$519 t$58$520 t$58$517 t$58$518
                           t$58$515 t$58$516 t$58$513 t$58$514 t$58$511 t$58$512
                           t$58$509 t$58$510)
                    ((lambda (t$58$567 t$58$568 t$58$565 t$58$566 t$58$563 t$58$564
                              t$58$561 t$58$562 t$58$559 t$58$560 t$58$557 t$58$558
                              t$58$555 t$58$556 t$58$553 t$58$554 t$58$551 t$58$552
                              t$58$549 t$58$550)
                       ((lambda (t$58$607 t$58$608 t$58$605 t$58$606 t$58$603 t$58$604
                                 t$58$601 t$58$602 t$58$599 t$58$600 t$58$597 t$58$598
                                 t$58$595 t$58$596 t$58$593 t$58$594 t$58$591 t$58$592
                                 t$58$589 t$58$590)
                          ((lambda (t$58$647 t$58$648 t$58$645 t$58$646 t$58$643 t$58$644
                                    t$58$641 t$58$642 t$58$639 t$58$640 t$58$637 t$58$638
                                    t$58$635 t$58$636 t$58$633 t$58$634 t$58$631 t$58$632
                                    t$58$629 t$58$630)
                             ((lambda (t$58$687 t$58$688 t$58$685 t$58$686 t$58$683 t$58$684
                                       t$58$681 t$58$682 t$58$679 t$58$680 t$58$677 t$58$678
                                       t$58$675 t$58$676 t$58$673 t$58$674 t$58$671 t$58$672
                                       t$58$669 t$58$670)
                                ((lambda (t$58$727 t$58$728 t$58$725 t$58$726 t$58$723
                                          t$58$724 t$58$721 t$58$722 t$58$719 t$58$720
                                          t$58$717 t$58$718 t$58$715 t$58$716 t$58$713
                                          t$58$714 t$58$711 t$58$712 t$58$709 t$58$710)
                                   ((lambda (t$58$767 t$58$768 t$58$765 t$58$766 t$58$763
                                             t$58$764 t$58$761 t$58$762 t$58$759 t$58$760
                                             t$58$757 t$58$758 t$58$755 t$58$756 t$58$753
                                             t$58$754 t$58$751 t$58$752 t$58$749 t$58$750)
                                      ((lambda (t$58$807 t$58$808 t$58$805 t$58$806 t$58$803
                                                t$58$804 t$58$801 t$58$802 t$58$799 t$58$800
                                                t$58$797 t$58$798 t$58$795 t$58$796 t$58$793
                                                t$58$794 t$58$791 t$58$792 t$58$789 t$58$790)
                                         ((lambda (t$58$847 t$58$848 t$58$845 t$58$846
                                                   t$58$843 t$58$844 t$58$841 t$58$842
                                                   t$58$839 t$58$840 t$58$837 t$58$838
                                                   t$58$835 t$58$836 t$58$833 t$58$834
                                                   t$58$831 t$58$832 t$58$829 t$58$830)
                                            ((lambda (t$58$887 t$58$888 t$58$885 t$58$886
                                                      t$58$883 t$58$884 t$58$881 t$58$882
                                                      t$58$879 t$58$880 t$58$877 t$58$878
                                                      t$58$875 t$58$876 t$58$873 t$58$874
                                                      t$58$871 t$58$872 t$58$869 t$58$870)
                                               ((lambda (t$58$927 t$58$928 t$58$925 t$58$926
                                                         t$58$923 t$58$924 t$58$921 t$58$922
                                                         t$58$919 t$58$920 t$58$917 t$58$918
                                                         t$58$915 t$58$916 t$58$913 t$58$914
                                                         t$58$911 t$58$912 t$58$909 t$58$910)
                                                  ((lambda (t$58$967 t$58$968 t$58$965
                                                            t$58$966 t$58$963 t$58$964
                                                            t$58$961 t$58$962 t$58$959
                                                            t$58$960 t$58$957 t$58$958
                                                            t$58$955 t$58$956 t$58$953
                                                            t$58$954 t$58$951 t$58$952
                                                            t$58$949 t$58$950)
                                                     ((lambda (t$58$1007 t$58$1008 t$58$1005
                                                               t$58$1006 t$58$1003 t$58$1004
                                                               t$58$1001 t$58$1002 t$58$999
                                                               t$58$1000 t$58$997 t$58$998
                                                               t$58$995 t$58$996 t$58$993
                                                               t$58$994 t$58$991 t$58$992
                                                               t$58$989 t$58$990)
                                                        ((lambda (t$58$1047 t$58$1048
                                                                  t$58$1045 t$58$1046
                                                                  t$58$1043 t$58$1044
                                                                  t$58$1041 t$58$1042
                                                                  t$58$1039 t$58$1040
                                                                  t$58$1037 t$58$1038
                                                                  t$58$1035 t$58$1036
                                                                  t$58$1033 t$58$1034
                                                                  t$58$1031 t$58$1032
                                                                  t$58$1029 t$58$1030)
                                                           ((lambda (t$58$1087 t$58$1088
                                                                     t$58$1085 t$58$1086
                                                                     t$58$1083 t$58$1084
                                                                     t$58$1081 t$58$1082
                                                                     t$58$1079 t$58$1080
                                                                     t$58$1077 t$58$1078
                                                                     t$58$1075 t$58$1076
                                                                     t$58$1073 t$58$1074
                                                                     t$58$1071 t$58$1072
                                                                     t$58$1069 t$58$1070)
                                                              ((lambda (t$58$1127 t$58$1128
                                                                        t$58$1125 t$58$1126
                                                                        t$58$1123 t$58$1124
                                                                        t$58$1121 t$58$1122
                                                                        t$58$1119 t$58$1120
                                                                        t$58$1117 t$58$1118
                                                                        t$58$1115 t$58$1116
                                                                        t$58$1113 t$58$1114
                                                                        t$58$1111 t$58$1112
                                                                        t$58$1109 t$58$1110)
                                                                 ((lambda (t$58$1167
                                                                           t$58$1168
                                                                           t$58$1165
                                                                           t$58$1166
                                                                           t$58$1163
                                                                           t$58$1164
                                                                           t$58$1161
                                                                           t$58$1162
                                                                           t$58$1159
                                                                           t$58$1160
                                                                           t$58$1157
                                                                           t$58$1158
                                                                           t$58$1155
                                                                           t$58$1156
                                                                           t$58$1153
                                                                           t$58$1154
                                                                           t$58$1151
                                                                           t$58$1152
                                                                           t$58$1149
                                                                           t$58$1150)
                                                                    ((lambda (t$58$1207
                                                                              t$58$1208
                                                                              t$58$1205
                                                                              t$58$1206
                                                                              t$58$1203
                                                                              t$58$1204
                                                                              t$58$1201
                                                                              t$58$1202
                                                                              t$58$1199
                                                                              t$58$1200
                                                                              t$58$1197
                                                                              t$58$1198
                                                                              t$58$1195
                                                                              t$58$1196
                                                                              t$58$1193
                                                                              t$58$1194
                                                                              t$58$1191
                                                                              t$58$1192
                                                                              t$58$1189
                                                                              t$58$1190)
                                                                       ((lambda (t$58$1247
                                                                                 t$58$1248
                                                                                 t$58$1245
                                                                                 t$58$1246
                                                                                 t$58$1243
                                                                                 t$58$1244
                                                                                 t$58$1241
                                                                                 t$58$1242
                                                                                 t$58$1239
                                                                                 t$58$1240
                                                                                 t$58$1237
                                                                                 t$58$1238
                                                                                 t$58$1235
                                                                                 t$58$1236
                                                                                 t$58$1233
                                                                                 t$58$1234
                                                                                 t$58$1231
                                                                                 t$58$1232
                                                                                 t$58$1229
                                                                                 t$58$1230)
                                                                          ((lambda (t$58$1287
                                                                                    t$58$1288
                                                                                    t$58$1285
                                                                                    t$58$1286
                                                                                    t$58$1283
                                                                                    t$58$1284
                                                                                    t$58$1281
                                                                                    t$58$1282
                                                                                    t$58$1279
                                                                                    t$58$1280
                                                                                    t$58$1277
                                                                                    t$58$1278
                                                                                    t$58$1275
                                                                                    t$58$1276
                                                                                    t$58$1273
                                                                                    t$58$1274
                                                                                    t$58$1271
                                                                                    t$58$1272
                                                                                    t$58$1269
                                                                                    t$58$1270)
                                                                             (fx+
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510
                                                                              t$58$510))
                                                                           '1 '2 '3 '4 '5 '6
                                                                           '7 '8 '9 '10 '11
                                                                           '12 '13 '14 '15
                                                                           '16 '17 '18 '19
                                                                           '20))
                                                                        '1 '2 '3 '4 '5 '6 '7
                                                                        '8 '9 '10 '11 '12 '13
                                                                        '14 '15 '16 '17 '18
                                                                        '19 '20))
                                                                     '1 '2 '3 '4 '5 '6 '7 '8
                                                                     '9 '10 '11 '12 '13 '14
                                                                     '15 '16 '17 '18 '19
                                                                     '20))
                                                                  '1 '2 '3 '4 '5 '6 '7 '8 '9
                                                                  '10 '11 '12 '13 '14 '15 '16
                                                                  '17 '18 '19 '20))
                                                               '1 '2 '3 '4 '5 '6 '7 '8 '9 '10
                                                               '11 '12 '13 '14 '15 '16 '17
                                                               '18 '19 '20))
                                                            '1 '2 '3 '4 '5 '6 '7 '8 '9 '10
                                                            '11 '12 '13 '14 '15 '16 '17 '18
                                                            '19 '20))
                                                         '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11
                                                         '12 '13 '14 '15 '16 '17 '18 '19
                                                         '20))
                                                      '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12
                                                      '13 '14 '15 '16 '17 '18 '19 '20))
                                                   '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13
                                                   '14 '15 '16 '17 '18 '19 '20))
                                                '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13
                                                '14 '15 '16 '17 '18 '19 '20))
                                             '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14
                                             '15 '16 '17 '18 '19 '20))
                                          '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15
                                          '16 '17 '18 '19 '20))
                                       '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15 '16
                                       '17 '18 '19 '20))
                                    '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15 '16
                                    '17 '18 '19 '20))
                                 '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15 '16 '17
                                 '18 '19 '20))
                              '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15 '16 '17
                              '18 '19 '20))
                           '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15 '16 '17
                           '18 '19 '20))
                        '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15 '16 '17
                        '18 '19 '20))
                     '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15 '16 '17
                     '18 '19 '20)))
""");

        test("(= '20 (huge-times-20 '0 '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15 '16 '17 '18 '1))");
    }

    @Test
    void testApply() {
        test("(eq? '19 (apply fx+ '1 '1 '1 '1 '(1 2 3 #b100 #x5)))");
    }

    @Test
    void testMultipleReturnValues() {
        test("""
(eq? '1
(call-with-values 
  (lambda () (values '1 '2 '3)) 
  (lambda (x y z) x)))
""");
        test("""
(eq? '2
(call-with-values 
  (lambda () (values '1 '2 '3)) 
  (lambda (x y z) y)))
""");
        test("""
(eq? '3
(call-with-values 
  (lambda () (values '1 '2 '3)) 
  (lambda (x y z) z)))
""");
        test("""
(=
(call-with-values 
  (lambda () (values '1 '2 '3)) 
  (lambda (x y z) (fx+ x y z)))
'6)
""");
    }

    @Test
    void testClosures() {
        // closure
        run("(define addn (lambda (n) (lambda (x) (+ x n))))");
        run("(define add-2 (addn '2))");
        run("(define add-3 (addn '3))");

        test("(fx= (add-2 '1) '3)");
        test("(fx= (add-3 '1) '4)");
    }

    @Test
    void testTailCallOptimization() {
        // All TsmNodes have to catch TCE when executing non-tail nodes.

        // check if TCE in define node is handled
        run("""
(define bad-define? (if '#t (void) (void)))
                """);
        // otherwise may be undefined when the above
        // is run as a non-tail expression
        test("(eq? bad-define? (void))");

        // check if TCE in begin node is handled
        run("""
(define bad-begin? (begin (if '#t (void) (void))
                          'correct-value))
                """);
        // otherwise may be void
        test("(eq? bad-begin? 'correct-value)");

        // check if TCE in set node is handled
        run("(define bad-set!? '1)");
        run("""
(set! bad-set!? (if '#t (void) (void)))
                """);
        // otherwise may be undefined
        test("(eq? bad-set!? (void))");
    }

    @Test
    void testLetrec() {
        // The following is buggy in Chez.
        //        run("""
        //(letrec ([x '1]
        //         [y (add1 x)])
        //  y)""");

        // TODO the following reports `Bad argument type` (`f`),
        // Report `f` is not bound instead?
        //        run("""
        //(letrec ([g (lambda (x) (+ '1 f))]
        //         [f (g '1)])
        //  f)
        //                """);

        test("""
(letrec ([f (lambda (y) (g y))]
         [g (lambda (x) (if (fx= x '1)
                            'done
                            (f (fx- x '1))))])
  (eq? (f '4) 'done))
                """);

        test("""
(letrec ((rec-even?
          (lambda (n)
            (if (zero? n)
                '#t
                (rec-odd? (fx- n '1)))))
         (rec-odd?
          (lambda (n)
            (if (zero? n)
                '#f
                (rec-even? (fx- n '1))))))
  (rec-odd? (fx+ '1 '1 '1)))
                """);
    }

    @Test
    void testIO() {
        run("(define v (list->vector '(1 2 3 4 5 #t #f (nested list))))");
        run("(define ll  '(1 2 3 4 5 #t #f (nested list)))");

        run("(define pout (open-output-file '\"out.ss\"))");
        test("(port? pout)");
        test("(output-port? pout)");
        test("(textual-port? pout)");
        testNot("(input-port? pout)");
        testNot("(binary-port? pout)");

        run("(write pout ll)");
        run("(put-datum pout v)");
        run("(close-port pout)");

        run("(define pin1 (open-input-file '\"out.ss\"))");
        test("(port? pin1)");
        test("(input-port? pin1)");
        test("(textual-port? pin1)");
        testNot("(output-port? pin1)");
        testNot("(binary-port? pin1)");

        test("(equal? ll (read pin1))");
        test("(equal? v (read pin1))");
        test("(equal? (eof-object) (get-datum pin1))");
        run("(close-port pin1)");

        run("(delete-file '\"out.ss\")");
    }

    @Test
    void testPrims() {
        test("(eq? '() (map odd? '()))");
        testNot("(exists odd? '())");
        test("(for-all odd? '())");

        test("(equal? (command-line) '(\"arg0\" \"arg1\" \"arg2\"))");

        // $make-list-builder
        run("(define lb ($make-list-builder))");
        test("(eq? '() (lb))");
        run("(lb '1)");
        run("(lb '())");
        run("(lb '#(#f #t))");
        run("(lb '(1 2 3 '()))");
        test("(equal? (lb) '(1 () #(#f #t) (1 2 3 '())))");

        test("(equal? '(1) (list-copy-improper '(1)))");
        test("(equal? '(1 2 3 . 5) (list-copy-improper '(1 2 3 . 5)))");
        test("(equal? '(1 2 3 5) (list-copy-improper '(1 2 3 5)))");
        test("(equal? '(#f #t 1 2 3 . 5) (list-copy-improper '(#f #t 1 2 3 . 5)))");

        test("(equal? (vector->list '#()) '())");
        test("(equal? (vector->list '#(1 2 3)) '(1 2 3))");
        test("(equal? (vector->list '#(1 2 3 (4 5 6) #(11 22 33) #t #f)) '(1 2 3 (4 5 6) #(11 22 33) #t #f))");

        test("(equal? (list->vector '()) '#())");
        test("(equal? (list->vector '(1 2 3)) '#(1 2 3))");
        test("(equal? (list->vector '(1 2 3 (4 5 6) #(11 22 33))) '#(1 2 3 (4 5 6) #(11 22 33)))");

        test("(equal? (cons* '1) '1)");
        test("(equal? (cons* '1 '2) '(1 . 2))");
        test("(equal? (cons* '1 '2 '3) '(1 2 . 3))");
        test("(equal? (cons* '1 '2 '3 '()) '(1 2 3))");

        run("(set-symbol-value! 'blabla 'damn)");
        test("(eq? 'damn (symbol-value 'blabla))");
        run("(set-symbol-value! 'blabla 'damn1)");
        test("(eq? 'damn1 (symbol-value 'blabla))");

        test("(equal? '\"0\" (number->string '0))");
        test("(equal? '\"1\" (number->string '1))");
        test("(equal? '\"-1\" (number->string '-1))");
        test("(equal? '\"3.14\"  (number->string '3.14))");
        test("(equal? '\"-3.14\" (number->string '-3.14))");
        test("(equal? '\"7\"  (number->string '#b111))");
        test("(equal? '\"-7\" (number->string '#b-111))");
        test("(equal? '\"3735928495\"  (number->string '#xdeadbeaf))");
        test("(equal? '\"-3735928495\" (number->string '#x-deadbeaf))");
        test("(equal? '\"deadbeaf\"  (number->string '#xdeadbeaf '16))");
        test("(equal? '\"-deadbeaf\" (number->string '#x-deadbeaf '16))");
        // FIXME
//        test("(eq? '\"295147905179352825855\" (number->string '#xfffffffffffffffff))");
//        test("(eq? '\"fffffffffffffffff\" (number->string '#xfffffffffffffffff '16))");

        // fold-left
        // fold-right
    }
}
