(ns one.test.dispatch
  (:use clojure.test
        one.test))

(use-fixtures :once within-rhino-env)

(deftest hello
  (is (= 2 (+ 1 1))))

#_(do
  (let [recorded-reactions (atom [])
        reaction (react-to #{:do-something} #(swap! recorded-reactions conj [%1 %2]))]
    ;; Did we get a reaction back?
    (assert reaction)
    (fire :do-something)
    ;; Did the reactor catch the event?
    (assert (= [[:do-something nil]] @recorded-reactions))
    (fire :do-something)
    ;; Did the reactor catch the event a second time?
    (assert (= [[:do-something nil] [:do-something nil]] @recorded-reactions))
    (fire :something-else)
    ;; Did we ignore events we're not reacting to?
    (assert (= [[:do-something nil] [:do-something nil]] @recorded-reactions))
    (reset! recorded-reactions [])
    (fire :do-something 17)
    ;; Does event data arrive intact?
    (assert (= [[:do-something 17]] @recorded-reactions))
    (reset! recorded-reactions [])
    (delete-reaction reaction)
    (fire :do-something 17)
    ;; Does deleting a reaction cause us to stop reacting?
    (assert (= [] @recorded-reactions)))
  (let [recorded-reactions (atom #{})
        reaction-once (react-to 1 #{:do-something}
                                #(swap! recorded-reactions conj [1 %1 %2]))
        reaction-twice (react-to 2 #{:do-something}
                                 #(swap! recorded-reactions conj [2 %1 %2]))]
    (fire :do-something 1)
    ;; Did both reactions react?
    (assert (= #{[1 :do-something 1] [2 :do-something 1]} @recorded-reactions))
    (fire :do-something 2)
    ;; Did only the second reaction react?
    (assert (= #{[1 :do-something 1] [2 :do-something 1] [2 :do-something 2]}
               @recorded-reactions))
    (fire :do-something 3)
    ;; Did nothing change?
    (assert (= #{[1 :do-something 1] [2 :do-something 1] [2 :do-something 2]}
               @recorded-reactions)))
  true)