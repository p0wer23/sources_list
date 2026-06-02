package com.example.sourceslist

import com.example.sourceslist.ui.common.preferredPackageName
import com.example.sourceslist.ui.common.toAppPreferredUrl
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun substack_post_urls_are_rewritten_for_the_app() {
        val original =
            "https://example.substack.com/p/hello-world?utm_source=test"

        assertEquals(
            "https://open.substack.com/pub/example/p/hello-world?utm_source=test",
            original.toAppPreferredUrl()
        )
    }

    @Test
    fun substack_urls_target_the_substack_package() {
        val url = "https://example.substack.com/archive"

        assertEquals("com.substack.app", url.preferredPackageName())
    }

    @Test
    fun non_substack_urls_are_left_unchanged() {
        val original = "https://example.com/path"

        assertEquals(original, original.toAppPreferredUrl())
    }
}
