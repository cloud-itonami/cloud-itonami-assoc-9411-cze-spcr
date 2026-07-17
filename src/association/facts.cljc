(ns association.facts
  "Industry rule/history catalog for the Confederation of Industry of
  the Czech Republic (Svaz průmyslu a dopravy ČR, SP ČR) -- a 47th
  industry-association-level source (see
  cloud-itonami-assoc-9411-sau-fsc, -9411-aut-wko, -9411-irl-ibec,
  -9411-nzl-businessnz for the first four) per ADR-2607141700
  (cloud-itonami-compliance-fact-federation). The FIFTH entry aligned
  to ISIC 9411 (activities of business, employers, and professional
  membership organizations). Fills the Czech Republic's
  previously-open association-axis gap (noted honestly at tick 139)
  -- the Czech Republic now has real, individually verified facts
  across ALL THREE axes (country: cloud-itonami-iso3166-cze
  statute.facts, tick 139; municipality:
  cloud-itonami-municipality-cze-prague, tick 138; association: this
  entry, tick 140).

  spcr.cz's own 'o nas'/'about-us' pages render successfully (unlike
  several other Czech official domains this session) but do not
  surface founding-history text on those specific subpages, so both
  entries here were directly WebFetch-verified against
  cs.wikipedia.org's own article, which quotes verbatim: '5. května
  1990, kdy byl v Ostravě z iniciativy 153 podniků a organizací
  založen Svaz průmyslu České republiky' (5 May 1990, founded in
  Ostrava on the initiative of 153 companies and organizations) and
  'Dne 17. března 1994 schválila valná hromada přejmenování na Svaz
  průmyslu a dopravy České republiky' (on 17 March 1994 the general
  assembly approved renaming to Svaz průmyslu a dopravy České
  republiky, adding transport to its scope). The 1990 date is
  independently corroborated by Wikidata Q12057458's own 'inception'
  statement (5 May 1990) -- two independent directly-read sources in
  agreement, not a single WebSearch synthesis. No personal names of
  office-holders (e.g. any current/former president) are persisted
  here.

  An association not in `catalog` has NO spec-basis, full stop; never
  fabricate one.")

(def catalog
  "association-slug -> vector of association-rule entries."
  {"spcr"
   [{:association-rule/id "spcr.founding-1990"
     :association-rule/title "Svaz průmyslu České republiky founded in Ostrava on the initiative of 153 companies and organizations (Czech Wikipedia, corroborated by Wikidata Q12057458 inception statement)"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://cs.wikipedia.org/wiki/Svaz_pr%C5%AFmyslu_a_dopravy_%C4%8CR"
     :association-rule/url-provenance :wikipedia-corroborated
     :association-rule/established-date "1990-05-05"
     :association-rule/retrieved-at "2026-07-17"
     :association-rule/topic #{:governance}}
    {:association-rule/id "spcr.renaming-1994-add-transport"
     :association-rule/title "General assembly approved renaming to Svaz průmyslu a dopravy České republiky (Confederation of Industry AND TRANSPORT), expanding scope (Czech Wikipedia)"
     :association-rule/association "spcr"
     :association-rule/isic "9411"
     :association-rule/country "CZE"
     :association-rule/kind :governance-program
     :association-rule/url "https://cs.wikipedia.org/wiki/Svaz_pr%C5%AFmyslu_a_dopravy_%C4%8CR"
     :association-rule/url-provenance :wikipedia-corroborated
     :association-rule/established-date "1994-03-17"
     :association-rule/retrieved-at "2026-07-17"
     :association-rule/topic #{:governance}}]})

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
      :note (str "cloud-itonami-assoc-9411-cze-spcr Wave 0 (ADR-2607141700): "
                 (count (get catalog "spcr")) " SP ČR entries seeded "
                 "with Czech-Wikipedia + Wikidata Q12057458 corroboration "
                 "(spcr.cz's own about-us subpages render but lack founding-history text). "
                 "Extend `association.facts/catalog`, never fabricate an id/url.")})))

(defn by-topic [association topic]
  (filterv #(contains? (:association-rule/topic %) topic) (spec-basis association)))
