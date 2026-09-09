;; scripts/gen_association_facts_kotoba.cljs — emit src/association_facts.kotoba
;; from data/datascript-tx.edn.
;;
;; The Kotoba port is a positional table: `entry-field` dispatches on an index
;; and answers a field by name, so every field of every entry appears once as a
;; literal. Fourteen entries times twelve fields is 168 literals, which is well
;; past the size at which a human transcribes one of them wrong, so the file is
;; generated and `test/association_facts_kotoba_parity_test.clj` compares every
;; one of them back against the .cljc.
;;
;;   nbb scripts/gen_association_facts_kotoba.cljs           # write
;;   nbb scripts/gen_association_facts_kotoba.cljs --check   # exit 1 if stale
;;
;; --check is the useful mode in CI: it fails when someone edits the catalog and
;; forgets to regenerate, which is the drift the parity test would otherwise
;; only catch as a mysterious field mismatch.

(ns gen-association-facts-kotoba
  (:require ["fs" :as fs]
            [clojure.string :as str]
            [cljs.reader :as reader]))

(def ^:private tx-path "data/datascript-tx.edn")
(def ^:private out-path "src/association_facts.kotoba")
(def ^:private slug "spcr")

(def ^:private fields
  ;; The order the port answers them in. `established-date` and
  ;; `last-revised-date` are each absent from some entries; the port answers
  ;; `none` for those, which is what the .cljc holds. `media-type` is what the
  ;; URL actually served -- spcr.cz serves PDFs from extension-less paths, so it
  ;; cannot be inferred from the URL and is carried as a field.
  ["id" "title" "association" "isic" "country" "kind" "url" "url-provenance"
   "media-type" "established-date" "last-revised-date" "retrieved-at"])

(def ^:private field->attr
  (into {} (map (fn [f] [f (keyword "association-rule" f)]) fields)))

(defn- read-entries []
  (let [tx (reader/read-string (fs/readFileSync tx-path "utf8"))]
    (when-not (vector? tx)
      (println "REFUSED:" tx-path "did not read as a vector")
      (js/process.exit 2))
    tx))

(defn- field-value
  "The literal string the port must answer, or nil for `none`. Keywords are
   named, so `:self-regulatory-code` answers \"self-regulatory-code\" — the same
   coercion the parity test applies to the .cljc side."
  [entry f]
  (let [v (get entry (field->attr f))]
    (cond (nil? v) nil
          (keyword? v) (name v)
          (string? v) v
          :else (do (println "REFUSED: non-scalar field" f (pr-str v))
                    (js/process.exit 2)))))

(defn- kotoba-string
  "Kotoba string literals are double-quoted. Refuse rather than emit an escape
   we have not verified the reader accepts — no catalog value needs one."
  [s]
  (when (or (str/includes? s "\"") (str/includes? s "\\") (str/includes? s "\n"))
    (println "REFUSED: catalog value needs an escape this generator will not invent:" (pr-str s))
    (js/process.exit 2))
  (str "\"" s "\""))

(defn- some-of [s] (str "(option-some-of [:option :string] " (kotoba-string s) ")"))
(def ^:private none-of "(option-none-of [:option :string])")

(defn- entry-field-cond
  "The per-entry `cond` over field names."
  [entry indent]
  (let [pad (apply str (repeat indent " "))]
    (str pad "(cond\n"
         (->> fields
              (keep (fn [f]
                      (when-let [v (field-value entry f)]
                        (str pad "  (string=? f " (kotoba-string f) ") " (some-of v)))))
              (str/join "\n"))
         "\n" pad "  :else " none-of ")")))

(defn- index-dispatch
  "`(cond (= i 0) <body 0> (= i 1) <body 1> ... :else <fallback>)`."
  [bodies fallback indent]
  (let [pad (apply str (repeat indent " "))]
    (str pad "(cond\n"
         (->> bodies
              (map-indexed (fn [i b] (str pad "  (= i " i ")\n" b)))
              (str/join "\n"))
         "\n" pad "  :else " fallback ")")))

(defn- topic-names [entry] (mapv name (:association-rule/topic entry)))

(defn- topic-dispatch
  "`topic` answers by (entry index, topic index)."
  [entries]
  (str "  (cond\n"
       (->> entries
            (map-indexed
             (fn [i e]
               (str "    (= i " i ")\n"
                    "    (cond\n"
                    (->> (topic-names e)
                         (map-indexed (fn [t nm] (str "      (= t " t ") " (some-of nm))))
                         (str/join "\n"))
                    "\n      :else " none-of ")")))
            (str/join "\n"))
       "\n    :else " none-of ")"))

(defn- by-topic
  "Per topic name: how many entries carry it, and the id of the first one.
   Catalog order, which is the order `association.facts/by-topic` filters in."
  [entries]
  (let [names (distinct (mapcat topic-names entries))]
    (mapv (fn [nm]
            (let [hits (filterv #(some #{nm} (topic-names %)) entries)]
              {:name nm
               :count (count hits)
               :first-id (:association-rule/id (first hits))}))
          names)))

(defn- render [entries]
  (let [n (count entries)
        bt (by-topic entries)]
    (str
     ";; The SP ČR source catalog, as Kotoba.\n"
     ";;\n"
     ";; GENERATED from data/datascript-tx.edn by\n"
     ";; scripts/gen_association_facts_kotoba.cljs -- do not edit by hand; edit the\n"
     ";; data file and regenerate. `src/association/facts.cljc` holds the same\n"
     ";; catalog, and association.facts-test asserts the two agree, so the data\n"
     ";; file is the one source all three readings come from. This one reaches the\n"
     ";; Kotoba oracle, wasm and both native ISAs, which the .cljc cannot.\n"
     ";;\n"
     ";; `:topic` is a SET in the .cljc. A set has no order and `topic` is indexed\n"
     ";; by position, so the generator uses the order the data file writes.\n"
     ";; `by-topic-id` answers by name and is unaffected.\n"
     ";;\n"
     ";; Parity: test/association_facts_kotoba_parity_test.clj compares every field\n"
     ";; of every entry against the .cljc, so this file cannot drift from the data\n"
     ";; without the suite saying so.\n"
     "\n"
     "(ns association-facts\n"
     "  (:export [main association-covered? entry-count entry-field topic-count\n"
     "            topic by-topic-count by-topic-id coverage-note]))\n"
     "\n"
     "(defn association-covered? [a :string] :bool (string=? a " (kotoba-string slug) "))\n"
     "\n"
     "(defn entry-count [a :string] :i64 (if (association-covered? a) " n " 0))\n"
     "\n"
     "(defn valid-entry? [a :string i :i64] :bool\n"
     "  (if (association-covered? a) (if (< i 0) false (< i " n ")) false))\n"
     "\n"
     "(defn entry-field [a :string i :i64 f :string] [:option :string]\n"
     "  (if (valid-entry? a i)\n"
     (index-dispatch (map #(entry-field-cond % 6) entries) none-of 4) "\n"
     "    " none-of "))\n"
     "\n"
     "(defn topic-count [a :string i :i64] :i64\n"
     "  (if (valid-entry? a i)\n"
     (index-dispatch (map (fn [e] (str "      " (count (topic-names e)))) entries) "0" 4) "\n"
     "    0))\n"
     "\n"
     "(defn topic [a :string i :i64 t :i64] [:option :string]\n"
     "  (if (valid-entry? a i)\n"
     (topic-dispatch entries) "\n"
     "    " none-of "))\n"
     "\n"
     "(defn by-topic-count [a :string candidate :string] :i64\n"
     "  (if (association-covered? a)\n"
     "    (cond\n"
     (->> bt (map (fn [{:keys [name count]}]
                    (str "      (string=? candidate " (kotoba-string name) ") " count)))
          (str/join "\n")) "\n"
     "      :else 0)\n"
     "    0))\n"
     "\n"
     "(defn by-topic-id [a :string candidate :string i :i64] [:option :string]\n"
     "  (if (association-covered? a)\n"
     "    (if (= i 0)\n"
     "      (cond\n"
     (->> bt (map (fn [{:keys [name first-id]}]
                    (str "        (string=? candidate " (kotoba-string name) ") " (some-of first-id))))
          (str/join "\n")) "\n"
     "        :else " none-of ")\n"
     "      " none-of ")\n"
     "    " none-of "))\n"
     "\n"
     "(defn coverage-note [a :string] [:option :string]\n"
     "  (if (association-covered? a)\n"
     "    " (some-of (str "cloud-itonami-assoc-9411-cze-spcr (ADR-2607141700): " n
                         " spcr entries, each with a citation that was fetched. Extend the sovereign catalog; never fabricate a rule id/url.")) "\n"
     "    " none-of "))\n"
     "\n"
     "(defn main [] :i64 0)\n")))

(let [entries (read-entries)
      out (render entries)
      check? (some #{"--check"} (js->clj (.slice js/process.argv 2)))]
  (if check?
    (let [cur (when (fs/existsSync out-path) (fs/readFileSync out-path "utf8"))]
      (if (= cur out)
        (println "OK" out-path "is up to date with" tx-path
                 (str "(" (count entries) " entries)"))
        (do (println "STALE:" out-path "does not match" tx-path
                     "- run: nbb scripts/gen_association_facts_kotoba.cljs")
            (js/process.exit 1))))
    (do (fs/writeFileSync out-path out)
        (println "wrote" out-path (str "(" (count entries) " entries, "
                                       (count out) " bytes)")))))
