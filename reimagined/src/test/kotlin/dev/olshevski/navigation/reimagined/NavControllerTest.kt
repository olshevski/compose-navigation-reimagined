package dev.olshevski.navigation.reimagined

import android.os.Parcel
import android.os.Parcelable
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.parcelize.Parcelize
import java.io.Serializable

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

    context("parcelable") {

        val parcel = Parcel.obtain()

        test("no items") {
            val navController = navController(initialBackstack = emptyList<TestDestination>())
            parcel.writeParcelable(navController, 0)

            parcel.setDataPosition(0)

            val restoredController = parcel.readParcelable<NavController<TestDestination>>(null)!!
            restoredController.backstack.entries.size shouldBe 0
            restoredController.backstack.action shouldBe NavAction.Idle
        }

        test("single item") {
            val navController = navController(TestDestination.A)
            parcel.writeParcelable(navController, 0)

            parcel.setDataPosition(0)

            val restoredController = parcel.readParcelable<NavController<TestDestination>>(null)!!
            restoredController.backstack.entries.size shouldBe 1
            restoredController.backstack.action shouldBe NavAction.Idle
        }

        test("non-default action") {
            val navController = navController(initialBackstack = emptyList<TestDestination>())
            navController.navigate(TestDestination.A)
            parcel.writeParcelable(navController, 0)

            parcel.setDataPosition(0)

            val restoredController = parcel.readParcelable<NavController<TestDestination>>(null)!!
            restoredController.backstack.entries.size shouldBe 1
            restoredController.backstack.action shouldBe NavAction.Navigate
        }

        test("parcelable destination types") {
            // just testing some key types as sanity check
            val backstackDestination = listOf(
                null,
                123456789,
                "string",
                SerializableTestClass(1234),
                ParcelableTestClass(5678)
            )
            val navController = navController(
                initialBackstack = backstackDestination
            )

            parcel.writeParcelable(navController, 0)

            parcel.setDataPosition(0)

            val restoredController = parcel.readParcelable<NavController<Any?>>(null)!!
            restoredController.backstack.entries.map { it.destination } shouldContainInOrder backstackDestination
        }

        test("instances are not duplicated") {
            val navController = navController<TestDestination>(initialBackstack = emptyList())
            val navEntryA = navEntry(TestDestination.A)
            val navEntryB = navEntry(TestDestination.B)
            val navEntryC = navEntry(TestDestination.C)
            val navEntryD = navEntry(TestDestination.D)
            navController.setNewBackstackEntries(
                entries = listOf(
                    navEntryA, // 0
                    navEntryB, // 1
                    navEntryC, // 2
                    navEntryC, // 3
                    navEntryC, // 4
                    navEntryB, // 5
                    navEntryD, // 6
                )
            )

            parcel.writeParcelable(navController, 0)

            parcel.setDataPosition(0)

            val restoredController = parcel.readParcelable<NavController<Any?>>(null)!!
            restoredController.backstack.entries.let { entries ->
                entries[1] shouldBeSameInstanceAs entries[5]
                entries[2] shouldBeSameInstanceAs entries[3]
                entries[2] shouldBeSameInstanceAs entries[4]
            }
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

private data class SerializableTestClass(val value: Int) : Serializable

@Parcelize
private data class ParcelableTestClass(val value: Int) : Parcelable