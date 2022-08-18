package dev.olshevski.navigation.reimagined

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test

class RecreateAndClearViewModelsTest {

    @get:Rule
    val scenarioRule = ActivityScenarioRule(ComponentActivity::class.java)

    class TestViewModel : ViewModel() {

        var cleared = false
            private set

        override fun onCleared() {
            cleared = true
        }

    }

    @Test
    fun test() {
        lateinit var viewModel1: TestViewModel
        scenarioRule.scenario.onActivity {
            viewModel1 = it.viewModels<TestViewModel>().value
        }
        scenarioRule.scenario.recreateAndClearViewModels()

        lateinit var viewModel2: TestViewModel
        scenarioRule.scenario.onActivity {
            viewModel2 = it.viewModels<TestViewModel>().value
        }

        assertThat(viewModel1.cleared).isTrue()
        assertThat(viewModel1).isNotSameInstanceAs(viewModel2)
    }

}