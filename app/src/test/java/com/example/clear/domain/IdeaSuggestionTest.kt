package com.example.clear.domain

import com.example.clear.data.IdeaLevel
import com.example.clear.model.CheckInCategory
import org.junit.Assert.assertEquals
import org.junit.Test

class IdeaSuggestionTest {

    private fun info(average: Double) = CategoryLevelInfo(LevelCalculator.levelOf(average), average, 3)

    @Test fun emptyNeedsSuggestsSinking() {
        val levels = mapOf(CheckInCategory.Energy.id to info(9.0))
        assertEquals(IdeaLevel.Sinking, IdeaSuggestion.suggest(emptySet(), levels))
    }

    @Test fun noDataSuggestsSinking() {
        val levels = mapOf<String, CategoryLevelInfo?>(CheckInCategory.Energy.id to null)
        assertEquals(IdeaLevel.Sinking, IdeaSuggestion.suggest(setOf(CheckInCategory.Energy), levels))
        assertEquals(IdeaLevel.Sinking, IdeaSuggestion.suggest(setOf(CheckInCategory.Energy), null))
    }

    @Test fun boundaries() {
        val needs = setOf(CheckInCategory.Energy)
        fun at(value: Double) = IdeaSuggestion.suggest(needs, mapOf(CheckInCategory.Energy.id to info(value)))
        assertEquals(IdeaLevel.Normal, at(7.0))
        assertEquals(IdeaLevel.Normal, at(10.0))
        assertEquals(IdeaLevel.Sinking, at(6.9))
        assertEquals(IdeaLevel.Sinking, at(4.0))
        assertEquals(IdeaLevel.Severe, at(3.9))
    }

    @Test fun averagesSeveralNeeds() {
        val needs = setOf(CheckInCategory.Energy, CheckInCategory.Calm)
        val levels = mapOf(CheckInCategory.Energy.id to info(8.0), CheckInCategory.Calm.id to info(5.0))
        // (8 + 5) / 2 = 6,5 -> wenn es sinkt
        assertEquals(IdeaLevel.Sinking, IdeaSuggestion.suggest(needs, levels))
    }

    @Test fun skipsNeedsWithoutData() {
        val needs = setOf(CheckInCategory.Energy, CheckInCategory.Clarity)
        val levels = mapOf(CheckInCategory.Energy.id to info(8.0), CheckInCategory.Clarity.id to null)
        assertEquals(IdeaLevel.Normal, IdeaSuggestion.suggest(needs, levels))
    }
}
