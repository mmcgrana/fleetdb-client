(ns fleetdb.helper-test
  (:use [fleetdb helper]
        [clj-unit core]))

(deftest "ping"
  (assert= [:ping] (ping)))

(deftest "select"
  (assert= [:select :people]
           (select :people))
  (assert= [:select :people {:where [:= :name "Bob"]}]
           (select :people (where (= :name "Bob"))))
  (assert= [:select :people {:limit 2 :only [:id :name]}]
           (select :people
                   (limit 2)
                   (only [:id :name])))
  (assert= [:select :people {:order [[:name "asc"] [:age "desc"]]}]
           (select :people
                   (order (asc :name) (desc :age))))
  (assert= [:select :people {:order [:name "asc"] :limit 1 :only :name}]
           (select :people
                   (order (asc :name))
                   (limit 1)
                   (only :name))))

(deftest "possible where criteria"
  (assert= {:where [:= :name "Bob"]}
           (where (= :name "Bob")))
  (assert= {:where [:!= :name "Bob"]}
           (where (!= :name "Bob")))
  (assert= {:where [:< :age 30]}
           (where (< :age 30)))
  (assert= {:where [:<= :age 30]}
           (where (<= :age 30)))
  (assert= {:where [:> :age 30]}
           (where (> :age 30)))
  (assert= {:where [:>= :age 30]}
           (where (>= :age 30)))
  (assert= {:where [:in :name ["Bob" "Amy"]]}
           (where (in :name ["Bob" "Amy"])))
  (assert= {:where [:>< :age [30 40]]}
           (where (>< :age [30 40])))
  (assert= {:where [:>=< :age [30 40]]}
           (where (>=< :age [30 40])))
  (assert= {:where [:><= :age [30 40]]}
           (where (><= :age [30 40])))
  (assert= {:where [:>=<= :age [30 40]]}
           (where (>=<= :age [30 40])))
  (assert= {:where [:and [:in :name ["Bob" "Amy"]] [:>=< :age [30 40]]]}
           (where (and (in :name ["Bob" "Amy"])
                       (>=< :age [30 40]))))
  (assert= {:where [:or [:in :name ["Bob" "Amy"]] [:>=< :age [30 40]]]}
           (where (or (in :name ["Bob" "Amy"])
                       (>=< :age [30 40])))))

(deftest "count"
  (assert= [:count :people]
           (dbcount :people))
  (assert= [:count :people {:where [:> :age 30]}]
           (dbcount :people
                          (where (> :age 30)))))

(deftest "insert"
  (assert= [:insert :people {:id 1 :name "Bob"}]
           (insert :people {:id 1 :name "Bob"}))
  (assert= [:insert :people [{:id 1 :name "Bob"}
                             {:id 2 :name "Amy"}]]
           (insert :people {:id 1 :name "Bob"}
                           {:id 2 :name "Amy"})))

(deftest "update"
  (assert= [:update :people {:vip true} {:where [:= :name "Bob"]}]
           (update :people {:vip true} (where (= :name "Bob")))))

(deftest "delete"
  (assert= [:delete :people]
           (delete :people))
  (assert= [:delete :people {:where [:= :name "Bob"]}]
           (delete :people (where (= :name "Bob")))))

(deftest "create-index"
  (assert= [:create-index :people :name]
           (create-index :people :name))
  (assert= [:create-index :people [:name [:age "desc"]]]
           (create-index :people [:name (desc :age)])))

(deftest "drop-index"
  (assert= [:drop-index :people :name]
           (drop-index :people :name))
  (assert= [:drop-index :people [:name [:age "desc"]]]
           (drop-index :people [:name (desc :age)])))

(deftest "multi-read"
  (assert= [:multi-read
              [[:count :people {:where [:= :age 30]}]
               [:count :people {:where [:= :name "Bob"]}]]]
           (multi-read
             (dbcount :people (where (= :age 30)))
             (dbcount :people (where (= :name "Bob"))))))

(deftest "multi-write"
  (assert= [:multi-write
              [[:select :people {:where [:= :counted false]}]
               [:update :people {:counted true} {:where [:= :counted false]}]]]
           (multi-write
             (select :people (where (= :counted false)))
             (update :people {:counted true} (where (= :counted false))))))

(deftest "checked-write"
  (assert= [:checked-write
              [:count :registrations {:where [:= :person-id 2]}]
              6
              [:insert :registrations {:id 13 :person-id 2 :event-id 4}]]
           (checked-write
             (dbcount :registrations (where (= :person-id 2)))
             6
             (insert :registrations {:id 13 :person-id 2 :event-id 4}))))

(deftest "explain"
  (assert= [:explain [:select :people]]
           (explain (select :people)))
  (assert= [:explain [:select :people {:where [:= :name "Bob"]
                                       :order [:age "asc"]}]]
           (explain (select :people
                            (where (= :name "Bob"))
                            (order (asc :age))))))

(deftest "list-collections"
  (assert= [:list-collections]
           (list-collections)))

(deftest "list-indexes"
  (assert= [:list-indexes :people]
           (list-indexes :people)))

(deftest "compact"
  (assert= [:compact] (compact)))
