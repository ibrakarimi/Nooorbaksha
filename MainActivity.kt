package com.noor.prayerwallpaper

import android.app.Activity
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*

class MainActivity : Activity() {
    private val names = listOf("فجر", "ظہر", "عصر", "مغرب", "عشاء")
    private val adhan = mutableListOf<EditText>()
    private val jamaat = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 24, 22, 24)
            setBackgroundColor(0xFFF4F0E6.toInt())
        }
        root.addView(TextView(this).apply {
            text = "NOOR  •  نور\nلداخ کے برف پوش پہاڑ"
            textSize = 25f; gravity = Gravity.CENTER; setTextColor(0xFF79551C.toInt())
        }, match(ViewGroup.LayoutParams.MATCH_PARENT, 100))

        root.addView(TextView(this).apply {
            text = "نماز کے اوقات  —  اذان اور جماعت"
            textSize = 19f; gravity = Gravity.CENTER; setTextColor(0xFF222222.toInt())
        })

        val current = PrayerTimes.get(this)
        current.forEachIndexed { i, p ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
            row.addView(TextView(this).apply { text = p.name; textSize = 20f; setTextColor(0xFF222222.toInt()) },
                LinearLayout.LayoutParams(0, 58, 1f))
            val a = EditText(this).apply { setSingleLine(); textSize = 16f; hint = "اذان"; setText(p.adhan) }
            val j = EditText(this).apply { setSingleLine(); textSize = 16f; hint = "جماعت"; setText(p.jamaat) }
            adhan.add(a); jamaat.add(j)
            row.addView(a, LinearLayout.LayoutParams(0, 58, 1f))
            row.addView(j, LinearLayout.LayoutParams(0, 58, 1f))
            root.addView(row)
        }

        root.addView(TextView(this).apply {
            text = "وقت 24-hour format میں درج کریں، مثلاً 17:33"
            textSize = 13f; setTextColor(0xFF555555.toInt())
        })
        root.addView(Button(this).apply {
            text = "اوقات محفوظ کریں"
            setOnClickListener {
                val values = mutableListOf<Prayer>()
                for (i in names.indices) {
                    val a = adhan[i].text.toString().trim()
                    val j = jamaat[i].text.toString().trim()
                    if (!PrayerTimes.validTime(a) || !PrayerTimes.validTime(j)) {
                        Toast.makeText(this@MainActivity, "وقت HH:MM کی صورت میں درج کریں", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }
                    values.add(Prayer(names[i], a, j))
                }
                PrayerTimes.save(this@MainActivity, values)
                Toast.makeText(this@MainActivity, "اوقات محفوظ ہوگئے", Toast.LENGTH_SHORT).show()
            }
        })
        root.addView(Button(this).apply {
            text = "Live Wallpaper لگائیں"
            setOnClickListener {
                val intent = Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
                    putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        ComponentName(this@MainActivity, PrayerWallpaperService::class.java))
                }
                startActivity(intent)
            }
        })
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun match(w: Int, h: Int) = LinearLayout.LayoutParams(w, h)
}
