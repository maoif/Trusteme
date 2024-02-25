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
import java.util.concurrent.TimeUnit;

public class TrustemeLauncher {
    private static final String TSM = "tsm";
//    static HostAccess access = HostAccess.newBuilder().
//            targetTypeMapping(TsmFixnum.class, Long.class,
//                    (Predicate<TsmFixnum>) input -> true,
//                    (Function<TsmFixnum, Long>) TsmFixnum::get).
//            allowPublicAccess(true).
//            allowAllImplementations(true).
//            allowAllClassImplementations(true).
//            allowArrayAccess(true).
//            allowListAccess(true).
//            allowBufferAccess(true).
//            allowIterableAccess(true).
//            allowIteratorAccess(true).
//            allowMapAccess(true).
//            allowAccessInheritance(true).build();

    static Context context = Context.newBuilder(TSM)
            .in(System.in).out(System.out).allowAllAccess(true).build();

    public static void main(String[] args) {
        test("(= (quote 3454.3242) '3454.3242)");
        test("(fx= (quote 10) '10)");
        test("(fx= '#b-101 '-5)");
        test("(= (+ '1 '2 '3) '6)");

        test("(equal? (car '('1 '2 '3))  ''1)");
        test("(equal? (car '('42 . '24)) ''42)");
        test("(equal? (cdr '('1 '2 '3)) '('2 '3))");
        test("(equal? (cdr '('42 . '24)) ''24)");

        test("(eq? (car (cons '1 '#f)) '1)");
        test("(equal? (cons '1 (cons '2 '())) '(1 2))");
        test("(equal? '#('1 '2 '3) (vector ''1 ''2 ''3))");

        testNot("(null? '1)");
        test("(null? '())");
        testNot("(null? '#(1 2 3))");
        testNot("(null? (lambda (x) x))");

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

        run("(define make-parameter$6$877 '#f)");
//        runAndPrint("make-parameter$6$877");
//        runAndPrint("(set! xyz '42)"); // error
        run("(define xyz '42)");
        test("(= xyz '42)");
        run("(set! xyz '666)");
        test("(= xyz '666)");
//        runAndPrint("xyz");

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

//        Stopwatch sw = Stopwatch.createStarted();
//        runAndPrint("(fib '25)");
//        sw.stop();
//        System.out.println("time: " + sw.elapsed(TimeUnit.MILLISECONDS));
//
//        sw.reset();
//        sw.start();
//        runAndPrint("(fib '25)");
//        sw.stop();
//        System.out.println("time: " + sw.elapsed(TimeUnit.MILLISECONDS));
//
//        sw.reset();
//        sw.start();
//        runAndPrint("(fib '25)");
//        sw.stop();
//        System.out.println("time: " + sw.elapsed(TimeUnit.MILLISECONDS));

        // thunk
//        runAndPrint("""
//( (lambda  () (fact (fib '5))) )
//""");
//        runAndPrint("""
//(begin (define th (lambda () (fact (fib '5))))
//       (th))
//""");
//        // TODO dot arg
//        runAndPrint("""
//(begin (define f1 (lambda (x . y) x))
//       (f1 '#f '#t '() '#\\A))
//""");
//        runAndPrint("""
//(begin (define f2 (lambda (x . y)  (car y)))
//       (f2 '#f '#t '() '#\\A))
//""");
//        runAndPrint("""
//(begin (define f2 (lambda (x . y)  (cdr y)))
//       (f2 '#f '#t '() '#\\A))
//""");
//
//        runAndPrint("(define fact-fake \n (lambda (x n) \n (if (fx= x '1) \n n \n (fact-fake (fx- x '1) (fx+ x n)))))");
//        runAndPrint("(fact-fake '10000 '0)"); // stack overflow
//        runAndPrint("(fact-fake '1500 '0)");

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
//        sw.reset();
//        sw.start();
        runAndPrint("(huge-times-20 '0 '1 '2 '3 '4 '5 '6 '7 '8 '9 '10 '11 '12 '13 '14 '15 '16 '17 '18 '1)");
//        sw.stop();
//        System.out.println("time: " + sw.elapsed(TimeUnit.MILLISECONDS));

        test("(eq? (length '()) '0)");
        test("(eq? '3 (length '(1 2 3)))");
        test("(= '5 (length '('#t '#f '12 '#xff '())))");
//        runAndPrint("(length '('#t '#f '12 '#xff '() . bla))");
        // this is strange, but correct (= 7)
        runAndPrint("(length '('#t '#f '12 '#xff '() . 'bla))");

        run("(define ll '(#t 1 2 3 . 4))");
//        runAndPrint("ll");
        test("(eq? '#t (list-ref ll '0))");
        test("(eq? '1  (list-ref ll '1))");
        test("(eq? '2  (list-ref ll '2))");
        test("(eq? '3  (list-ref ll '3))");
//        runAndPrint("(list-ref ll '4)"); // error

        run("(define v (list->vector '(1 2 3 4 5 #t #f (nested list))))");
        test("(vector? v)");
        test("(= '8 (vector-length v))");
        runAndPrint("""
(call-with-values 
  (lambda () (values '1 '2 '3)) 
  (lambda (x y z) (values x y z)))
""");
        test("""
(=
(call-with-values 
  (lambda () (values '1 '2 '3)) 
  (lambda (x y z) (fx+ x y z)))
'6)
""");

        test("(eq? '19 (apply fx+ '1 '1 '1 '1 '(1 2 3 #b100 #x5)))");

//        runAndPrint("(define pin (open-input-file '\"1.ss\"))");
//        runAndPrint("pin");
//        runAndPrint("(read pin)");
//        runAndPrint("(read pin)");
//        runAndPrint("(read pin)");
//        runAndPrint("(read pin)");
//        runAndPrint("(read pin)");
//        runAndPrint("(read pin)");
//        runAndPrint("(read pin)");

//        runAndPrint("(read)");

        run("(define pout (open-output-file '\"out.ss\"))");
        runAndPrint("pout");
        test("(port? pout)");
        testNot("(input-port? pout)");
        test("(output-port? pout)");
        test("(textual-port? pout)");
        testNot("(binary-port? pout)");

        run("(write pout ll)");
        run("(put-datum pout v)");
        run("(close-port pout)");

        run("(define pin1 (open-input-file '\"out.ss\"))");
        test("(port? pin1)");
        test("(input-port? pin1)");
        testNot("(output-port? pin1)");
        test("(textual-port? pin1)");
        testNot("(binary-port? pin1)");

//        runAndPrint("pin1");
        runAndPrint("(read pin1)");
        runAndPrint("(read pin1)");
        runAndPrint("(get-datum pin1)");
        runAndPrint("(close-port pin1)");

        run("(define ppp '(1 . 2))");
        test("(equal? ppp (cons '1 '2))");
        testNot("(eq? ppp (cons '1 '2))");
        run("(set-car! ppp '11)");
        run("(set-cdr! ppp '22)");
        test("(equal? ppp (cons '11 '22))");
        testNot("(eq? ppp (cons '11 '22))");

        run("(define odd?  (lambda (x) (fx= '1 (fxmod x '2))))");
        run("(define even? (lambda (x) (fx= '0 (fxmod x '2))))");

        run("(define lll1 '(1 2 3 4 5))");
        run("(define lll2 '(1 2 3 4 5))");
        run("(define lll3 '(1 2 3 4 5))");
//        runAndPrint("(map (lambda (x y z) (fx+ x y z)) lll1 lll2 lll3)");
//        runAndPrint("(exists (lambda (x)  (fx= '0 (fxmod x '2))) '(1 3 5 7 10))");
//        runAndPrint("(exists (lambda (x)  (fx= '0 (fxmod x '2))) '(1 3 5 7 11))");
//        runAndPrint("(for-all odd? '(1 3 5 7 11))");
//        runAndPrint("(map (lambda (x) (fx= '0 (fxmod x '2))) '(1 2 3 4 5 6 7 8 9 10))");
//
//        test("(eq? '() (map odd? '()))");
//        testNot("(exists odd? '())");
//        test("(for-all odd? '())");
//        test("(equal? (map cons '(1 2) '(1 2)) '((1 . 1) (2 . 2)))");

        run("(define v (list->vector '(1 2 3 4 5 #t #f (nested list))))");
//        runAndPrint("v");
        test("(vector? v)");
        test("(fx= (vector-length v) '8)");
        test("(equal? (vector-ref v '7) '(nested list))");
        test("(vector-ref v '5)");
        run("(vector-set! v '5 '(bla bla))");
        test("(equal? '(bla bla) (vector-ref v '5))");

        test("(equal? ((lambda args args) '1 '2 '#t) '(1 2 #t))");

        run("(define bb1 '(id e* ...))");
        run("(define bb2 '(#'(e* ...)))");
        test("(equal? bb1 '(id e* ...))");
        test("(equal? bb2 '((syntax (e* ...))))");

        run("(define ... '...)");
        test("(eq? ... '...)");

        test("(eq? bb1 bb1)");
        testNot("(eq? bb1 bb2)");
        test("(procedure? odd?)");

        test("(= '#b101 '#b101)");
        test("(= '-2.4 '-2.4)");
        test("(= '1.0 '1.0 '1)");
        testNot("(= '#xffffffffffffff '#xffffffffffffff '#xffffffffffffff '1)");

        test("(eq? (list) '())");
//        runAndPrint("(list '1 '#t '#f '2 '#(1 2 3))");
        test("(list? (list '1 '#t '#f '2 '#(1 2 3)))");

        test("(eq? (vector) '#())");
        test("(vector? (vector))");
        test("(vector? (vector '1 '#t '#f '2 '#(1 2 3)))");

        test("(pair? (cons '1 '2))");
        test("(pair? '(1 2 3))");
        test("(pair? '(1 2 . 3))");
        test("(list? '(1 2 3))");
        testNot("(list? '(1 2 . 3))");

//        runAndPrint("(undefined-var 'x)");
//        runAndPrint("(error 'blabla '\"sth bad\")");
        runAndPrint("""
(letrec ([f (lambda (y) (g y))]
         [g (lambda (x) (if (fx= x '1)
                            'done
                            (f (fx- x '1))))])
  (f '4))
                """);
        runAndPrint("""
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
        // The following is buggy in Chez.
//        runAndPrint("""
//(letrec ([x '1]
//         [y (add1 x)])
//  y)""");
        // TODO the following reports `Bad argument type` (`f`),
        // Report `f` is not bound instead?
//        runAndPrint("""
//(letrec ([g (lambda (x) (+ '1 f))]
//         [f (g '1)])
//  f)
//                """);

        run("(define ht (make-eq-hashtable))");
//        runAndPrint("ht");
        run("(hashtable-set! ht '1 '11)");
        run("(hashtable-set! ht 'k 'v)");
        run("(hashtable-set! ht 'l '(#f #t))");
        runAndPrint("(hashtable-ref ht '1 'def)");
        // TODO handle primitive types
//        test("(eq? '11 (hashtable-ref ht '1 'def))");
        test("(eq? 'v (hashtable-ref ht 'k 'def))");
        test("(equal? '(#f #t) (hashtable-ref ht 'l 'def))");
        test("(eq? 'def (hashtable-ref ht '42 'def))");

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

        // check if TCE in define node is handled
        run("""
(define bad-define? (if '#t (void) (void)))
                """);
        runAndPrint("bad-define?");

        // check if TCE in begin node is handled
        run("""
(define bad-begin? (begin (if '#t (void) (void))
                          'correct-value))
                """);
        runAndPrint("bad-begin?");

        // check if TCE in set node is handled
        run("(define bad-set!? '1)");
        run("""
(set! bad-set!?(if '#t (void) (void)))
                """);
        runAndPrint("bad-set!?");

        test("(equal? (memp (lambda (x) (= x '1)) '(2 3 4 1)) '(1))");
        test("(equal? (memp odd? '(2 4 6 1 2 3 4)) '(1 2 3 4))");

        // closure
        run("(define addn (lambda (n) (lambda (x) (+ x n))))");
        run("(define add-2 (addn '2))");
        run("(define add-3 (addn '3))");

        test("(fx= (add-2 '1) '3)");
        test("(fx= (add-3 '1) '4)");

        test("(not (pair? '#f))");

        test("(eq? '() (cddr '(1 2)))");

        test("(equal? (memp odd? '(2 4 6 7)) '(7))");
        test("(equal? '(7 9) (memq '7 '(1 2 7 9)))");
        testNot("(memq '7 '())");

//        runAndPrint("(remp odd? '(1 2 3 4 5 6 7 8))");
        test("(equal? '(6 . 66) (assoc '6 '((1 . 11) (6 . 66) (7 . 77))))");
        test("(equal? '(6 66 666) (assoc '6 '((1 . 11) (6 66 666) (7 . 77))))");

        run("(define lb ($make-list-builder))");
//        runAndPrint("lb");
        runAndPrint("(lb)");
        run("(lb '1)");
        run("(lb '())");
        run("(lb '#(#f #t))");
        run("(lb '(1 2 3 '()))");
        runAndPrint("(lb)");

        test("(equal? '() (reverse '()))");
        test("(equal? '(3 2 1) (reverse '(1 2 3)))");


//        runAndPrint("(null? '())");

    }

    private static Value run(String str) {
        return context.eval(Source.create(TSM, str));
    }

    private static void runAndPrint(String str) {
        System.out.println("\033[1;33m" + run(str) + "\033[0m");
    }

    private static void test(String str) {
        var res = run(str);
        if (res.toString().equals("#f")) {
            throw new RuntimeException(String.format("Test failed:\n\t%s\n\t-> %s", str, res));
        }
    }

    private static void testNot(String str) {
        var res = run(str);
        if (!res.toString().equals("#f")) {
            throw new RuntimeException(String.format("Test failed:\n\t%s\n\t-> %s", str, res));
        }
    }
}
