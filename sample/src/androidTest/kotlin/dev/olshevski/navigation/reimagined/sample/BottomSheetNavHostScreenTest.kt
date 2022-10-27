package dev.olshevski.navigation.reimagined.sample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.olshevski.navigation.testutils.getString
import org.junit.Before
import org.junit.Rule
import org.junit.Test

private class BottomSheetNavHostScreenScope(composeRule: MainActivityComposeRule) :
    DemoSelectionScreenScope(composeRule) {

    fun assertBottomSheetNavHostScreenIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__demo_screen_title))
            .assertIsDisplayed()
    }

    fun assertSheetsDoNotExist() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__first_sheet_title))
            .assertDoesNotExist()
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__second_sheet_title))
            .assertDoesNotExist()
    }

    fun assertFirstSheetIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__first_sheet_title))
            .assertIsDisplayed()
    }

    fun assertSecondSheetIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__second_sheet_title))
            .assertIsDisplayed()
    }

    fun performOpenFirstSheetButtonClick() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__open_first_sheet_button))
            .performClick()
    }

    fun performOpenSecondSheetButtonClick() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__open_second_sheet_button))
            .performClick()
    }

    fun assertExpandSheetButtonIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__expand_sheet_button))
            .assertIsDisplayed()
    }

    fun assertHalfExpandSheetButtonIsDisplayed() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__half_expand_sheet_button))
            .assertIsDisplayed()
    }

    fun performExpandSheetButtonClick() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__expand_sheet_button))
            .performClick()
    }

    fun performHalfExpandSheetButtonClick() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__half_expand_sheet_button))
            .performClick()
    }

    fun performCloseSheetButtonClick() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__close_sheet_button))
            .performClick()
    }

    fun performCloseAllSheetsButtonClick() {
        composeRule.onNodeWithText(getString(R.string.bottom_sheet_nav_host__close_all_sheets_button))
            .performClick()
    }

}

private fun MainActivityComposeRule.bottomSheetNavHostScreenScope(block: BottomSheetNavHostScreenScope.() -> Unit) =
    BottomSheetNavHostScreenScope(this).block()

class BottomSheetNavHostScreenTest {

    @get:Rule
    val composeRule = createMainActivityComposeRule()

    @Before
    fun before() = composeRule.bottomSheetNavHostScreenScope {
        performBottomSheetNavHostButtonClick()
        assertBottomSheetNavHostScreenIsDisplayed()
        assertSheetsDoNotExist()
    }

    private fun generalFlow(middleBlock: BottomSheetNavHostScreenScope.() -> Unit) =
        composeRule.bottomSheetNavHostScreenScope {
            performOpenFirstSheetButtonClick()
            assertFirstSheetIsDisplayed()
            performOpenSecondSheetButtonClick()
            assertSecondSheetIsDisplayed()

            middleBlock()
        }

    @Test
    fun sheetAreOpenedAndClosed() = composeRule.bottomSheetNavHostScreenScope {
        performOpenFirstSheetButtonClick()
        assertFirstSheetIsDisplayed()
        performOpenSecondSheetButtonClick()
        assertSecondSheetIsDisplayed()
        pressBack()
        assertFirstSheetIsDisplayed()
        pressBack()
        assertSheetsDoNotExist()
    }

    @Test
    fun sheetIsExpandable() = composeRule.bottomSheetNavHostScreenScope {
        performOpenFirstSheetButtonClick()
        performOpenSecondSheetButtonClick()
        assertExpandSheetButtonIsDisplayed()
        performExpandSheetButtonClick()
        assertHalfExpandSheetButtonIsDisplayed()
        performHalfExpandSheetButtonClick()
        assertExpandSheetButtonIsDisplayed()
    }

    @Test
    fun sheetExpandStateIsSaved() = composeRule.bottomSheetNavHostScreenScope {
        performOpenFirstSheetButtonClick()
        performOpenSecondSheetButtonClick()
        assertExpandSheetButtonIsDisplayed()
        performExpandSheetButtonClick()

        assertHalfExpandSheetButtonIsDisplayed()
        recreateActivity()
        assertHalfExpandSheetButtonIsDisplayed()

        performHalfExpandSheetButtonClick()
        assertExpandSheetButtonIsDisplayed()
        recreateActivity()
        assertExpandSheetButtonIsDisplayed()
    }

    @Test
    fun sheetIsClosed() = composeRule.bottomSheetNavHostScreenScope {
        performOpenFirstSheetButtonClick()
        performOpenSecondSheetButtonClick()
        performCloseSheetButtonClick()
        assertFirstSheetIsDisplayed()
    }

    @Test
    fun allSheetsAreClosed() = composeRule.bottomSheetNavHostScreenScope {
        performOpenFirstSheetButtonClick()
        performOpenSecondSheetButtonClick()
        performExpandSheetButtonClick()
        performCloseAllSheetsButtonClick()
        assertSheetsDoNotExist()
    }

}