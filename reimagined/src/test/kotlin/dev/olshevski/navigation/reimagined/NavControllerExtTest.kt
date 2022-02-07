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
            val previousEntries = navController.backstack.destinations.toList()
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

        test("with collection") {
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

        test("with empty collection") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.navigate(emptyList())
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A
            )
            navController.backstack.action shouldBe NavAction.Idle
        }

    }

    context("pop") {

        test("when single item in backstack") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.pop() shouldBe true
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Pop
        }

        test("when two items in backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            navController.pop() shouldBe true
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.A
            )
            navController.backstack.action shouldBe NavAction.Pop
        }

        test("when no items in backstack") {
            val navController = navController(
                initialBackstack = emptyList<Any>()
            )
            navController.pop() shouldBe false
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Idle
        }

    }

    context("popAll") {

        test("when single item in backstack") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.popAll() shouldBe true
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Pop
        }

        test("when two items in backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            navController.popAll() shouldBe true
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Pop
        }

        test("when no items in backstack") {
            val navController = navController(
                initialBackstack = emptyList<Any>()
            )
            navController.popAll() shouldBe false
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Idle
        }

    }

    context("popUpTo") {

        context("inclusive = false") {

            test("when no 'upTo' item in backstack") {
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

            test("when 'upTo' item in backstack is last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo { it == TestDestination.B } shouldBe false
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.B,
                )
                navController.backstack.action shouldBe NavAction.Idle
            }

            test("when 'upTo' item in backstack is not last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("upToPolicy = LastMatching") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.popUpTo(
                    upToPolicy = UpToPolicy.LastMatching,
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.A
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("upToPolicy = FirstMatching") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.popUpTo() { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

        }

        context("inclusive = true") {

            test("when no 'upTo' item in backstack") {
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

            test("when 'upTo' item in backstack is last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo(inclusive = true) { it == TestDestination.B } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("when 'upTo' item in backstack is not last") {
                val navController = navController(
                    initialBackstack = listOf(TestDestination.A, TestDestination.B)
                )
                navController.popUpTo(inclusive = true) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldHaveSize 0
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("upToPolicy = LastMatching") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.popUpTo(
                    inclusive = true,
                    upToPolicy = UpToPolicy.LastMatching,
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                )
                navController.backstack.action shouldBe NavAction.Pop
            }

            test("upToPolicy = FirstMatching") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.popUpTo(
                    inclusive = true
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations.size shouldBe 0
                navController.backstack.action shouldBe NavAction.Pop
            }

        }

    }

    context("replaceLast") {

        test("when single item in backstack") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceLast(TestDestination.B) shouldBe true
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B,
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("when two items in backstack") {
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

        test("when no items in backstack") {
            val navController = navController(
                initialBackstack = emptyList<Any>()
            )
            navController.replaceLast(TestDestination.B) shouldBe false
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Idle
        }

        test("collection") {
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

        test("empty collection") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceLast(emptyList()) shouldBe true
            navController.backstack.destinations shouldHaveSize 0
            navController.backstack.action shouldBe NavAction.Replace
        }

    }

    context("replaceAll") {

        test("single item in backstack") {
            val navController = navController(
                startDestination = TestDestination.A
            )
            navController.replaceAll(TestDestination.B)
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.B,
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("two items in backstack") {
            val navController = navController(
                initialBackstack = listOf(TestDestination.A, TestDestination.B)
            )
            navController.replaceAll(TestDestination.C)
            navController.backstack.destinations shouldContainInOrder listOf(
                TestDestination.C,
            )
            navController.backstack.action shouldBe NavAction.Replace
        }

        test("no items in backstack") {
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

        test("with collection") {
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

        test("with empty collection") {
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

            test("when no 'upTo' item in backstack") {
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

            test("when 'upTo' item in backstack is last") {
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

            test("when 'upTo' item in backstack is not last") {
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

            test("with collection") {
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

            test("with empty collection") {
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

            test("upToPolicy = LastMatching") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.C,
                    upToPolicy = UpToPolicy.LastMatching,
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.A,
                    TestDestination.C
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("upToPolicy = FirstMatching") {
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
                    TestDestination.C
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

        }

        context("inclusive = true") {

            test("when no 'upTo' item in backstack") {
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

            test("when 'upTo' item in backstack is last") {
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

            test("when 'upTo' item in backstack is not last") {
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

            test("with collection") {
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

            test("with empty collection") {
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

            test("upToPolicy = LastMatching") {
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
                    upToPolicy = UpToPolicy.LastMatching,
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.A,
                    TestDestination.C
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

            test("upToPolicy = FirstMatching") {
                val navController = navController(
                    initialBackstack = listOf(
                        TestDestination.A,
                        TestDestination.A,
                        TestDestination.B
                    )
                )
                navController.replaceUpTo(
                    newDestination = TestDestination.C,
                    inclusive = true
                ) { it == TestDestination.A } shouldBe true
                navController.backstack.destinations shouldContainInOrder listOf(
                    TestDestination.C
                )
                navController.backstack.action shouldBe NavAction.Replace
            }

        }

    }

})