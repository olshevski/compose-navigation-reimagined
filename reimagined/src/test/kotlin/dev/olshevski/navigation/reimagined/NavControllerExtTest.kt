package dev.olshevski.navigation.reimagined

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@Suppress("ClassName")
class NavControllerExtTest {

    private enum class TestDestination {
        A, B, C, D
    }

    @Nested
    inner class navigate {

        @Test
        fun once() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.navigate(TestDestination.B)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(TestDestination.A, TestDestination.B)
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
        }

        @Test
        fun twice() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.navigate(TestDestination.B)
            navController.navigate(TestDestination.C)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.A,
                    TestDestination.B,
                    TestDestination.C
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
        }

        @Test
        fun `list of destinations`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.navigate(listOf(TestDestination.B, TestDestination.C))
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.A,
                    TestDestination.B,
                    TestDestination.C
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
        }

        @Test
        fun `empty list of destinations`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.navigate(emptyList())
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.A
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
        }

    }

    @Nested
    inner class moveToTop {

        @Test
        fun `item in the middle of the backstack`() {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B, TestDestination.C)
            )
            assertThat(navController.moveToTop { it == TestDestination.B }).isEqualTo(true)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.A,
                    TestDestination.C,
                    TestDestination.B
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
        }

        @Test
        fun `item in the end of the backstack`() {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B, TestDestination.C)
            )
            assertThat(navController.moveToTop { it == TestDestination.C }).isEqualTo(true)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.A,
                    TestDestination.B,
                    TestDestination.C
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Navigate)
        }

        @Test
        fun `no item in the backstack`() {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B, TestDestination.C)
            )
            assertThat(navController.moveToTop { it == TestDestination.D }).isEqualTo(false)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.A,
                    TestDestination.B,
                    TestDestination.C
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Idle)
        }
    }

    @Nested
    inner class pop {

        @Test
        fun `single item in the backstack`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            assertThat(navController.pop()).isEqualTo(true)
            assertThat(navController.backstack.destinations).hasSize(0)
            assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
        }

        @Test
        fun `two items in the backstack`() {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            assertThat(navController.pop()).isEqualTo(true)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.A
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
        }

        @Test
        fun `no items in the backstack`() {
            val navController = navController(
                initialBackstack = emptyList<Any>()
            )
            assertThat(navController.pop()).isEqualTo(false)
            assertThat(navController.backstack.destinations).hasSize(0)
            assertThat(navController.backstack.action).isEqualTo(NavAction.Idle)
        }

    }

    @Nested
    inner class popAll {

        @Test
        fun `single item in the backstack`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.popAll()
            assertThat(navController.backstack.destinations).hasSize(0)
            assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
        }

        @Test
        fun `two items in the backstack`() {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            navController.popAll()
            assertThat(navController.backstack.destinations).hasSize(0)
            assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
        }

        @Test
        fun `no items in the backstack`() {
            val navController = navController(
                initialBackstack = emptyList<Any>()
            )
            navController.popAll()
            assertThat(navController.backstack.destinations).hasSize(0)
            assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
        }

    }

    @Nested
    inner class popUpTo {

        @Nested
        inner class `inclusive = false` {

            @Test
            fun `when no 'upTo' item in the backstack`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.popUpTo { it == TestDestination.C }).isEqualTo(false)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.B,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Idle)
            }

            @Test
            fun `when 'upTo' item in the backstack is last`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.popUpTo { it == TestDestination.B }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.B,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
            }

            @Test
            fun `when 'upTo' item in the backstack is not last`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.popUpTo { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
            }

            @Test
            fun `match = Last`() {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                assertThat(navController.popUpTo { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.A
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
            }

            @Test
            fun `match = First`() {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                assertThat(navController.popUpTo(match = Match.First) { it == TestDestination.A })
                    .isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
            }

        }

        @Nested
        inner class `inclusive = true` {

            @Test
            fun `when no 'upTo' item in the backstack`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.popUpTo(inclusive = true) { it == TestDestination.C })
                    .isEqualTo(false)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.B,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Idle)
            }

            @Test
            fun `when 'upTo' item in the backstack is last`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.popUpTo(inclusive = true) { it == TestDestination.B })
                    .isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
            }

            @Test
            fun `when 'upTo' item in the backstack is not last`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.popUpTo(inclusive = true) { it == TestDestination.A })
                    .isEqualTo(true)
                assertThat(navController.backstack.destinations).hasSize(0)
                assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
            }

            @Test
            fun `match = Last`() {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                assertThat(navController.popUpTo(
                    inclusive = true,
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
            }

            @Test
            fun `match = First`() {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                assertThat(navController.popUpTo(
                    inclusive = true,
                    match = Match.First,
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations.size).isEqualTo(0)
                assertThat(navController.backstack.action).isEqualTo(NavAction.Pop)
            }

        }

    }

    @Nested
    inner class replaceLast {

        @Test
        fun `single item in the backstack`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            assertThat(navController.replaceLast(TestDestination.B)).isEqualTo(true)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.B,
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
        }

        @Test
        fun `two items in the backstack`() {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            assertThat(navController.replaceLast(TestDestination.C)).isEqualTo(true)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.A,
                    TestDestination.C,
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
        }

        @Test
        fun `no items in the backstack`() {
            val navController = navController(
                initialBackstack = emptyList<Any>()
            )
            assertThat(navController.replaceLast(TestDestination.B)).isEqualTo(false)
            assertThat(navController.backstack.destinations).hasSize(0)
            assertThat(navController.backstack.action).isEqualTo(NavAction.Idle)
        }

        @Test
        fun `list of newDestinations`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            assertThat(
                navController.replaceLast(
                    listOf(TestDestination.B, TestDestination.C)
                )
            ).isEqualTo(true)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.B,
                    TestDestination.C
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
        }

        @Test
        fun `empty list of newDestinations`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            assertThat(navController.replaceLast(emptyList())).isEqualTo(true)
            assertThat(navController.backstack.destinations).hasSize(0)
            assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
        }

    }

    @Nested
    inner class replaceAll {

        @Test
        fun `single item in the backstack`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceAll(TestDestination.B)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.B,
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
        }

        @Test
        fun `two items in the backstack`() {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            navController.replaceAll(TestDestination.C)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.C,
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
        }

        @Test
        fun `no items in the backstack`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.pop()
            navController.replaceAll(TestDestination.B)
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.B,
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
        }

        @Test
        fun `list of newDestinations`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceAll(listOf(TestDestination.B, TestDestination.C))
            assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                listOf(
                    TestDestination.B,
                    TestDestination.C
                )
            )
            assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
        }

        @Test
        fun `empty list of newDestinations`() {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceAll(emptyList())
            assertThat(navController.backstack.destinations).hasSize(0)
            assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
        }

    }

    @Nested
    inner class replaceUpTo {

        @Nested
        inner class `inclusive = false` {

            @Test
            fun `when no 'upTo' item in the backstack`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.D
                ) { it == TestDestination.C }).isEqualTo(false)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.B,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Idle)
            }

            @Test
            fun `when 'upTo' item in the backstack is last`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.D
                ) { it == TestDestination.B }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.B,
                        TestDestination.D,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `when 'upTo' item in the backstack is not last`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.D
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.D,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `list of newDestinations`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestinations = listOf(TestDestination.D, TestDestination.B)
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.D,
                        TestDestination.B,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `empty list of newDestinations`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestinations = emptyList()
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `match = Last`() {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.C,
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.C
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `match = First`() {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.C,
                    match = Match.First
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.C
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

        }

        @Nested
        inner class `inclusive = true` {

            @Test
            fun `when no 'upTo' item in the backstack`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.D,
                    inclusive = true
                ) { it == TestDestination.C }).isEqualTo(false)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.B,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Idle)
            }

            @Test
            fun `when 'upTo' item in the backstack is last`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.D,
                    inclusive = true
                ) { it == TestDestination.B }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.D,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `when 'upTo' item in the backstack is not last`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.D,
                    inclusive = true
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.D,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `list of newDestinations`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestinations = listOf(TestDestination.D, TestDestination.B),
                    inclusive = true
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.D,
                        TestDestination.B,
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `empty list of newDestinations`() {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                assertThat(navController.replaceUpTo(
                    newDestinations = emptyList(),
                    inclusive = true
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).hasSize(0)
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `match = Last`() {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.C,
                    inclusive = true,
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.A,
                        TestDestination.C
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

            @Test
            fun `match = First`() {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                assertThat(navController.replaceUpTo(
                    newDestination = TestDestination.C,
                    inclusive = true,
                    match = Match.First
                ) { it == TestDestination.A }).isEqualTo(true)
                assertThat(navController.backstack.destinations).containsExactlyElementsIn(
                    listOf(
                        TestDestination.C
                    )
                )
                assertThat(navController.backstack.action).isEqualTo(NavAction.Replace)
            }

        }

    }

}