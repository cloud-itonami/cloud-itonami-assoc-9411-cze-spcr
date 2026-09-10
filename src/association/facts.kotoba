(ns association.facts
  "Industry rule/history catalog for the Confederation of Industry of the Czech
  Republic (Svaz průmyslu a dopravy ČR, SP ČR) -- an industry-association-level
  source aligned to ISIC 9411 (activities of business, employers, and
  professional membership organizations) per ADR-2607141700
  (cloud-itonami-compliance-fact-federation).

  The catalog began (2026-07-17) as two history entries taken from
  cs.wikipedia.org, because spcr.cz's own 'o nas' subpages were read at the time
  and did not surface founding-history text. That was honest about its sourcing
  and weak as a citation: an employers' confederation's own statutes, code of
  ethics and payment rules are primary documents, and they are published.

  On 2026-09-09 the confederation's own document register at
  https://www.spcr.cz/dokumenty-sp-cr was read and twelve entries were added
  from documents fetched from spcr.cz itself, each date quoted from the
  document's own text rather than from a description of it -- the Statutes'
  Article 19 ('Tyto Stanovy byly schváleny valnou hromadou Svazu dne 13. května
  2026'), the Code of Ethics' closing line ('schválila valná hromada dne 15.
  června 2021'), the Payment Rules' own header, and so on. The two Wikipedia
  entries are kept: the 1990 founding date is not stated in any of the primary
  documents found, so replacing them would lose a fact, and deleting them would
  claim the confederation has no recorded founding.

  Three notes on shape, because each is a way this catalog could quietly become
  wrong:

  - Several entries cite the same URL. The Statutes are one PDF, and Articles
    1(2), 2(2)(a), 9 and 19 are four distinct rules inside it. Entry ids are
    unique; URLs are not, and the test asserts the weaker true thing.
  - `:association-rule/media-type` records what the URL actually served.
    spcr.cz serves PDFs from extension-less paths -- `/stanovy-2026` is a PDF --
    so a citation checker that infers the type from the URL is wrong here by
    construction.
  - An absent date is the honest answer where the page gives no date. The
    predecessor entry records a year and no day, so it carries no
    `:established-date` at all rather than an invented 1918-01-01.

  An association not in `catalog` has NO spec-basis, full stop; never fabricate
  one."
  (:require [kotoba.lang.text :as str]))

(def catalog
  "association-slug -> vector of association-rule entries.

   Written out here; `data/datascript-tx.edn` holds the same catalog as
   tx-data and `src/association_facts.kotoba` is generated from that file.
   `association.facts-test` asserts all of them agree."
  {"spcr"
   [{:association-rule/id "spcr.founding-1990"
     :association-rule/title "Svaz průmyslu České republiky founded in Ostrava on the initiative of 153 companies and organizations (Czech Wikipedia, corroborated by Wikidata Q12057458 inception statement)"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://cs.wikipedia.org/wiki/Svaz_pr%C5%AFmyslu_a_dopravy_%C4%8CR"
     :association-rule/url-provenance :wikipedia-corroborated
     :association-rule/media-type "text/html"
     :association-rule/established-date "1990-05-05"
     :association-rule/retrieved-at "2026-07-17"
     :association-rule/topic #{:governance :history}}
    {:association-rule/id "spcr.renaming-1994-add-transport"
     :association-rule/title "General assembly approved renaming to Svaz průmyslu a dopravy České republiky (Confederation of Industry AND TRANSPORT), expanding scope (Czech Wikipedia)"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://cs.wikipedia.org/wiki/Svaz_pr%C5%AFmyslu_a_dopravy_%C4%8CR"
     :association-rule/url-provenance :wikipedia-corroborated
     :association-rule/media-type "text/html"
     :association-rule/established-date "1994-03-17"
     :association-rule/retrieved-at "2026-07-17"
     :association-rule/topic #{:governance :history}}
    {:association-rule/id "spcr.statutes-2026"
     :association-rule/title "Stanovy Svazu průmyslu a dopravy České republiky (Statutes) -- Article 19: approved by the general assembly on 13 May 2026, amending and replacing the Statutes adopted by the general assembly on 15 June 2021"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/stanovy-2026"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "application/pdf"
     :association-rule/last-revised-date "2026-05-13"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:governance}}
    {:association-rule/id "spcr.employers-organisation-legal-basis"
     :association-rule/title "Article 1(2) of the Statutes: SP ČR is an organisation of employers within the meaning of § 320 of Act No. 262/2006 Sb. (Labour Code), § 3025 of Act No. 89/2012 Sb. (Civil Code) and ILO Convention No. 87"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/stanovy-2026"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "application/pdf"
     :association-rule/established-date "2026-05-13"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:governance :labour-law}}
    {:association-rule/id "spcr.tripartite-social-dialogue"
     :association-rule/title "Article 2(2)(a) of the Statutes: SP ČR asserts and protects its members' interests in the Council of Economic and Social Agreement of the Czech Republic (Rada hospodářské a sociální dohody ČR), the national tripartite body, alongside public authorities and trade unions"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/stanovy-2026"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "application/pdf"
     :association-rule/established-date "2026-05-13"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:social-dialogue :governance}}
    {:association-rule/id "spcr.governing-bodies"
     :association-rule/title "Article 9 of the Statutes: the organs of SP ČR are the general assembly; the elected organs (board of directors, president and vice-presidents, supervisory board, conciliation committee); the advisory organs (membership base council, expert teams); and the executive organs (secretariat, director general). The term of office of elected organs is four years"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/stanovy-2026"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "application/pdf"
     :association-rule/established-date "2026-05-13"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:governance}}
    {:association-rule/id "spcr.ethics-code-2021"
     :association-rule/title "Etický kodex Svazu průmyslu a dopravy ČR (Code of Ethics) -- minimum standards of conduct binding on all members and on members of the elected organs, covering legal compliance, competition law, equal treatment and sustainable business; approved by the general assembly on 15 June 2021"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :self-regulatory-code
     :association-rule/url "https://www.spcr.cz/images/Eticky_kodex_SPCR_2021.pdf"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "application/pdf"
     :association-rule/established-date "2021-06-15"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:ethics :governance}}
    {:association-rule/id "spcr.payment-rules-2026"
     :association-rule/title "Platební řád (Payment Rules) -- annual membership contributions and partner and observer payments issued under Article 8(2) of the Statutes; approved by the general assembly on 13 May 2026 and effective from 14 May 2026"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/platebni-rad-2026-05-14"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "application/pdf"
     :association-rule/established-date "2026-05-13"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:membership :governance}}
    {:association-rule/id "spcr.candidate-nomination-rules-2026"
     :association-rule/title "Pravidla pro návrh kandidátů do orgánů Svazu (Rules for Nominating Candidates to the Organs of the Confederation) -- approved by the general assembly on 13 May 2026; fixes the order of election and the eligibility condition, and the size of each organ: eighteen elected board members, seven vice-presidents, a five-member supervisory board and a three-member conciliation committee"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/pravidla-pro-navrh-kandidatu-2026"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "application/pdf"
     :association-rule/established-date "2026-05-13"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:elections :governance}}
    {:association-rule/id "spcr.programme-declaration-2026"
     :association-rule/title "Programové prohlášení Svazu průmyslu a dopravy ČR na rok 2026 (Programme Declaration for 2026) -- approved by the general assembly on 9 December 2025; states the Vize Česko 2029 target (the Czech Republic in the EU TOP 10 by GDP per capita and the world TOP 20 of the Global Innovation Index) and the confederation's ten priorities"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/media/27070"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "application/pdf"
     :association-rule/established-date "2025-12-09"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:economic-policy :governance}}
    {:association-rule/id "spcr.trade-fair-committee-statutes"
     :association-rule/title "Stanovy Veletržního výboru při Svazu průmyslu a dopravy ČR (Statutes of the Trade Fair Committee) -- an expert team constituted under the Statutes to coordinate Czech industry's participation in domestic and foreign trade fairs; signed in Prague on 23 January 2013"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/media/25986"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "application/pdf"
     :association-rule/established-date "2013-01-23"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:trade-promotion :governance}}
    {:association-rule/id "spcr.predecessor-first-union-of-industrialists"
     :association-rule/title "SP ČR's own account of its lineage: in 2018 exactly one hundred years had passed since the founding of the first Český svaz průmyslníků (Czech Union of Industrialists), which the confederation names as its predecessor. The same page states SP ČR is a mandatory consultation body for many draft legal regulations. The year is all the page gives; no day or month is recorded here"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/kdo-jsme"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "text/html"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:history :governance}}
    {:association-rule/id "spcr.eu-priorities-2024-2029"
     :association-rule/title "Priority českého byznysu v Evropské unii pro roky 2024-2029 (Priorities of Czech Business in the European Union) -- twelve steps for the competitiveness of Czech and European business, addressed to the European Parliament and Commission for their 2024-2029 term"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/prioritybyznysu"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "text/html"
     :association-rule/last-revised-date "2025-06-02"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:economic-policy :eu-affairs}}
    {:association-rule/id "spcr.vision-cesko-2029"
     :association-rule/title "Česko v roce 2029: vize hospodářské politiky Svazu průmyslu (Czechia in 2029: the confederation's economic-policy vision) -- published 3 October 2024; sets targets including above-EU-average DESI scores and 25 % of Czech firms using artificial intelligence by 2029"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://www.spcr.cz/cesko2029"
     :association-rule/url-provenance :official-association-site
     :association-rule/media-type "text/html"
     :association-rule/established-date "2024-10-03"
     :association-rule/retrieved-at "2026-09-09"
     :association-rule/topic #{:economic-policy}}]})

(defn spec-basis [association] (get catalog association))

(defn coverage
  ([] (coverage (keys catalog)))
  ([associations]
   (let [have (filter catalog associations)
         missing (remove catalog associations)]
     {:requested (count associations)
      :covered (count have)
      :covered-associations (vec (sort have))
      :missing-associations (vec (sort missing))
      :note (str "cloud-itonami-assoc-9411-cze-spcr (ADR-2607141700): "
                 (count (get catalog "spcr")) " spcr entries, "
                 (count (filter #(= :official-association-site
                                    (:association-rule/url-provenance %))
                                (get catalog "spcr")))
                 " of them cited to spcr.cz itself and the rest to "
                 "cs.wikipedia.org corroborated by Wikidata. "
                 "Extend the sovereign catalog; never fabricate a rule id/url.")})))

(defn by-topic [association topic]
  (filterv #(contains? (:association-rule/topic %) topic) (spec-basis association)))

(defn by-kind [association kind]
  (filterv #(= kind (:association-rule/kind %)) (spec-basis association)))

(defn primary-sourced
  "The entries cited to the confederation's own site, as opposed to the
   Wikipedia-corroborated history entries. The distinction is the point of
   `:association-rule/url-provenance`, so it is worth being able to ask for."
  [association]
  (filterv #(= :official-association-site (:association-rule/url-provenance %))
           (spec-basis association)))

(defn citations
  "Every distinct URL in the catalog, in catalog order -- what
   `scripts/verify_citations.cljs` goes and fetches."
  [association]
  (->> (spec-basis association) (map :association-rule/url) distinct vec))

(defn describe
  "One line per entry, for a human reading the catalog rather than querying it."
  [association]
  (mapv (fn [e] (str (:association-rule/id e) "  "
                     (or (:association-rule/established-date e)
                         (:association-rule/last-revised-date e)
                         "(undated)")
                     "  " (str/join "," (sort (map name (:association-rule/topic e))))))
        (spec-basis association)))
