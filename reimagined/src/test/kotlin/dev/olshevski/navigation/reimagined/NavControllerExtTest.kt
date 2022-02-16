package dev.olshevski.navigation.reimagined

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

@RobolectricTest
class NavControllerExtTest : FunSpec({

    context("navigate") {

        test("once") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.navigate(TestDestination.B)
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B
            )
            navController.backstack.action shouldBe NavAction.Navigate
        }

        test("twice") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.navigate(TestDestination.B)
            navController.navigate(TestDestination.C)
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.B,
                TestDestination.C
            )
            navController.backstack.action shouldBe NavAction.Navigate
        }

        test("list of destinations") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.navigate(listOf(TestDestination.B, TestDestination.C))
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.B,
                TestDestination.C
            )
            navController.backstack.action shouldBe NavAction.Navigate
        }

        test("empty list of destinations") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.navigate(emptyList())
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A
            )
            navController.backstack.action shouldBe NavAction.Navigate
        }

    }

    context("moveToTop") {

        test("item in the middle of the backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B, TestDestination.C)
            )
            navController.moveToTop { it == TestDestination.B } shouldBe true
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.C,
                TestDestination.B
            )
            navController.backstack.action shouldBe NavAction.Navigate
        }

        test("item in the end of the backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B, TestDestination.C)
            )
            navController.moveToTop { it == TestDestination.C } shouldBe true
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.B,
                TestDestination.C
            )
            navController.backstack.action shouldBe NavAction.Navigate
        }

        test("no item in the backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B, TestDestination.C)
            )
            navController.moveToTop { it == TestDestination.D } shouldBe false
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.B,
                TestDestination.C
            )
            navController.backstack.action shouldBe NavAction.Idle
        }
    }

    context("pop") {

        test("single item in the backstack") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.pop() shouldBe true
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Pop
        }

        test("two items in the backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            navController.pop() shouldBe true
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A
            )
            navController.backstack.action shouldBe NavAction.Pop
        }

        test("no items in the backstack") {
            val navController = navController(
                initialBackstack = emptyList<Any>()
            )
            navController.pop() shouldBe false
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Idle
        }

    }

    context("popAll") {

        test("single item in the backstack") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.popAll()
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Pop
        }

        test("two items in the backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            navController.popAll()
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Pop
        }

        test("no items in the backstack") {
            val navController = navController(
                initialBackstack = emptyList<Any>()
            )
            navController.popAll()
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Pop
        }

    }

    context("popUpTo") {

        context("inclusive = false") {

            test("when no 'upTo' item in the backstack") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo { it == TestDestination.C } shouldBe false
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.B,
                )
                navController.backstack.action shouldBe NavAction.Idle
            }

            test("when 'upTo' item in the backstack is last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo { it == TestDestination.B } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.B,
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("when 'upTo' item in the backstack is not last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("match = Last") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.popUpTo() { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.A
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("match = First") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.popUpTo(match = Match.First) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

        }

        context("inclusive = true") {

            test("when no 'upTo' item in the backstack") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo(inclusive = true) { it == TestDestination.C } shouldBe false
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.B,
                )
                navController.backstack.action shouldBe NavAction.Idle
            }

            test("when 'upTo' item in the backstack is last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo(inclusive = true) { it == TestDestination.B } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("when 'upTo' item in the backstack is not last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo(inclusive = true) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldHaveSize 0
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("match = Last") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.popUpTo(
                    inclusive = true,
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("match = First") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.popUpTo(
                    inclusive = true,
                    match = Match.First,
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations.size shouldBe 0
                navController.backstack.action shouldBe NavAction.Pop
            }

        }

    }

    context("replaceLast") {

        test("single item in the backstack") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceLast(TestDestination.B) shouldBe true
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B,
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("two items in the backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            navController.replaceLast(TestDestination.C) shouldBe true
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A,
                TestDestination.C,
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("no items in the backstack") {
            val navController = navController(
                initialBackstack = emptyList<Any>()
            )
            navController.replaceLast(TestDestination.B) shouldBe false
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Idle
        }

        test("list of newDestinations") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceLast(
                listOf(TestDestination.B, TestDestination.C)
            ) shouldBe true
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B,
                TestDestination.C
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("empty list of newDestinations") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceLast(emptyList()) shouldBe true
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Replace
        }

    }

    context("replaceAll") {

        test("single item in the backstack") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceAll(TestDestination.B)
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B,
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("two items in the backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            navController.replaceAll(TestDestination.C)
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.C,
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("no items in the backstack") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.pop()
            navController.replaceAll(TestDestination.B)
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B,
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("list of newDestinations") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceAll(listOf(TestDestination.B, TestDestination.C))
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B,
                TestDestination.C
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("empty list of newDestinations") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceAll(emptyList())
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Replace
        }

    }

    context("replaceUpTo") {

        context("inclusive = false") {

            test("when no 'upTo' item in the backstack") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.D
                ) { it == TestDestination.C } shouldBe false
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.B,
                )
                navController.backstack.action shouldBe NavAction.Idle
            }

            test("when 'upTo' item in the backstack is last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.D
                ) { it == TestDestination.B } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.B,
                    TestDestination.D,
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("when 'upTo' item in the backstack is not last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.D
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.D,
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("list of newDestinations") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestinations = listOf(TestDestination.D, TestDestination.B)
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.D,
                    TestDestination.B,
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("empty list of newDestinations") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestinations = emptyList()
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("match = Last") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.C,
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.A,
                    TestDestination.C
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("match = First") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.C,
                    match = Match.First
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.C
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

        }

        context("inclusive = true") {

            test("when no 'upTo' item in the backstack") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.D,
                    inclusive = true
                ) { it == TestDestination.C } shouldBe false
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.B,
                )
                navController.backstack.action shouldBe NavAction.Idle
            }

            test("when 'upTo' item in the backstack is last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.D,
                    inclusive = true
                ) { it == TestDestination.B } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.D,
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("when 'upTo' item in the backstack is not last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.D,
                    inclusive = true
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.D,
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("list of newDestinations") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestinations = listOf(TestDestination.D, TestDestination.B),
                    inclusive = true
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.D,
                    TestDestination.B,
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("empty list of newDestinations") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.replaceUpTo(
                    newDestinations = emptyList(),
                    inclusive = true
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldHaveSize 0
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("match = Last") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.C,
                    inclusive = true,
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.C
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("match = First") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.C,
                    inclusive = true,
                    match = Match.First
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.C
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

        }

    }

})