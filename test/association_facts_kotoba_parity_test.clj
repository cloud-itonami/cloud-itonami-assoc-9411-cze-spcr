(ns association-facts-kotoba-parity-test
  "The SPCR catalog in .cljc and in .kotoba, field by field.

  Both are readings of the same `data/datascript-tx.edn`, so this is not two
  implementations of a rule; it is one body of facts written twice, and the
  risk is transcription -- a wrong URL, a dropped field, a topic that lost its
  entry. Every field of every entry is compared, plus the counts and topic
  membership, because a catalog is exactly the shape where checking a sample
  checks the entries someone already looked at.

  `:association-rule/topic` is a SET. A set has no order and `topic` is indexed
  by position, so the port chose the order the data file writes; the assertion
  below compares against that written order rather than against `seq` on a set,
  which is not stable to rely on."
  (:require [clojure.edn :as edn]
            [clojure.test :refer [deftest is testing]]
            [association.facts :as facts]
            [kotoba.compiler.core :as compiler]
            [kotoba.kir :as ir]))

(def ^:private source (slurp "src/association_facts.kotoba"))
(def ^:private kir (:kir (compiler/compile-source source :js-kotoba-v1)))
(defn- call [f & args] (ir/execute kir f (vec args)))
(defn- present [option] (when (second option) (nth option 2)))

(def ^:private slug "spcr")
(def ^:private fields
  ;; `media-type` and `last-revised-date` were absent from this list until
  ;; 2026-09-09, so the port could omit either for an entry that has one and
  ;; nothing would say so. Measured on 2026-09-09, both directions: dropping
  ;; `media-type` from the generator and regenerating fails four assertions
  ;; here ("text/html" vs nil) under this list, and passes -- 18 tests, 412
  ;; assertions, 0 failures -- under the old one, with the same broken port.
  ["id" "title" "association" "isic" "country" "kind" "url" "url-provenance"
   "media-type" "established-date" "last-revised-date" "retrieved-at"])
(def ^:private kw->field
  {"id" :association-rule/id "title" :association-rule/title
   "association" :association-rule/association "isic" :association-rule/isic
   "country" :association-rule/country "kind" :association-rule/kind
   "url" :association-rule/url "url-provenance" :association-rule/url-provenance
   "media-type" :association-rule/media-type
   "established-date" :association-rule/established-date
   "last-revised-date" :association-rule/last-revised-date
   "retrieved-at" :association-rule/retrieved-at})
(def ^:private entries (vec (facts/spec-basis slug)))
(def ^:private topic-order
  ;; The written order, read from the file the port is generated from.
  ;;
  ;; It was a hand-kept literal until 2026-09-09. Measured before changing it:
  ;; a SHORT hand-kept list does not silently pass -- the count assertion in
  ;; `the-fixture-reads-a-real-catalog` catches it (1 failure), and a list of
  ;; the right length with wrong contents is caught by the set comparison
  ;; below. So this is not a hole being closed; it is a maintenance step being
  ;; removed. The list had to be edited in step with the catalog, and the
  ;; failure it produced when someone forgot named the fixture rather than the
  ;; catalog, which is the wrong place to start reading.
  (mapv #(mapv name (:association-rule/topic %))
        (edn/read-string (slurp "data/datascript-tx.edn"))))

(deftest the-fixture-reads-a-real-catalog
  ;; An empty catalog compares equal to an empty port.
  (is (pos? (count entries)))
  (is (= (count entries) (count topic-order)))
  ;; And every field this port claims to answer must be exercised by at least
  ;; one entry, or the comparison for that field is vacuous.
  (doseq [f fields]
    (testing f
      (is (some #(some? (get % (kw->field f))) entries)
          "no entry carries this field, so comparing it proves nothing"))))

(deftest every-field-of-every-entry-is-transcribed
  (is (= (count entries) (call 'entry-count slug)))
  (doseq [[i entry] (map-indexed vector entries)]
    (doseq [f fields]
      (testing (str "entry " i " / " f)
        (let [expected (get entry (kw->field f))
              expected (cond (keyword? expected) (name expected)
                             (nil? expected) nil
                             :else expected)]
          (is (= expected (present (call 'entry-field slug i f)))))))))

(deftest topics-are-complete-and-in-the-order-the-port-chose
  (doseq [[i names] (map-indexed vector topic-order)]
    (testing (str "entry " i)
      (is (= (count names) (call 'topic-count slug i))
          "one number for every entry is the mistake this invites")
      (is (= (set names)
             (set (map name (:association-rule/topic (nth entries i)))))
          "the written order must name exactly the set the cljc holds")
      (doseq [[t nm] (map-indexed vector names)]
        (is (= nm (present (call 'topic slug i t))))))))

(deftest by-topic-answers-the-same-entries
  (doseq [names topic-order t names]
    (testing t
      (let [cljc (mapv :association-rule/id (facts/by-topic slug (keyword t)))]
        (is (= (count cljc) (call 'by-topic-count slug t)))
        (is (= (first cljc) (present (call 'by-topic-id slug t 0)))))))
  (is (zero? (call 'by-topic-count slug "no-such-topic")))
  (is (nil? (present (call 'by-topic-id slug "no-such-topic" 0)))))

(deftest an-unknown-association-is-covered-by-nothing
  (doseq [other ["zzz" ""]]
    (is (false? (call 'association-covered? other)))
    (is (zero? (call 'entry-count other)))
    (is (nil? (present (call 'entry-field other 0 "id"))))
    (is (nil? (present (call 'coverage-note other))))
    (is (nil? (facts/spec-basis other)) "and the cljc agrees")))

(deftest the-module-compiles-for-every-target-it-claims
  (doseq [target [:js-kotoba-v1 :wasm32-kotoba-v1 :x86_64-kotoba-v1 :aarch64-kotoba-v1]]
    (testing (name target)
      (is (some? (compiler/compile-source source target {}))))))
