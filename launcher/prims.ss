(define undefined-var (lambda (x) (error 'parser '"attempt to reference undefined variable" x)))

(define caar   (lambda (x) (car (car x))))
(define cddr   (lambda (x) (cdr (cdr x))))
(define caaar  (lambda (x) (car car (car x))))
(define cadr   (lambda (x) (car (cdr x))))
(define caddr  (lambda (x) (car (cdr (cdr x)))))
(define cadddr (lambda (x) (car (cdr (cdr (cdr x))))))

(define add1 (lambda (x) (+ x '1)))
(define sub1 (lambda (x) (- x '1)))
(define zero? (lambda (x) (fx= x '0)))

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
