package com.example.clear.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class WarningAnalyzerTest {

    private val zone: ZoneId = ZoneOffset.UTC
    private val start: Instant = Instant.parse("2026-01-01T08:00:00Z")
    private val analyzer = EarlyWarningAnalyzer()

    private fun day(n: Long): Instant = start.plus(n, ChronoUnit.DAYS)
    private fun rating(d: Long, value: Int) = Rating(day(d), value)

    /** Normale Phase: 12 Bewertungen an Tag 0, 2, 4 ... 22. Genug fuer eine Baseline. */
    private fun stable(value: Int = 7): List<Rating> = (0L..22L step 2).map { rating(it, value) }

    private fun analyze(ratings: List<Rating>, nowDay: Long) =
        analyzer.analyzeCategory(ratings, day(nowDay), zone)

    // ---------- Baseline ----------

    @Test
    fun baselineIsTheMedianOfRatings() {
        val values = listOf(6, 7, 8, 7, 7, 6, 8, 7, 7, 7, 6, 8)
        val ratings = values.mapIndexed { index, v -> rating(index * 2L, v) }
        assertEquals(7.0, BaselineCalculator().calculate(ratings, zone)!!, 0.0001)
    }

    @Test
    fun medianOfEvenCountIsAverageOfMiddleValues() {
        assertEquals(6.5, median(listOf(5, 6, 7, 8)), 0.0001)
        assertEquals(6.0, median(listOf(5, 6, 7)), 0.0001)
    }

    @Test
    fun tooFewRatingsGiveNoBaselineAndNoWarning() {
        // nur 9 Bewertungen, auch wenn die letzten niedrig sind
        val ratings = (0L..24L step 4).map { rating(it, 7) } + rating(26, 3) + rating(27, 3)
        val result = analyze(ratings, 28)
        assertNull(result.baseline)
        assertEquals(WarningState.Normal, result.state)
    }

    @Test
    fun tooShortObservationPeriodGivesNoBaselineAndNoWarning() {
        // 14 Bewertungen, aber nur ueber 14 Tage verteilt (weniger als 21)
        val ratings = (0L..11L).map { rating(it, 7) } + rating(12, 3) + rating(13, 3)
        val result = analyze(ratings, 14)
        assertNull(result.baseline)
        assertEquals(WarningState.Normal, result.state)
    }

    @Test
    fun baselineIsNotLoweredByALongBadPhase() {
        val badPhase = (24L..52L step 2).map { rating(it, 3) }
        val ratings = stable() + badPhase
        val result = analyze(ratings, 53)
        assertEquals(7.0, result.baseline!!, 0.0001)
        assertEquals(WarningState.ContinuedLow, result.state)
    }

    // ---------- Warnregeln ----------

    @Test
    fun singleLowValueGivesNoWarning() {
        val result = analyze(stable() + rating(24, 3), 25)
        assertNotNull(result.baseline)
        assertEquals(WarningState.Normal, result.state)
    }

    @Test
    fun twoPointsBelowMedianCountsAsLowOnePointDoesNot() {
        val twoBelow = analyze(stable() + rating(24, 5) + rating(26, 5), 27)
        val oneBelow = analyze(stable() + rating(24, 6) + rating(26, 6), 27)
        assertEquals(WarningState.Sinking, twoBelow.state)
        assertEquals(WarningState.Normal, oneBelow.state)
    }

    @Test
    fun twoLowValuesOutOfLastThreeGiveSinking() {
        val result = analyze(stable() + rating(24, 4) + rating(26, 4), 27)
        assertEquals(WarningState.Sinking, result.state)
    }

    @Test
    fun lowValuesFurtherThan14DaysApartGiveNoWarning() {
        val ratings = stable() + rating(30, 4) + rating(35, 7) + rating(46, 4)
        assertEquals(WarningState.Normal, analyze(ratings, 47).state)
    }

    @Test
    fun threeOfLastFourLowWithLatestLowGiveContinuedLow() {
        val result = analyze(stable() + rating(24, 4) + rating(26, 4) + rating(28, 4), 29)
        assertEquals(WarningState.ContinuedLow, result.state)
    }

    // ---------- Fehlende und alte Daten ----------

    @Test
    fun missingAnswersAreNeutral() {
        // Check-ins, in denen "joy" nicht beantwortet wurde, erzeugen weder Baseline noch Warnung
        val records = (0L..26L step 2).map {
            CheckInRecord(day(it), mapOf("energy" to 7))
        }
        val result = analyzer.analyzeAll(records, listOf("energy", "joy"), day(27), zone)
        assertNull(result.categories["joy"]!!.baseline)
        assertEquals(WarningState.Normal, result.categories["joy"]!!.state)
        assertEquals(WarningState.Normal, result.categories["energy"]!!.state)
    }

    @Test
    fun outdatedLowValuesGiveNoWarning() {
        val ratings = stable() + rating(24, 4) + rating(26, 4)
        assertEquals(WarningState.Sinking, analyze(ratings, 27).state)
        assertEquals(WarningState.Normal, analyze(ratings, 60).state)   // Daten sind zu alt
    }

    @Test
    fun inputOrderDoesNotMatter() {
        val ratings = stable() + rating(24, 4) + rating(26, 4)
        val shuffled = ratings.reversed()
        assertEquals(analyze(ratings, 27), analyze(shuffled, 27))
    }

    // ---------- Erholung ----------

    @Test
    fun recoveryNeedsSeveralGoodCheckIns() {
        val low = stable() + (24L..30L step 2).map { rating(it, 4) }
        assertEquals(WarningState.ContinuedLow, analyze(low, 31).state)

        val oneGood = low + rating(32, 7)
        assertEquals(WarningState.Sinking, analyze(oneGood, 33).state)   // noch nicht Normal

        val twoGood = oneGood + rating(34, 7)
        assertEquals(WarningState.Normal, analyze(twoGood, 35).state)
    }

    // ---------- Mehrere Kategorien ----------

    private fun recordsWith(energyLow: Boolean, joyLow: Boolean): List<CheckInRecord> {
        val normal = (0L..22L step 2).map { CheckInRecord(day(it), mapOf("energy" to 7, "joy" to 7)) }
        val recent = listOf(24L, 26L).map {
            CheckInRecord(
                day(it),
                mapOf("energy" to if (energyLow) 4 else 7, "joy" to if (joyLow) 4 else 7)
            )
        }
        return normal + recent
    }

    @Test
    fun generalHintAppearsWhenTwoCategoriesAreAffected() {
        val ids = listOf("energy", "joy", "sleep")
        val both = analyzer.analyzeAll(recordsWith(energyLow = true, joyLow = true), ids, day(27), zone)
        assertEquals(WarningState.Sinking, both.categories["energy"]!!.state)
        assertEquals(WarningState.Sinking, both.categories["joy"]!!.state)
        assertEquals(WarningState.Normal, both.categories["sleep"]!!.state)
        assertTrue(both.showGeneralHint)
    }

    @Test
    fun noGeneralHintForASingleAffectedCategory() {
        val ids = listOf("energy", "joy", "sleep")
        val one = analyzer.analyzeAll(recordsWith(energyLow = true, joyLow = false), ids, day(27), zone)
        assertEquals(WarningState.Sinking, one.categories["energy"]!!.state)
        assertFalse(one.showGeneralHint)
    }
}
