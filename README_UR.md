# NOOR – Prayer Wallpaper (Android)

یہ ایک ابتدائی Android Studio project ہے۔ یہ Live Wallpaper کے طور پر لداخ کے برف پوش پہاڑ، سنہری اسلامی عنوان، اذان اور جماعت کے اوقات اور اگلی نماز کا countdown دکھاتا ہے۔

## تصویر سے درج کیے گئے ابتدائی اوقات
| نماز | اذان | جماعت |
|---|---:|---:|
| فجر | 05:28 | 05:58 |
| ظہر | 13:00 | 13:15 |
| عصر | 15:45 | 16:00 |
| مغرب | 17:33 | 17:37 |
| عشاء | 18:35 | 18:42 |

**اہم:** یہ اوقات صرف آپ کی بھیجی ہوئی تصویر سے ابتدائی طور پر درج کیے گئے ہیں۔ انہیں مستقل یا روزانہ بدلنے والا سرکاری/مسجد کا ٹائم ٹیبل نہ سمجھیں۔ مسجد کے نئے اوقات ملنے پر ایپ میں خود تبدیل کریں۔

## APK بنانے کا طریقہ
1. کمپیوٹر پر Android Studio انسٹال کریں۔
2. Android Studio میں `Open` منتخب کرکے اس فولڈر کو کھولیں۔
3. Gradle Sync مکمل ہونے دیں۔
4. `Build > Build Bundle(s) / APK(s) > Build APK(s)` منتخب کریں۔
5. بننے والی APK فون میں منتقل کرکے انسٹال کریں۔
6. NOOR کھولیں، ضرورت ہو تو اوقات درست کریں، پھر **Live Wallpaper لگائیں** دبائیں اور Wallpaper Preview میں Apply کریں۔

## وقت تبدیل کرنا
ایپ کھول کر ہر نماز کے اذان اور جماعت کے خانے میں 24-hour وقت `HH:MM` کی صورت میں لکھیں، مثلاً شام 5:33 کے لیے `17:33`، پھر محفوظ کریں۔

## نوٹ
یہ سورس پروجیکٹ ہے، پہلے سے compiled APK نہیں۔ اس ماحول میں Android SDK کے ذریعے APK compile نہیں کی گئی۔ فون کے Android ورژن/لانچر کے مطابق Live Wallpaper کی دستیابی مختلف ہو سکتی ہے۔


## GitHub se APK banana (Gradle Wrapper ke baghair)
Is project mein `gradlew` wrapper file shamil nahin hai. GitHub Actions workflow ab Gradle 8.9 khud install karke APK banata hai; isliye `./gradlew` command ki zaroorat nahin.

1. ZIP extract karke iske andar ki tamam files GitHub repository ke **root** mein upload karein (ZIP ko ZIP ki tarah repository mein upload na karein).
2. Repository mein `.github/workflows/build-apk.yml` file bhi honi chahiye.
3. `Actions` tab kholen. `Build NOOR APK` workflow run hoga (ya **Run workflow** dabayein).
4. Successful build ke baad workflow run ke neeche **Artifacts** mein `NOOR-debug-APK` download karein.
5. ZIP extract karke `app-debug.apk` install karein.

Agar repository ka default branch `main` nahin hai, to workflow file mein `branches: [ "main" ]` ko apne branch ke naam se badlein; ya Actions tab se workflow manually run karein.
