package com.noor.prayerwallpaper

import android.graphics.*
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import java.text.SimpleDateFormat
import java.util.*

class PrayerWallpaperService : WallpaperService() {
    override fun onCreateEngine(): Engine = PrayerEngine()

    inner class PrayerEngine : Engine() {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val handler = android.os.Handler(mainLooper)
        private var visible = false
        private val drawTask = object : Runnable {
            override fun run() {
                drawFrame()
                if (visible) handler.postDelayed(this, 60_000)
            }
        }

        override fun onVisibilityChanged(isVisible: Boolean) {
            visible = isVisible
            handler.removeCallbacks(drawTask)
            if (visible) handler.post(drawTask)
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            drawFrame()
        }

        override fun onDestroy() {
            visible = false
            handler.removeCallbacks(drawTask)
            super.onDestroy()
        }

        private fun drawFrame() {
            val holder = surfaceHolder
            var c: Canvas? = null
            try {
                c = holder.lockCanvas()
                if (c != null) render(c)
            } catch (_: Exception) {
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c)
            }
        }

        private fun render(c: Canvas) {
            val w = c.width.toFloat(); val h = c.height.toFloat()
            c.drawColor(Color.rgb(13, 28, 48))
            paint.shader = LinearGradient(0f, 0f, 0f, h, Color.rgb(14, 31, 55), Color.rgb(204, 211, 213), Shader.TileMode.CLAMP)
            c.drawRect(0f, 0f, w, h, paint); paint.shader = null

            // Moon and stars
            paint.color = Color.rgb(255, 239, 190)
            c.drawCircle(w * .78f, h * .12f, w * .045f, paint)
            paint.color = Color.WHITE
            for (i in 0..22) c.drawCircle((i * 73f % w), (i * 47f % (h * .32f)), 1.8f, paint)

            // Layered Ladakhi mountain silhouettes with snow caps
            mountain(c, w, h, floatArrayOf(0f,.45f,.23f,.29f,.40f,.10f,.55f,.42f,.72f,.18f,1f,.43f), Color.rgb(46,65,81))
            mountain(c, w, h, floatArrayOf(0f,.58f,.17f,.32f,.34f,.47f,.55f,.24f,.77f,.50f,1f,.34f), Color.rgb(83,102,115))
            mountain(c, w, h, floatArrayOf(0f,.67f,.20f,.47f,.38f,.60f,.62f,.39f,.82f,.62f,1f,.49f), Color.rgb(218,224,224))
            paint.color = Color.argb(210,255,255,255)
            val snow = Path().apply {
                moveTo(w*.08f,h*.57f); lineTo(w*.17f,h*.32f); lineTo(w*.22f,h*.43f); lineTo(w*.27f,h*.36f); lineTo(w*.34f,h*.55f); close()
            }
            c.drawPath(snow, paint)
            val snow2 = Path().apply {
                moveTo(w*.48f,h*.52f); lineTo(w*.55f,h*.24f); lineTo(w*.62f,h*.39f); lineTo(w*.69f,h*.31f); lineTo(w*.78f,h*.55f); close()
            }
            c.drawPath(snow2, paint)

            // Foreground valley
            paint.color = Color.rgb(31, 48, 57)
            c.drawRect(0f,h*.66f,w,h,paint)

            // Elegant gold frame and calligraphy
            paint.style = Paint.Style.STROKE; paint.strokeWidth = 2.2f; paint.color = Color.rgb(218,180,91)
            c.drawRoundRect(w*.055f,h*.055f,w*.945f,h*.945f,22f,22f,paint)
            paint.style = Paint.Style.FILL
            centered(c, "بِسْمِ اللّٰهِ الرَّحْمٰنِ الرَّحِيْمِ", w/2, h*.105f, w*.052f, Color.rgb(255,226,157), true)
            centered(c, "أَوْقَاتُ الصَّلَاة", w/2, h*.175f, w*.075f, Color.rgb(239,198,105), true)
            centered(c, "لداخ کے برف پوش پہاڑ", w/2, h*.215f, w*.035f, Color.WHITE, false)

            val prayers = PrayerTimes.get(this@PrayerWallpaperService)
            val now = Calendar.getInstance()
            val nowMin = now.get(Calendar.HOUR_OF_DAY)*60 + now.get(Calendar.MINUTE)
            val next = prayers.mapIndexedNotNull { i,p ->
                val mins = parse(p.adhan)
                if (mins > nowMin) Triple(i,p,mins-nowMin) else null
            }.firstOrNull()
            val nextText = if (next != null) "اگلی نماز: ${next.second.name}  •  ${duration(next.third)}" else {
                val p = prayers.first()
                "اگلی نماز: ${p.name}  •  ${duration(24*60-nowMin+parse(p.adhan))}"
            }
            centered(c, nextText, w/2, h*.275f, w*.041f, Color.rgb(255,235,190), true)

            val top = h*.34f
            val rowH = h*.083f
            centered(c, "نماز", w*.24f, top, w*.038f, Color.rgb(255,222,151), true)
            centered(c, "اذان", w*.55f, top, w*.038f, Color.rgb(255,222,151), true)
            centered(c, "جماعت", w*.80f, top, w*.038f, Color.rgb(255,222,151), true)
            prayers.forEachIndexed { i,p ->
                val y = top + (i+1)*rowH
                val active = next?.first == i
                paint.color = if (active) Color.argb(95,218,180,91) else Color.argb(55,255,255,255)
                c.drawRoundRect(w*.10f,y-rowH*.66f,w*.90f,y+rowH*.20f,12f,12f,paint)
                centered(c,p.name,w*.24f,y,w*.050f,Color.WHITE,true)
                centered(c,p.adhan,w*.55f,y,w*.047f,Color.WHITE,false)
                centered(c,p.jamaat,w*.80f,y,w*.047f,Color.WHITE,false)
            }
            centered(c, SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date()),
                w/2,h*.86f,w*.032f,Color.rgb(245,225,185),false)
            centered(c, "NOOR  •  نور", w/2,h*.91f,w*.045f,Color.rgb(239,198,105),true)
        }

        private fun parse(s: String): Int = try {
            val a=s.split(":"); a[0].toInt()*60+a[1].toInt()
        } catch (_: Exception) { 0 }

        private fun duration(m: Int): String = "%02d:%02d".format(m/60,m%60)

        private fun centered(c: Canvas, text: String, x: Float, y: Float, size: Float, color: Int, bold: Boolean) {
            paint.shader = null; paint.color=color; paint.textSize=size
            paint.typeface = if (bold) Typeface.create("sans-serif",Typeface.BOLD) else Typeface.create("sans-serif",Typeface.NORMAL)
            paint.textAlign=Paint.Align.CENTER
            c.drawText(text,x,y,paint)
        }

        private fun mountain(c: Canvas,w:Float,h:Float,points:FloatArray,color:Int) {
            val p=Path(); p.moveTo(0f,h)
            var i=0
            while(i<points.size) { p.lineTo(points[i]*w,points[i+1]*h); i+=2 }
            p.lineTo(w,h); p.close()
            paint.color=color; c.drawPath(p,paint)
        }
    }
}
