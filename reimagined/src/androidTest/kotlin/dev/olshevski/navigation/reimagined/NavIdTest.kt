package dev.olshevski.navigation.reimagined

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class NavIdTest {

    @Test
    fun testToString() {
        assertThat(NavId().toString().length).isEqualTo(22) // 128 bits of UUID divided by 6 bits of Base64
    }

}