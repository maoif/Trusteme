(define test-all
    (case-lambda
      [() (test-all #f)]
      [(noisy?)
       (for-all
         (lambda (t)
           (when noisy? (pretty-print t))
           (let ([expected (eval t)]
                 [actual (guard (e [else (printf "test-exception evaluating:~%") (pretty-print t) (raise e)])
                           (tiny-compile t))])
             (unless (or (equal? expected actual) (equal? actual '#(exception)))
               (printf "test-failed: expected ~s, but got ~s but got:~%" expected actual)
               (pretty-print t)
               (errorf 'test-all "testing failed"))))
         tests)]))
(define last-test
    (make-parameter 0
      (lambda (x)
        (unless (and (integer? x) (exact? x)) (errorf 'last-test "expected exact integer, but got ~s" x))
        x)))
