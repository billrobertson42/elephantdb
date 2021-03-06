(ns elephantdb.config-test
  (:use clojure.test
        [elephantdb config testing])
  (:import [elephantdb.persistence JavaBerkDB]
           [elephantdb DomainSpec]))

(defn- norm-spec [s]
  (assoc s :persistence-factory
         (-> (:persistence-factory s)
             (.getClass)
             (.getName))))

(defn- specs= [s1 s2]
  (= (norm-spec s1)
     (norm-spec s2)))

(def-fs-test test-rw-domain-spec [fs tmp]
  (let [spec {:num-shards 20 :persistence-factory (JavaBerkDB.)}]
    (write-domain-spec! spec fs tmp)
    (is (specs= spec (read-domain-spec fs tmp))))
  (let [jspec (DomainSpec/readFromFileSystem fs tmp)]
    (is (= 20 (.getNumShards jspec)))
    (is (= "elephantdb.persistence.JavaBerkDB" (-> (.getLPFactory jspec)
                                                   (.getClass)
                                                   (.getName))))))

(def-fs-test test-rw-clj-configs [fs tmp1 tmp2]
  (let [config1 {:blah 2 :a "eee" :c [1 2 "a"]}
        config2 {:foo {:lalala {:a 1 "c" 3}}}]
    (write-clj-config! config1 fs tmp1)
    (write-clj-config! config2 fs tmp2)
    (is (= config1 (read-clj-config fs tmp1)))
    (is (= config2 (read-clj-config fs tmp2)))))
