package dev.olshevski.navigation.reimagined

import android.os.Parcel
import android.os.Parcelable
import com.google.common.truth.Truth.assertThat
import kotlinx.parcelize.Parcelize
import org.junit.Test
import java.io.Serializable

class NavControllerParcelableTest {

    private enum class TestDestination {
        A, B, C, D
    }

    private data class SerializableTestClass(val value: Int) : Serializable

    @Parcelize
    private data class ParcelableTestClass(val value: Int) : Parcelable

    private val parcel = Parcel.obtain()

    @Test
    fun noItems() {
        val navController = navController(initialBackstack = emptyList<TestDestination>())
        navController.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        val restoredController = NavController.createFromParcel(parcel)
        assertThat(restoredController.backstack.entries.size).isEqualTo(0)
        assertThat(restoredController.backstack.action).isEqualTo(NavAction.Idle)
    }

    @Test
    fun singleItem() {
        val navController = navController(TestDestination.A)
        navController.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        val restoredController = NavController.createFromParcel(parcel)
        assertThat(restoredController.backstack.entries.size).isEqualTo(1)
        assertThat(restoredController.backstack.action).isEqualTo(NavAction.Idle)
    }

    @Test
    fun nonDefaultAction() {
        val navController = navController(initialBackstack = emptyList<TestDestination>())
        navController.navigate(TestDestination.A)
        navController.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        val restoredController = NavController.createFromParcel(parcel)
        assertThat(restoredController.backstack.entries.size).isEqualTo(1)
        assertThat(restoredController.backstack.action).isEqualTo(NavAction.Navigate)
    }

    @Test
    fun parcelableDestinationTypes() {
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

        navController.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        val restoredController = NavController.createFromParcel(parcel)
        assertThat(restoredController.backstack.entries.map { it.destination })
            .containsExactlyElementsIn(backstackDestination)
    }

    @Test
    fun instancesAreNotDuplicated() {
        val navController = navController<TestDestination>(initialBackstack = emptyList())
        val navEntryA = navEntry(TestDestination.A)
        val navEntryB = navEntry(TestDestination.B)
        val navEntryC = navEntry(TestDestination.C)
        val navEntryD = navEntry(TestDestination.D)
        navController.setNewBackstack(
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

        navController.writeToParcel(parcel, 0)

        parcel.setDataPosition(0)

        val restoredController = NavController.createFromParcel(parcel)
        restoredController.backstack.entries.let { entries ->
            assertThat(entries[1]).isSameInstanceAs(entries[5])
            assertThat(entries[2]).isSameInstanceAs(entries[3])
            assertThat(entries[2]).isSameInstanceAs(entries[4])
        }
    }

}

