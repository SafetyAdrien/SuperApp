# Dependencies

Every version lives in `gradle/libs.versions.toml`. This table records how
each was chosen and how confident that choice is, since this scaffold could
not be validated with a real `./gradlew build` (see `PROJECT_STATUS.md`).

**Verified** = confirmed via web search against an official release-notes
page or Maven Central/mvnrepository listing, dated 2026-07-12.
**Best-effort** = inferred from the surrounding ecosystem (e.g. a versioning
pattern) without a direct confirmation; verify with `./gradlew
dependencies` or the artifact's Maven metadata before relying on it.

| Dependency | Version | Confidence | Notes |
|---|---|---|---|
| Android Gradle Plugin | 9.2.0 | Verified | Requires Gradle 9.4.1+, JDK 17, SDK Build Tools 36.0.0+, max API 37 |
| Gradle | 9.4.1 | Verified | Distribution download blocked in this session (see PROJECT_STATUS.md), not exercised |
| Kotlin | 2.3.10 | Verified | Matches AGP 9.2.0's bundled built-in-Kotlin default |
| KSP | 2.3.10 | Verified | Confirmed against Maven Central's `maven-metadata.xml` (2026-07-13) — KSP dropped the old `<kotlin>-<ksp>` combined version string as of 2.3.0; it now versions independently and this release explicitly targets Kotlin 2.4.0-era module naming while remaining compatible with recent 2.3.x Kotlin. The old `2.3.10-2.0.4` guess in this catalog never existed as a published artifact and failed to resolve — see `docs/DECISIONS.md` |
| Compose BOM | 2026.06.00 | Verified | |
| Room | 2.8.4 | Verified | Deliberately not Room 3.0.0 — see `docs/DECISIONS.md` |
| Navigation Compose | 2.9.8 | Verified | Deliberately not Navigation 3 (still alpha) — see `docs/DECISIONS.md` |
| Lifecycle | 2.11.0 | Verified | |
| DataStore | 1.2.1 | Verified | |
| WorkManager | 2.11.2 | Verified | |
| Paging | 3.4.2 | Verified | |
| Hilt (Dagger) | 2.59.2 | Verified | |
| androidx.hilt (navigation-compose/work/compiler) | 1.4.0 | Verified | |
| core-ktx | 1.19.0 | Verified | |
| activity-compose | 1.9.0 | Best-effort | 1.12.0 was beta-only at query time; 1.9.0 is the last confirmed stable |
| core-splashscreen | 1.2.0 | Verified | |
| kotlinx-coroutines | 1.11.0 | Verified | |
| kotlinx-serialization | 1.11.0 | Verified | |
| Coil | 3.5.0 | Verified | Coil 3.x (`io.coil-kt.coil3`), not the Coil 2.x `io.coil-kt` coordinates |
| JUnit4 / Truth / Turbine / MockK / Robolectric / androidx.test | pinned in catalog | Best-effort | Standard, low-risk testing libs; not individually re-verified this session |
| `com.android.tools:desugar_jdk_libs` | 2.1.5 | Best-effort | Needed for `isCoreLibraryDesugaringEnabled` |

## Before adding a new dependency

1. State the need in the PR/commit description.
2. Confirm it's actively maintained (recent releases, open issues addressed).
3. Use a stable release — no `-alpha`, `-beta`, `-rc`, or SNAPSHOT unless a
   stable equivalent genuinely does not exist yet, and document that
   exception in `docs/DECISIONS.md` (see the Navigation 3 entry for the
   template).
4. Check the license is compatible (Apache 2.0 / MIT / BSD are fine; ask
   before adding anything copyleft).
5. Add it to `gradle/libs.versions.toml`, never as a hardcoded string in a
   module's `build.gradle.kts`.
6. Update this table.
