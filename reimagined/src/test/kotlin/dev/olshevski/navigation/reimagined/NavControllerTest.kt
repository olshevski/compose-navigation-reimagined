package dev.olshevski.navigation.reimagined

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@RobolectricTest
class NavControllerTest : FunSpec({

    context("modifyBackstack") {

        test("set empty list") {
            val navController = navController(TestDestination.A)
            navController.setNewBackstackEntries(
                entries = emptyList(),
                action = NavAction.Pop
            )
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Pop
        }

        test("set new list") {
            val navController = navController(TestDestination.A)
            navController.setNewBackstackEntries(
                entries = listOf(navEntry(TestDestination.B)),
                action = NavAction.Navigate
            )
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B,
            )
            navController.backstack.action shouldBe NavAction.Navigate
        }

        test("modify existing list") {
            val navController = navController(TestDestination.A)
            navController.setNewBackstackEntries(
                entries = navController.backstack.entries + navEntry(TestDestination.B),
                action = NavAction.Navigate
            )
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.B,
            )
            navController.backstack.action shouldBe NavAction.Navigate
        }

    }

    context("onBackstackChange") {

        test("empty list") {
            val navController = navController(TestDestination.A)
            val onBackstackChangeCallback = OnBackstackChangeCallback<TestDestination>()
            navController.onBackstackChange = onBackstackChangeCallback
            navController.setNewBackstackEntries(
                entries = emptyList(),
                action = NavAction.Pop
            )
            onBackstackChangeCallback.isCalled shouldBe true
            onBackstackChangeCallback.backstack shouldNotBe null
            onBackstackChangeCallback.backstack!!.entries shouldHaveSize 0
            onBackstackChangeCallback.backstack!!.action shouldBe NavAction.Pop
        }

        test("new entry") {
            val navController = navController(TestDestination.A)
            val onBackstackChangeCallback = OnBackstackChangeCallback<TestDestination>()
            navController.onBackstackChange = onBackstackChangeCallback
            navController.setNewBackstackEntries(
                entries = navController.backstack.entries + navEntry(TestDestination.B),
                action = NavAction.Navigate
            )
            onBackstackChangeCallback.isCalled shouldBe true
            onBackstackChangeCallback.backstack shouldNotBe null
            onBackstackChangeCallback.backstack!!.entries.map { it.destination } shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.B,
            )
            onBackstackChangeCallback.backstack!!.action shouldBe NavAction.Navigate
        }

    }

})

private class OnBackstackChangeCallback<T> : (NavBackstack<T>) -> Unit {

    var isCalled = false
        private set

    var backstack: NavBackstack<T>? = null
        private set

    override fun invoke(backstack: NavBackstack<T>) {
        isCalled = true
        this.backstack = backstack
    }

}

