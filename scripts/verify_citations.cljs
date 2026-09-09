;; scripts/verify_citations.cljs — fetch every URL in the catalog and report
;; what the server actually answered.
;;
;; The repo's whole claim is that each entry cites a document that exists at the
;; URL recorded for it. Nothing in the test suite can check that -- the suite is
;; hermetic on purpose -- so the claim is only ever as good as the last time
;; someone looked. This is how you look.
;;
;;   nbb scripts/verify_citations.cljs
;;
;; Exit 0 every URL answered 2xx with the media type the catalog records.
;;      1 at least one did not (the line says which, and what came back).
;;      2 REFUSED -- the catalog could not be read, so nothing was checked.
;;        Not 0: "could not check" must not look like "checked, all fine".
;;
;; The expected media type is READ FROM THE ENTRY, not inferred from the URL.
;; spcr.cz serves PDFs from extension-less paths -- https://www.spcr.cz/stanovy-2026
;; is a 233 KB PDF -- so an extension rule would expect HTML for most of this
;; catalog and pass whatever came back. It would also miss the failure that
;; actually matters here: spcr.cz answers a removed document with a 200 landing
;; page in some cases and a 404 in others, and only the media type separates
;; "the PDF is still there" from "the PDF became a web page saying it moved".
;;
;; Link rot is the expected failure and it is not automatically a bug in this
;; repo: SP ČR renumbers /media/NNNNN when it reissues a document, and the
;; year-stamped paths (/stanovy-2026, /platebni-rad-2026-05-14) change when the
;; general assembly approves a new text. A non-2xx means go to
;; https://www.spcr.cz/dokumenty-sp-cr and find where the document went -- it
;; does not mean delete the entry, and it never means substitute a URL that was
;; not read off an spcr.cz page.
;;
;; A 404 here is a real 404: spcr.cz was probed on 2026-09-09 with six
;; plausible-but-invented paths (/stanovy-2025, /stanovy-2027,
;; /platebni-rad-2026-05-15, /images/Eticky_kodex_SPCR_2020.pdf, /media/99999,
;; /no-such-page-xyz) and answered 404 to all six. It does not soft-404.

(ns verify-citations
  (:require ["fs" :as fs]
            [kotoba.lang.text :as str]
            [cljs.reader :as reader]))

(def ^:private tx-path "data/datascript-tx.edn")

(defn- refuse! [msg]
  (println "REFUSED:" msg)
  (println "nothing was checked; this is not a pass")
  (js/process.exit 2))

(def ^:private entries
  (let [tx (try (reader/read-string (fs/readFileSync tx-path "utf8"))
                (catch :default e (refuse! (str tx-path " did not read: " (.-message e)))))]
    (when-not (and (vector? tx) (seq tx))
      (refuse! (str tx-path " read as " (type tx) " with no entries")))
    ;; A `(str ...)` where a URL belongs reads back as a list without throwing,
    ;; and `fetch` would then be handed something that is not a string. The same
    ;; goes for the media type: a non-string expectation compares false against
    ;; every response, which reads as total link rot rather than as a bad file.
    (doseq [e tx]
      (doseq [k [:association-rule/url :association-rule/media-type]]
        (when-not (string? (get e k))
          (refuse! (str "entry " (pr-str (:association-rule/id e))
                        " has a non-string " k ": " (pr-str (get e k)))))))
    tx))

;; Distinct URLs: four entries cite four articles of the same Statutes PDF, and
;; fetching it four times checks nothing extra while asking spcr.cz for 233 KB
;; three more times than needed.
(def ^:private citations
  (->> entries
       (map (fn [e] {:url (:association-rule/url e)
                     :want (:association-rule/media-type e)
                     :id (:association-rule/id e)}))
       (group-by :url)
       (map (fn [[url es]]
              {:url url
               :want (distinct (map :want es))
               :ids (mapv :id es)}))
       vec))

(defn- fetch-one [url]
  (-> (js/fetch url #js {:method "GET" :redirect "follow"
                         :headers #js {"User-Agent" "cloud-itonami-assoc-9411-cze-spcr citation check"}})
      (.then (fn [r] {:status (.-status r)
                      :type (or (.get (.-headers r) "content-type") "")}))
      (.catch (fn [e] {:status 0 :type "" :error (.-message e)}))))

(defn- check [{:keys [url want ids]}]
  (.then (fetch-one url)
         (fn [{:keys [status type error]}]
           ;; Two entries recording different media types for one URL is a
           ;; catalog bug, not a fetch result; say so rather than picking one.
           (let [disagree? (< 1 (count want))
                 want1 (first want)
                 ok? (and (not disagree?)
                          (<= 200 status 299)
                          (str/includes? type want1))]
             {:url url :ids ids :status status :type type :error error
              :want (str/join "/" want) :disagree? disagree? :ok? ok?}))))

(-> (js/Promise.all (clj->js (map check citations)))
    (.then
     (fn [results]
       (let [results (js->clj results :keywordize-keys true)
             bad (remove :ok? results)]
         (doseq [{:keys [ok? status type url ids error want disagree?]} results]
           (println (str (if ok? "  ok  " "FAIL  ") status
                         " [" (first (str/split (or type "") #";")) "] "
                         url "  (" (str/join ", " ids) ")"
                         (when-not ok?
                           (str "  -- wanted 2xx " want
                                (when disagree? " -- entries disagree on the media type")
                                (when error (str " / " error)))))))
         ;; The floor: a run that checked nothing must not print a clean line.
         (println (str "CHECKED\t" (count results)
                       "\tURLS\tFOR\t" (count entries)
                       "\tENTRIES\tFAILED\t" (count bad)))
         (when (zero? (count results)) (refuse! "no entries were checked"))
         (js/process.exit (if (seq bad) 1 0)))))
    (.catch (fn [e] (refuse! (str "the run itself failed: " (.-message e))))))
