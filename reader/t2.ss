(define (analyze-all)
    (let ([c 1])
      (for-all
        (lambda (t)
          (let ([expected (eval t)]
                [actual (guard (e [else (printf "E") (flush-output-port) '#(exception)]) (tiny-compile t))])
            (cond
              [(equal? expected actual) (printf ".") (flush-output-port)]
              [(equal? actual '#(exception)) (void)]
              [else (printf "F") (flush-output-port)])
            (when (= c 50) (newline) (set! c 0))
            (set! c (+ c 1))))
        tests)))
