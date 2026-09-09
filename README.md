# cloud-itonami-assoc-9411-cze-spcr

Industry rule/history catalog for the **Confederation of Industry of
the Czech Republic** (Svaz průmyslu a dopravy ČR, SP ČR) — the FIFTH
entry aligned to **ISIC 9411** (activities of business, employers, and
professional membership organizations), alongside
[`-9411-sau-fsc`](https://github.com/cloud-itonami/cloud-itonami-assoc-9411-sau-fsc)
(Saudi Arabia),
[`-9411-aut-wko`](https://github.com/cloud-itonami/cloud-itonami-assoc-9411-aut-wko)
(Austria),
[`-9411-irl-ibec`](https://github.com/cloud-itonami/cloud-itonami-assoc-9411-irl-ibec)
(Ireland), and
[`-9411-nzl-businessnz`](https://github.com/cloud-itonami/cloud-itonami-assoc-9411-nzl-businessnz)
(New Zealand). Part of the
[`cloud-itonami`](https://github.com/cloud-itonami) compliance-fact
family (ADR-2607141700, `cloud-itonami-compliance-fact-federation`,
in `com-junkawasaki/root`).

## Sourcing

The catalog began (2026-07-17) with two history entries taken from
`cs.wikipedia.org`, corroborated by Wikidata (Q12057458), because
`spcr.cz`'s own "o-nas"/"about-us" subpages were read at the time and
did not surface founding-history text. That was honest about its
sourcing and weak as a citation.

On **2026-09-09** the confederation's own document register at
<https://www.spcr.cz/dokumenty-sp-cr> was read and **twelve entries
were added from documents fetched from `spcr.cz` itself**. Every date
is quoted from the document's own text rather than from a description
of it — the Statutes' Article 19 ("Tyto Stanovy byly schváleny valnou
hromadou Svazu dne 13. května 2026. Mění a nahrazují Stanovy Svazu
přijaté valnou hromadou konanou dne 15. června 2021."), the Code of
Ethics' closing line ("schválila valná hromada dne 15. června 2021"),
the Payment Rules' own header ("SCHVÁLENÝ VALNOU HROMADOU DNE
13. 5. 2026 A ÚČINNÝ OD 14. 5. 2026"), and so on.

The two Wikipedia entries are **kept**: the 1990 founding date is not
stated in any of the primary documents found, so replacing them would
lose a fact and deleting them would claim the confederation has no
recorded founding. `:association-rule/url-provenance` is what tells
the two kinds apart, and the test suite asserts the keyword matches
the host the URL is actually on.

### Checking the citations

The suite is hermetic, so it cannot know whether a URL still resolves:

```
nbb scripts/verify_citations.cljs     # 0 all ok / 1 something rotted / 2 REFUSED
```

It fetches each **distinct** URL once (four entries cite four articles
of the same Statutes PDF) and compares the response against the
`:association-rule/media-type` the catalog records.

That field is not decoration. **`spcr.cz` serves PDFs from
extension-less paths** — `https://www.spcr.cz/stanovy-2026` is a 233 KB
PDF — so a checker that infers the type from the URL would expect HTML
for most of this catalog and pass whatever came back. Recording it per
entry is also what separates "the PDF is still there" from "the PDF
became a landing page saying it moved".

A 404 here is a real 404: `spcr.cz` was probed on 2026-09-09 with six
plausible-but-invented paths (`/stanovy-2025`, `/stanovy-2027`,
`/platebni-rad-2026-05-15`, `/images/Eticky_kodex_SPCR_2020.pdf`,
`/media/99999`, `/no-such-page-xyz`) and answered 404 to all six. It
does not soft-404.

Link rot is the expected failure and is not automatically a bug here:
SP ČR renumbers `/media/NNNNN` when it reissues a document, and the
year-stamped paths change when the general assembly approves a new
text. A non-2xx means go to <https://www.spcr.cz/dokumenty-sp-cr> and
find where the document went — it does not mean delete the entry, and
it never means substitute a URL that was not read off an `spcr.cz`
page.

## Scope

A **read-only reference/archive** catalog — not an Advisor⊣Governor
actuation actor. It proposes or executes nothing on SP ČR's behalf.

Coverage is reported honestly (see `association.facts/coverage`): an
association not in `catalog` has **no spec-basis**, full stop — never
fabricate one.

## Data

- `data/datascript-tx.edn` — **the source**. The other two readings are
  derived from it.
- `src/association/facts.cljc` — the catalog as a map;
  `association.facts-test` asserts it agrees with the data file field
  for field.
- `src/association_facts.kotoba` — **generated**; do not edit by hand.

```
nbb scripts/gen_association_facts_kotoba.cljs           # write
nbb scripts/gen_association_facts_kotoba.cljs --check   # exit 1 if stale
```

- `schema/association-rule.edn` — DataScript schema. Query this
  alongside other `cloud-itonami`/`etzhayyim` compliance-fact sources
  via `com-junkawasaki/root`'s `scripts/compliance-fact-query.cljs`.

Several entries share a URL: Articles 1(2), 2(2)(a), 9 and 19 of the
Statutes are four distinct rules inside one PDF. Ids are unique; URLs
are not, and the suite asserts the weaker true thing — that no
(url, title) pair repeats, and that the catalog reaches at least ten
distinct documents.

One entry carries **no** `:established-date` on purpose:
`spcr.cz` gives the predecessor's founding year (1918) and no day, and
a completed date would be indistinguishable from a fact.

## License

AGPL-3.0-or-later (matches the `cloud-itonami-iso3166-*` /
`-municipality-*` / `-assoc-*` / `-lei-*` convention). Policy text
itself remains SP ČR's; this repo stores only citation metadata
(id/title/url/dates), not full text.
