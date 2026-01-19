package com.vikas.androidaudioplayer.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatterTest {

    @Test
    fun `formatDuration returns 00 00 for zero milliseconds`() {
        val result = Formatter.formatDuration(0L)
        assertEquals("00:00", result)
    }

    @Test
    fun `formatDuration formats seconds correctly`() {
        // 30 seconds = 30000 ms
        val result = Formatter.formatDuration(30_000L)
        assertEquals("00:30", result)
    }

    @Test
    fun `formatDuration formats minutes and seconds correctly`() {
        // 3 minutes 45 seconds = 225000 ms
        val result = Formatter.formatDuration(225_000L)
        assertEquals("03:45", result)
    }

    @Test
    fun `formatDuration formats single digit seconds with leading zero`() {
        // 1 minute 5 seconds = 65000 ms
        val result = Formatter.formatDuration(65_000L)
        assertEquals("01:05", result)
    }

    @Test
    fun `formatDuration handles hour plus durations`() {
        // 65 minutes = 3900000 ms (shows 65:00 since format uses total minutes)
        val result = Formatter.formatDuration(3_900_000L)
        assertEquals("65:00", result)
    }

    @Test
    fun `formatDuration handles typical song duration`() {
        // 4 minutes 32 seconds = 272000 ms
        val result = Formatter.formatDuration(272_000L)
        assertEquals("04:32", result)
    }
}
