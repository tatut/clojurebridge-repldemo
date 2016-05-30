(ns repldemo
  (:require [clojure.repl :refer :all]
            [clojure.string :as str]))

;; This is a REPL demo, demonstrating simple functionality
;;
;; Evaluate each form with:
;;  Cmd/Ctrl-Enter

;; Define a simple function
(defn hello
  "This is the documentation for the function. 
  Return a friendly greeting for the given target."
  [to]
  (str "Hello, " to "!"))

;; Call the function
(hello "world")

;; Check the documentation
(doc hello)

(defn bye
  "Say goodbye"
  [to]
  (str "Goodbye " to ", have a nice day."))

(bye "world")

;; Functions can be given as parameters

(map hello ["world" "Oulu" "ClojureBridge students"])


;; Functions can take multiple parameters 

(defn say [speaker saying]
  (str speaker ": " saying))

(say "Ceasar" "Et tu, Brute?")

;; Functions compose to create larger functionality

(defn conversation [a b sayings]
  (println (say a (hello b)))
  (println  (say b (hello a)))
  (dorun
   (map (fn [speaker saying]
          (println (say speaker saying)))
        (cycle [a b])
        sayings))
  (println (say a (bye b)))
  (println (say b (bye a))))

(conversation "Bob" "Alice"
              ["How do you do?"
               "I'm fine thank you, and you?"
               "Fine, fine... what lovely weather we have?"
               "Indeed."])
;; prints:
;; Bob: Hello, Alice!
;; Alice: Hello, Bob!
;; Bob: How do you do?
;; Alice: I'm fine thank you, and you?
;; Bob: Fine, fine... what lovely weather we have?
;; Alice: Indeed.
;; Bob: Goodbye Alice, have a nice day
;; Alice: Goodbye Bob, have a nice day


;;;;;;;;;;;; sales tax ;;;;;;;;;;;

;; Data is often stored in maps (collection of keys to values).
;; for example: {:name "Bill" :age 35} is a map with two keys

;; Let's define a vector of sales data where each sale is a map
;; of the item purchased, its price in euros and the VAT percent (value added tax).
;; There are three 

(def sales
  [{:name "Reinon Ruistanko" :price 3.67 :tax 24} ; some bread
   {:name "Hiipivä Haamu" :price 35.99 :tax 10} ; a mystery novel
   {:name "Log from Blammo" :price 19.99 :tax 24} ; a popular children's toy
   {:name "Fine leather jacket" :price 300.0 :tax 24}
   ])

;; check the count, we should have 4 items purchased
(count sales)

(defn sales-tax [item]
  (* (:price item)
     (/ (:tax item) 100)))

(defn receipt
  "Given a list of purchased items, generate a receipt"
  [purchases]
  (str "Item                  Price 0%  Tax%   Price  \n"
       "--------------------+---------+-----+---------\n"
       (str/join "\n"
                 (map (fn [item]
                        (let [tax (sales-tax item)]
                          (format "%-20s|%7.2f€ | %2d%% |%7.2f€"
                                  (:name item)
                                  (- (:price item) tax)
                                  (:tax item)
                                  (:price item))))
                      purchases))

       (let [total-price (reduce + (map :price sales))
             total-tax (reduce + (map sales-tax sales))]
         (format "\n             TOTAL: |%7.2f€ | --- |%7.2f€\n\n TOTAL TAX: %.2f€"
                 (- total-price total-tax)
                 total-price
                 total-tax))))
    
;; repldemo> (print (receipt sales))
;; Item                  Price 0%  Tax%   Price  
;; --------------------+---------+-----+---------
;; Reinon Ruistanko    |   2,79€ | 24% |   3,67€
;; Hiipivä Haamu       |  32,39€ | 10% |  35,99€
;; Log from Blammo     |  15,19€ | 24% |  19,99€
;; Fine leather jacket | 228,00€ | 24% | 300,00€
;;             TOTAL: | 278,37€ | --- | 359,65€
;;
;; TOTAL TAX: 81,28€

