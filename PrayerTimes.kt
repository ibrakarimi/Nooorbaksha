package com.noor.prayerwallpaper

import android.content.Context

data class Prayer(val name: String, val adhan: String, val jamaat: String)

object PrayerTimes {
    private val defaults = listOf(
        Prayer("فجر", "05:28", "05:58"),
        Prayer("ظہر", "13:00", "13:15"),
        Prayer("عصر", "15:45", "16:00"),
        Prayer("مغرب", "17:33", "17:37"),
        Prayer("عشاء", "18:35", "18:42")
    )

    fun get(context: Context): List<Prayer> {
        val p = context.getSharedPreferences("noor_times", Context.MODE_PRIVATE)
        return defaults.mapIndexed { i, d ->
            Prayer(d.name, p.getString("adhan_$i", d.adhan) ?: d.adhan,
                p.getString("jamaat_$i", d.jamaat) ?: d.jamaat)
        }
    }

    fun save(context: Context, values: List<Prayer>) {
        val e = context.getSharedPreferences("noor_times", Context.MODE_PRIVATE).edit()
        values.forEachIndexed { i, p ->
            e.putString("adhan_$i", p.adhan)
            e.putString("jamaat_$i", p.jamaat)
        }
        e.apply()
    }

    fun validTime(value: String): Boolean =
        Regex("^([01]?[0-9]|2[0-3]):[0-5][0-9]$").matches(value.trim())
}
