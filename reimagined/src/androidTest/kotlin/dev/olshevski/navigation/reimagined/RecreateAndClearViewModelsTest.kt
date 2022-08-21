package dev.olshevski.navigation.reimagined

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.ViewModel
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class RecreateAndClearViewModelsTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    class TestViewModel : ViewModel() {

        var cleared = false
            private set

        override fun onCleared() {
            cleared = true
        }

    }

    @Test
    fun test() {
        val viewModel1 = composeRule.activity.viewModels<TestViewModel>().value
        composeRule.recreateActivityAndClearViewModels()

        val viewModel2 = composeRule.activity.viewModels<TestViewModel>().value

        assertThat(viewModel1.cleared).isTrue()
        assertThat(viewModel1).isNotSameInstanceAs(viewModel2)
    }

}