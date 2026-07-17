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

## Sourcing note

This repo fills the Czech Republic's previously-open association-axis
gap (noted honestly at tick 139). The Czech Republic now has real,
individually verified facts across all three axes: country
([`cloud-itonami-iso3166-cze`](https://github.com/cloud-itonami/cloud-itonami-iso3166-cze)),
municipality
([`cloud-itonami-municipality-cze-prague`](https://github.com/cloud-itonami/cloud-itonami-municipality-cze-prague)),
and association (this repo).

`spcr.cz`'s own "o-nas"/"about-us" pages render successfully (unlike
several other Czech official domains encountered this session) but do
not surface founding-history text on those specific subpages — both
entries here were instead directly WebFetch-verified against
`cs.wikipedia.org`'s own article, independently corroborated by
Wikidata (Q12057458)'s own "inception" statement for the 1990 date.

## Scope

A **read-only reference/archive** catalog — not an Advisor⊣Governor
actuation actor. It proposes or executes nothing on SP ČR's behalf.

Coverage is reported honestly (see `association.facts/coverage`): an
association not in `catalog` has **no spec-basis**, full stop — never
fabricate one.

## Data

- `src/association/facts.cljc` — the catalog, source of truth.
- `schema/association-rule.edn` — DataScript schema.
- `data/datascript-tx.edn` — derived DataScript tx-data (query this
  alongside other `cloud-itonami`/`etzhayyim` compliance-fact sources via
  `com-junkawasaki/root`'s `scripts/compliance-fact-query.cljs`).

Both entries directly WebFetch-verified against `cs.wikipedia.org`'s
own article: the 5 May 1990 founding of Svaz průmyslu České republiky
in Ostrava (153 founding companies/organizations), and the 17 March
1994 general-assembly renaming to Svaz průmyslu a dopravy České
republiky (adding transport to its scope).

## License

AGPL-3.0-or-later (matches the `cloud-itonami-iso3166-*` /
`-municipality-*` / `-assoc-*` / `-lei-*` convention). Policy text
itself remains SP ČR's; this repo stores only citation metadata
(id/title/url/dates), not full text.
