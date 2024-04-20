(define undefined-var (lambda (x) (error 'parser '"attempt to reference undefined variable" x)))
(define todo (lambda (who) (error who '"Not implemented yet")))

(define caar   (lambda (x) (car (car x))))
(define cddr   (lambda (x) (cdr (cdr x))))
(define caaar  (lambda (x) (car car (car x))))
(define cadr   (lambda (x) (car (cdr x))))
(define caddr  (lambda (x) (car (cdr (cdr x)))))
(define cadddr (lambda (x) (car (cdr (cdr (cdr x))))))

(define add1 (lambda (x) (+ x '1)))
(define sub1 (lambda (x) (- x '1)))
(define zero? (lambda (x) (fx= x '0)))
(define integer?
  (lambda (x)
    (if (fixnum? x)
        '#t
        (if (bignum? x)
            '#t
            '#f))))
(define odd?
  (lambda (x)
    (if (integer? x)
        (= '1 (mod x '2))
        (error 'odd? '"Not an integer" x))))
(define even?
  (lambda (x)
    (if (integer? x)
        (= '0 (mod x '2))
        (error 'even? '"Not an integer" x))))

(define $make-list-builder
  (lambda ()
    (letrec ([res '()]
             [current '()])
      (lambda args
        (if (fx= '0 (length args))
            res
            (if (fx= '1 (length args))
                (if (null? res)
                    (begin (set! res (cons (car args) '()))
                           (set! current res))
                    (begin (set-cdr! current (cons (car args) '()))
                           (set! current (cdr current))))
                (error '$make-list-builder '"Invalid number of arguments" args)))))))

(define $length-check
  (lambda (who first rest)
    (letrec ([n (length first)]
             [loop (lambda (rest)
                     (if (null? rest)
                         n
                         (if (fx= (length (car rest)) n)
                             (loop (cdr rest))
                             (error who '"Lengths of lists are not the same" first rest))))])
      (loop rest))))

(define $map1
  (lambda (proc ls)
    (letrec ([loop (lambda (ls)
                     (if (null? ls)
                         '()
                         ((lambda (tail)
                            (cons (proc (car ls)) tail))
                          (loop (cdr ls)))))])
      (loop ls))))

(define map
  (lambda (proc ls . ls*)
    (if (procedure? proc)
        (begin ($length-check 'map ls ls*)
               (if (null? ls)
                   '()
                   (if (null? ls*)
                       ($map1 proc ls)
                       (letrec ([loop
                                 (lambda (ls ls*)
                                   (if (null? ls)
                                       '()
                                       ((lambda (tail)
                                          (cons (apply proc (car ls) ($map1 car ls*)) tail))
                                        (loop (cdr ls) ($map1 cdr ls*)))))])
                         (loop ls ls*)))))
        (error 'map '"Not a procedure" proc))))

(define for-each
  (lambda (proc ls . ls*)
    (if (procedure? proc)
        (begin ($length-check 'for-each ls ls*)
               (if (null? ls)
                   (void)
                   (letrec ([loop
                             (lambda (ls ls*)
                               (if (null? ls)
                                   '()
                                   (begin (apply proc (car ls) ($map1 car ls*))
                                          (loop (cdr ls) ($map1 cdr ls*)))))])
                     (loop ls ls*))))
        (error 'for-each '"Not a procedure" proc))))

(define for-all
  (lambda (pred ls . ls*)
    (if (procedure? pred)
        (begin ($length-check 'for-all ls ls*)
               (if (null? ls)
                   '#t
                   (letrec ([loop
                             (lambda (ls ls*)
                               (if (null? ls)
                                   '#t
                                   (if (apply pred (car ls) ($map1 car ls*))
                                       (loop (cdr ls) ($map1 cdr ls*))
                                       '#f)))])
                     (loop ls ls*))))
        (error 'for-all '"Not a procedure" pred))))

(define exists
  (lambda (pred ls . ls*)
    (if (procedure? pred)
        (begin ($length-check 'exists ls ls*)
               (if (null? ls)
                   '#f
                   (letrec ([loop
                             (lambda (ls ls*)
                               (if (null? ls)
                                   '#f
                                   (if (apply pred (car ls) ($map1 car ls*))
                                       '#t
                                       (loop (cdr ls) ($map1 cdr ls*)))))])
                     (loop ls ls*))))
        (error 'exists '"Not a procedure" pred))))

(define reverse
  (lambda (e*)
    (if (null? e*)
        '()
        (if (pair? e*)
            (letrec ([loop
                      (lambda (res ls)
                        (if (null? ls)
                            res
                            (if (pair? ls)
                                (loop (cons (car ls) res) (cdr ls))
                                (error 'reverse '"Not a list" ls))))])
              (loop '() e*))
            (error 'reverse '"Not a list" e*)))))

(define $append1!
  (lambda (who ls1 ls2)
    (if (list? ls1)
        (letrec ([loop (lambda (ls1)
                         (if (null? (cdr ls1))
                             (set-cdr! ls1 ls2)
                             (loop (cdr ls1))))])
          (loop ls1)
          ls1)
        (error who '"Not a proper list" ls1))))

(define list-copy
  (lambda (ls)
    (if (null? ls)
        ls
        (if (list? ls)
            ((lambda (lb)
               (letrec ([loop (lambda (ls)
                                (if (null? ls)
                                    (lb)
                                    (begin (lb (car ls))
                                           (loop (cdr ls)))))])
                 (loop ls)))
             ($make-list-builder))
            (error 'list-copy '"Not a proper list" ls)))))

(define list-copy-improper
  (lambda (ls)
    (if (null? ls)
        ls
        (if (pair? ls)
            (letrec ([loop (lambda (ls)
                             (if (null? ls)
                                 ls
                                 (if (pair? (cdr ls))
                                     (cons (car ls) (loop (cdr ls)))
                                     (cons (car ls) (cdr ls)))))])
              (loop ls))
            (error 'list-copy-improper '"Not a list" ls)))))

(define append
  (lambda args
    (if (fx= '0 (length args))
        '()
        ((lambda (ls obj)
           (if (null? ls)
               (if (null? obj)
                   '()
                   (if (pair? (car obj))
                       (if (null? (cdr obj))
                           (list-copy-improper (car obj))
                           (apply append obj))
                       (if (null? (cdr obj))
                           (car obj)
                           (error 'append '"Not a list" (car obj)))))
               (if (pair? ls)
                   (if (null? obj)
                       (list-copy-improper ls)
                       (if (null? (car obj))
                           (apply append ls (cdr obj))
                           (if (pair? (car obj))
                               (cons (car ls) (apply append (cdr ls) obj))
                               (if (null? (cdr obj))
                                   ($append1! 'append (list-copy ls) (car obj))
                                   (error 'append '"Not a list" (car obj))))))
                   (error 'append '"Not a list" ls))))
         (car args) (cdr args)))))

(define append!
  (lambda (ls . obj)
    (error 'append! '"Not impl")))

(define $assp
  (lambda (who pred ls)
    (letrec ([loop
              (lambda (alist)
                (if (null? alist)
                    '#f
                    (if (pair? alist)
                        (if (pred (car (car alist)))
                            (car alist)
                            (loop (cdr alist)))
                        (error who '"Not a proper list" ls))))])
      (loop ls))))

(define $remp
  (lambda (who pred ls)
    (letrec ([loop
              (lambda (res e*)
                (if (null? e*)
                    (reverse res)
                    (if (pair? e*)
                        (if (pred? (car e*))
                            (loop res (cdr e*))
                            (loop (cons (car e*) res) (cdr e*)))
                        (error who '"Not a proper list" ls))))])
      (loop '() ls))))

(define $memp
  (lambda (who pred ls)
    (letrec ([loop
              (lambda (e*)
                (if (null? e*)
                    '#f
                    (if (pair? e*)
                        (if (pred (car e*))
                            e*
                            (loop (cdr e*)))
                        (error who '"Not a proper list" ls e*))))])
      (loop ls))))

(define $gen-eq?    (lambda (x) (lambda (y) (eq?  x y))))
(define $gen-eqv?   (lambda (x) (lambda (y) (eqv? x y))))
(define $gen-equal? (lambda (x) (lambda (y) (equal? x y))))

(define memp   (lambda (pred ls) ($memp 'memp pred ls)))
(define memq   (lambda (x ls) ($memp 'memq ($gen-eq? x) ls)))
(define memv   (lambda (x ls) ($memp 'memv ($gen-eqv? x) ls)))
(define member (lambda (x ls) ($memp 'member ($gen-equal? x) ls)))

(define remp   (lambda (pred ls) ($remp 'remp pred ls)))
(define remq   (lambda (x ls) ($remp 'remq ($gen-eq? x) ls)))
(define remv   (lambda (x ls) ($remp 'remv ($gen-eqv? x) ls)))
(define remove (lambda (x ls) ($remp 'remove ($gen-equal? x) ls)))

(define assp  (lambda (pred ls) ($assp 'assp pred ls)))
(define assq  (lambda (x ls) ($assp 'assq ($gen-eq? x) ls)))
(define assv  (lambda (x ls) ($assp 'assv ($gen-eqv? x) ls)))
(define assoc (lambda (x ls) ($assp 'assoc ($gen-equal? x) ls)))

(define fold-left
  (lambda ()
    (todo 'fold-left)))

(define fold-right
  (lambda ()
    (todo 'fold-right)))

(define with-input-from-file
  (lambda (file th)
    ((lambda (old-in)
       ((lambda (in)
          (begin (current-input-port in)
                 ((lambda (res)
                    (close-port in)
                    (current-input-port old-in)
                    res)
                  (th))))
        (open-input-file file)))
     (current-input-port))))

(define with-output-to-file
  (lambda (file th)
    ((lambda (old-out)
       ((lambda (out)
          (begin (current-output-port out)
                 ((lambda (res)
                    (close-port out)
                    (current-output-port old-out)
                    res)
                  (th))))
        (open-output-file file)))
     (current-output-port))))

(define open-string-output-port
  (lambda ()
    ((lambda (x)
       (values x
               (lambda ()
                 ($string-output-port-extract x))))
     ($open-string-output-port))))


(define dynamic-wind
  (lambda (pre th post)
    (pre)
    ((lambda (res)
       (post)
       res)
     (th))))

(define sorted?
  (lambda (pred e*)
    (letrec ([loop (lambda (e*)
                     (if (null? (cdr e*))
                         '#t
                         (if (pred (car e*) (cadr e*))
                             (loop (cdr e*))
                             '#f)))])
      (loop e*))))

(define char=?
  (lambda (c . cs)
    (if (char? c)
        (if (null? cs)
            '#t
            (sorted? equal? (cons c cs)))
        (error 'char=? '"Not a character" c))))

(define char<?
  (lambda (c . cs)
    (if (char? c)
        (if (null? cs)
            '#t
            (sorted? $char<1? (cons c cs)))
        (error 'char<? '"Not a character" c))))

(define char<=?
  (lambda (c . cs)
    (if (char? c)
        (if (null? cs)
            '#t
            (sorted? $char<=1? (cons c cs)))
        (error 'char<=? '"Not a character" c))))

(define char>?
  (lambda (c . cs)
    (if (char? c)
        (if (null? cs)
            '#t
            (sorted? (lambda (x y) (not ($char<=1? x y))) (cons c cs)))
        (error 'char>? '"Not a character" c))))

(define char>=?
  (lambda (c . cs)
    (if (char? c)
        (if (null? cs)
            '#t
            (sorted? (lambda (x y) (not ($char<1? x y))) (cons c cs)))
        (error 'char>=? '"Not a character" c))))

(define string
  (lambda chars
    (for-each (lambda (c) (if (char? c)
                              (void)
                              (error 'string '"Not a character" c)))
              chars)
    ((lambda (str)
       (letrec ([loop (lambda (cs i)
                        (if (null? cs)
                            str
                            (begin (string-set! str i (car cs))
                                   (loop (cdr cs) (fx+ i '1)))))])
         (loop chars '0)))
     (make-string (length chars)))))


(define vector->list
  (lambda (vec)
    (if (vector? vec)
        ((lambda (n)
           (if (fx= '0 n)
               '()
               (letrec ([loop (lambda (res i)
                                (if (fx= '-1 i)
                                    res
                                    (loop (cons (vector-ref vec i)  res) (fx- i '1))))])
                 (loop '() (fx- n '1)))))
         (vector-length vec))
        (error 'vector->list '"Not a vector" vec))))

(define list->vector
  (lambda (ls)
    (if (list? ls)
        (if (null? ls)
            '#()
            ((lambda (vec)
               (letrec ([loop (lambda (ls i)
                                (if (null? ls)
                                    vec
                                    (begin (vector-set! vec i (car ls))
                                           (loop (cdr ls) (fx+ '1 i)))))])
                 (loop ls '0)))
             (make-vector (length ls))))
        (error 'list->vector '"Not a list" ls))))

(define string->list
  (lambda (str)
    (if (fx= '0 (string-length str))
        '()
        ((lambda (bd len)
           (letrec ([loop (lambda (n)
                            (if (fx= n len)
                                (bd)
                                (begin (bd (string-ref str n))
                                       (loop (fx+ n '1)))))])
             (loop '0)))
         ($make-list-builder) (string-length str)))))

(define cons*
  (lambda args
    ((lambda (len)
       (if (fx= '0 len)
           (error 'cons* '"Invalid number of arguments")
           (if (fx= '1 len)
               (car args)
               (letrec ([loop (lambda (rest)
                                (if (null? (cdr rest))
                                    (car rest)
                                    (cons (car rest) (loop (cdr rest)))))])
                 (loop args)))))
     (length args))))

(define gensym
  ((lambda (id)
     (lambda args
       (if (null? args)
           (gensym '"g")
           (if (fx= '1 (length args))
               ((lambda (x)
                  ((lambda (s)
                     (set! id (fx+ '1 id))
                     (string->symbol
                      (string-append '"$" s '"$gensym$" (number->string id))))
                   (if (symbol? x)
                       (symbol->string x)
                       (if (string? x)
                           x
                           (error 'gensym '"Expected a symbol or a string" x)))))
                (car args))
               (error 'gensym '"Invalid number of arguments")))))
   '0))

(define $arith-dispatch
  (lambda (who fix-op flo-op big-op)
    (lambda (x y)
      (if (fixnum? x)
          (if (fixnum? y)
              (fix-op x y)
              (if (flonum? y)
                  (flo-op (fixnum->flonum x) y)
                  (if (bignum? y)
                      (big-op ($fix->big x) y)
                      (error who '"Not a number" y))))
          (if (flonum? x)
              (if (fixnum? y)
                  (flo-op x (fixnum->flonum y))
                  (if (flonum? y)
                      (flo-op x y)
                      (if (bignum? y)
                          (todo '$arith-dispatch)
                          (error who '"Not a number" y))))
              (if (bignum? x)
                  (if (fixnum? y)
                      (big-op x ($fix->big y))
                      (if (flonum? y)
                          (todo '$arith-dispatch)
                          (if (bignum? y)
                              (big-op x y)
                              (error who '"Not a number" y))))
                  (error who '"Not a number" x)))))))

(define $fx>  (lambda (x y) (not (fx<= x y))))
(define $fx>= (lambda (x y) (not (fx< x y))))

(define $fl>=  (lambda (x y) (not ($fl< x y))))
(define $big>= (lambda (x y) (not ($big< x y))))

(define helper-+
  ($arith-dispatch '+ fx+ fl+ $big+))

(define helper--
  ($arith-dispatch '- fx- fl- $big-))

(define helper-*
  ($arith-dispatch '* fx* fl* $big*))

(define helper-/
  ($arith-dispatch '/ fx/ fl/ $big/))

(define helper-/
  ($arith-dispatch '/ fx/ fl/ $big/))

(define helper-<
  ($arith-dispatch '/ fx< $fl< $big>))

(define helper-<=
  ($arith-dispatch '/ fx<= $fl<= $big<=))

(define helper->
  ($arith-dispatch '/ $fx> $fl> $big>))

(define helper->=
  ($arith-dispatch '/ $fx>= $fl>= $big>=))

(define +
  (lambda args
    (if (null? args)
        '0
        (letrec ([loop (lambda (res rest)
                         (if (null? rest)
                             res
                             (loop (helper-+ res (car rest)) (cdr rest))))])
          (loop (car args) (cdr args))))))

(define -
  (lambda (n . args)
    (if (null? args)
        (if (fixnum? n)
            (fx- n)
            (if (flonum? n)
                (fl- n)
                (if (bignum? n)
                    ($big- n)
                    (error '- '"Not a number" n))))
        (letrec ([loop (lambda (res rest)
                         (if (null? rest)
                             res
                             (loop (helper-- res (car rest)) (cdr rest))))])
          (loop n args)))))

(define *
  (lambda args
    (if (null? args)
        '1
        (letrec ([loop (lambda (res rest)
                         (if (null? rest)
                             res
                             (loop (helper-* res (car rest)) (cdr rest))))])
          (loop (car args) (cdr args))))))

(define /
  (lambda (n . args)
    (if (null? args)
        (if (fixnum? n)
            (fx/ '1 n)
            (if (flonum? n)
                (fl/ '1.0 n)
                (if (bignum? n)
                    ($big/ ($fix->big '1) n)
                    (error '/ '"Not a number" n))))
        (letrec ([loop (lambda (res rest)
                         (if (null? rest)
                             res
                             (loop (helper-/ res (car rest)) (cdr rest))))])
          (loop n args)))))

(define <
  (lambda (n . args)
    (if (null? args)
        '#t
        (letrec ([loop (lambda (res n rest)
                         (if (null? rest)
                             res
                             (if res
                                 (loop (helper-< n (car rest)) (car rest) (cdr rest))
                                 '#f)))])
          (loop '#t n args)))))

(define <=
  (lambda (n . args)
    (if (null? args)
        '#t
        (letrec ([loop (lambda (res n rest)
                         (if (null? rest)
                             res
                             (if res
                                 (loop (helper-<= n (car rest)) (car rest) (cdr rest))
                                 '#f)))])
          (loop '#t n args)))))

(define >
  (lambda (n . args)
    (if (null? args)
        '#t
        (letrec ([loop (lambda (res n rest)
                         (if (null? rest)
                             res
                             (if res
                                 (loop (helper-> n (car rest)) (car rest) (cdr rest))
                                 '#f)))])
          (loop '#t n args)))))

(define >=
  (lambda (n . args)
    (if (null? args)
        '#t
        (letrec ([loop (lambda (res n rest)
                         (if (null? rest)
                             res
                             (if res
                                 (loop (helper->= n (car rest)) (car rest) (cdr rest))
                                 '#f)))])
          (loop '#t n args)))))

(define modulo fxmod)
(define remainder fxremainder)

(define quotient
  (lambda (n1 n2)
    (fx/ n1 n2)))
