(ns association.facts-test
  "The catalog as a body of citations, not as a count.

  Twelve entries were added on 2026-09-09, which is the size at which a catalog
  starts to acquire a fabricated URL, a copied-down date, or a second entry for
  a document already in the table. The assertions below are the ones that would
  have caught each of those, so they are written over the whole catalog rather
  than over the entry someone happened to look at.

  Two things here are deliberately weaker than in the sibling repos, because the
  stronger form would be false:

  - URLs are NOT distinct. Articles 1(2), 2(2)(a), 9 and 19 of the Statutes are
    four rules in one PDF. What must be distinct is the id, and what must not
    happen is the same rule appended twice -- so (url, title) is the pair that
    is asserted unique, with a floor on how many distinct documents the catalog
    actually reaches.
  - `:established-date` is absent from one entry on purpose. spcr.cz gives the
    predecessor's founding year and no day, and a completed date would be
    indistinguishable from a fact.

  What none of this can check is whether the URL still resolves -- the suite is
  hermetic on purpose. `nbb scripts/verify_citations.cljs` is the check that
  goes and looks."
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [clojure.test :refer [deftest is testing]]
            [association.facts :as facts]))

(def ^:private slug "spcr")
(def ^:private entries (vec (facts/spec-basis slug)))

(def ^:private tx-data
  "The file the Kotoba port is generated from. Reading it here is what makes
   the two files one catalog instead of two."
  (edn/read-string (slurp "data/datascript-tx.edn")))

(deftest spcr-has-spec-basis
  (is (seq entries))
  (is (every? #(= "9411" (:association-rule/isic %)) entries))
  (is (every? #(= "CZE" (:association-rule/country %)) entries))
  (is (every? #(= slug (:association-rule/association %)) entries)))

(deftest provenance-names-the-host-the-url-is-actually-on
  ;; The whole difference between the two kinds of entry here is where the fact
  ;; came from. A provenance keyword that does not match the host is the way
  ;; that difference would quietly stop being true -- and it is exactly what
  ;; would happen if someone "upgraded" a Wikipedia entry by editing the
  ;; keyword instead of finding the primary document.
  (doseq [e entries]
    (testing (:association-rule/id e)
      (let [url (:association-rule/url e)]
        (case (:association-rule/url-provenance e)
          :official-association-site
          (is (re-find #"^https://www\.spcr\.cz/" url)
              "an entry claiming the association's own site must cite it")
          :wikipedia-corroborated
          (is (re-find #"^https://cs\.wikipedia\.org/" url)
              "an entry claiming Wikipedia corroboration must cite Wikipedia")
          (is false (str "unknown provenance: "
                         (:association-rule/url-provenance e)))))))
  ;; The floor: if every entry lost its provenance the case above would still
  ;; pass on an empty catalog, and a catalog of nothing but Wikipedia would be
  ;; a regression to what this repo looked like before 2026-09-09.
  (is (<= 12 (count (facts/primary-sourced slug)))
      "the primary-sourced entries are the point of the 2026-09-09 pass")
  (is (= 2 (count (remove #(= :official-association-site
                              (:association-rule/url-provenance %))
                          entries)))))

(deftest ids-are-distinct-and-no-rule-is-appended-twice
  (let [ids (map :association-rule/id entries)]
    (is (= (count entries) (count (distinct ids))) (pr-str (frequencies ids)))
    (is (every? #(str/starts-with? % (str slug ".")) ids)))
  ;; URLs repeat by design; the same URL AND the same title is a duplicate.
  (let [pairs (map (juxt :association-rule/url :association-rule/title) entries)]
    (is (= (count entries) (count (distinct pairs)))
        (pr-str (->> pairs frequencies (filter (fn [[_ n]] (< 1 n))) (into {})))))
  ;; And the catalog must actually reach several documents, not one cited many
  ;; times, which the assertion above would happily allow.
  (is (<= 10 (count (facts/citations slug)))))

(deftest kinds-provenance-and-media-types-come-from-a-closed-vocabulary
  ;; A typo'd keyword is not an error anywhere else -- it just silently answers
  ;; no query.
  (is (every? #{:self-regulatory-code :governance-program :practice-guidance}
              (map :association-rule/kind entries)))
  (is (every? #{:official-association-site :wikipedia-corroborated}
              (map :association-rule/url-provenance entries)))
  ;; The media type is an assertion about the live response that
  ;; verify_citations.cljs compares against. A value outside this set makes that
  ;; comparison fail for a reason that has nothing to do with link rot.
  (is (every? #{"text/html" "application/pdf"}
              (map :association-rule/media-type entries)))
  ;; And it must not be inferable-by-extension, or the field would be pointless
  ;; here: this catalog's whole reason for carrying it is spcr.cz serving PDFs
  ;; from paths that do not end in .pdf.
  (is (seq (filter #(and (= "application/pdf" (:association-rule/media-type %))
                         (not (str/ends-with? (:association-rule/url %) ".pdf")))
                   entries))))

(deftest every-entry-carries-at-least-one-topic
  ;; A topicless entry is reachable by id and by nothing else, which is the
  ;; quiet way an entry stops being part of the catalog.
  (doseq [e entries]
    (testing (:association-rule/id e)
      (is (seq (:association-rule/topic e)))
      (is (every? keyword? (:association-rule/topic e))))))

(deftest dates-are-iso-and-ordered-when-present
  (doseq [e entries]
    (testing (:association-rule/id e)
      (let [est (:association-rule/established-date e)
            rev (:association-rule/last-revised-date e)]
        (when est (is (re-matches #"\d{4}-\d{2}-\d{2}" est)))
        (when rev (is (re-matches #"\d{4}-\d{2}-\d{2}" rev)))
        (when (and est rev)
          (is (not (neg? (compare rev est)))
              "a document cannot have been amended before it was enacted"))
        (is (re-matches #"\d{4}-\d{2}-\d{2}" (:association-rule/retrieved-at e))))))
  ;; The floor: if every date went missing, the loop above would assert nothing
  ;; and still pass.
  (is (<= 11 (count (filter :association-rule/established-date entries))))
  (is (<= 2 (count (filter :association-rule/last-revised-date entries))))
  ;; And the honest gap is kept honest: the predecessor entry has a year only,
  ;; so it must carry no established date at all.
  (is (nil? (:association-rule/established-date
             (first (filter #(= "spcr.predecessor-first-union-of-industrialists"
                                (:association-rule/id %))
                            entries))))
      "spcr.cz gives 1918 as a year; a day would be invented"))

(deftest the-cljc-and-the-data-file-are-one-catalog
  ;; src/association_facts.kotoba is generated from data/datascript-tx.edn while
  ;; this map is written out separately, so without this the two could disagree
  ;; and the .kotoba parity test would report the mismatch against the wrong
  ;; file.
  (is (= (count tx-data) (count entries)))
  (doseq [[i [from-data from-cljc]] (map-indexed vector (map vector tx-data entries))]
    (testing (str "entry " i)
      (is (= (dissoc from-data :association-rule/topic)
             (dissoc from-cljc :association-rule/topic)))
      (is (= (set (:association-rule/topic from-data))
             (:association-rule/topic from-cljc))
          "the data file writes topics as a vector, the map holds a set"))))

(deftest unknown-association-has-no-spec-basis
  (is (nil? (facts/spec-basis "wko")))
  (is (nil? (facts/spec-basis "zzz"))))

(deftest coverage-is-honest
  (let [c (facts/coverage [slug "wko"])]
    (is (= 2 (:requested c)))
    (is (= 1 (:covered c)))
    (is (= ["wko"] (:missing-associations c)))
    (is (str/includes? (:note c) (str (count entries) " spcr entries")))
    (is (str/includes? (:note c) (str (count (facts/primary-sourced slug))
                                      " of them cited to spcr.cz")))))

(deftest by-topic-filters
  ;; Named entries, not counts: a count passes when by-topic returns the wrong
  ;; entries in the right number.
  (is (= ["spcr.founding-1990"
          "spcr.renaming-1994-add-transport"
          "spcr.predecessor-first-union-of-industrialists"]
         (mapv :association-rule/id (facts/by-topic slug :history))))
  (is (= ["spcr.employers-organisation-legal-basis"]
         (mapv :association-rule/id (facts/by-topic slug :labour-law))))
  (is (= ["spcr.tripartite-social-dialogue"]
         (mapv :association-rule/id (facts/by-topic slug :social-dialogue))))
  (is (= ["spcr.ethics-code-2021"]
         (mapv :association-rule/id (facts/by-topic slug :ethics))))
  (is (= ["spcr.payment-rules-2026"]
         (mapv :association-rule/id (facts/by-topic slug :membership))))
  (is (= ["spcr.candidate-nomination-rules-2026"]
         (mapv :association-rule/id (facts/by-topic slug :elections))))
  (is (= ["spcr.trade-fair-committee-statutes"]
         (mapv :association-rule/id (facts/by-topic slug :trade-promotion))))
  (is (= ["spcr.eu-priorities-2024-2029"]
         (mapv :association-rule/id (facts/by-topic slug :eu-affairs))))
  (is (= ["spcr.programme-declaration-2026"
          "spcr.eu-priorities-2024-2029"
          "spcr.vision-cesko-2029"]
         (mapv :association-rule/id (facts/by-topic slug :economic-policy))))
  (is (= 12 (count (facts/by-topic slug :governance))))
  (is (empty? (facts/by-topic slug :consumer-protection)))
  (is (empty? (facts/by-topic "wko" :governance))))

(deftest by-kind-separates-the-binding-code-from-the-programmes
  ;; The Code of Ethics is the one document here that binds members' conduct
  ;; rather than describing what the confederation itself will do.
  (is (= ["spcr.ethics-code-2021"]
         (mapv :association-rule/id (facts/by-kind slug :self-regulatory-code))))
  (is (= 13 (count (facts/by-kind slug :governance-program))))
  (is (empty? (facts/by-kind slug :practice-guidance))))

(deftest describe-answers-one-line-per-entry
  ;; `describe` falls back through established -> last-revised -> "(undated)",
  ;; and the fallback is the part that would silently print "(undated)" for the
  ;; whole catalog if the field names were wrong.
  (let [lines (facts/describe slug)]
    (is (= (count entries) (count lines)))
    (is (= 1 (count (filter #(str/includes? % "(undated)") lines))))
    (is (some #(str/includes? % "2026-05-13") lines))))
