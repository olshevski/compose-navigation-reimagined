package dev.olshevski.navigation.reimagined

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@RobolectricTest
class NavIdTest : FunSpec({

    test("toString") {
        NavId().toString().length shouldBe 22 // 128 bits of UUID divided by 6 bits of Base64
    }

})