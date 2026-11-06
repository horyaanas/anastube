# anastube (أنس تيوب) 🚀
**Clean, Modern, Ad-Free Android Media Downloader & Player built with Kotlin & Jetpack Compose**

---

## 🌟 نظرة عامة (Overview)
تم بناء تطبيق **anastube** كبديل عصري وخالٍ تماماً من الإعلانات مستوحى من أفضل ميزات ومعمارية تطبيق SnapTube، ولكن بكود أندرويد نقي 100% ومبني وفق أحدث معايير جوجل الرسمية.

### 🎯 الميزات الرئيسية المستوحاة والمطورة:
1. **الهوية البصرية الملكية (Royal Blue Theme):**
   - تصميم متكامل متوافق مع **Material 3**.
   - دعم الوضع الداكن (Dark Mode) والنهاري (Light Mode).
2. **تجربة خالية من الإعلانات بنسبة 100% (Zero Ads):**
   - تم استبعاد كافة شبكات الإعلانات المتطفلة، وحزم تتبع الإعلانات والـ Popups.
3. **نافذة اختيار الصيغ الذكية (ChooseFormat Bottom Sheet):**
   - تتيح للمستخدم فور اختيار أي مقطع أو لصق رابطه الاختيار المباشر بين:
     - **صوت فقط (Music):** MP3 160K, MP3 320K HD Audio, M4A.
     - **فيديو (Video):** 1080p Full HD, 720p HD, 360p Fast.
     - مؤشر حجم الملف التقديري لكل جودة.
4. **المتصفح المدمج مع لاقط الوسائط (In-App Browser & Media Sniffer):**
   - متصفح ويب مدمج لتصفح منصات الفيديو.
   - زر تحميل عائم ذكي (Floating Action Button) يظهر تلقائياً بمجرد رصد ملف فيديو أو صوت في الصفحة لتنزيله بضغطة زر.
5. **استقبال الروابط عبر المشاركة (System Share Sheet Integration):**
   - يدعم `android.intent.action.SEND` بحيث يمكنك من أي تطبيق خارجي (يوتيوب، المتصفح، تيك توك، تويتر) الضغط على "مشاركة" واختيار **anastube** ليفتح مباشرة نافذة خيارات التنزيل.
6. **مشغل وسائط متقدم (ExoPlayer / AndroidX Media3):**
   - تشغيل الفيديوهات بجودة عالية.
   - دعم الاستماع في الخلفية (Background Playback) حتى عند قفل الشاشة.
   - دعم وضع صورة داخل صورة (Picture-in-Picture - PiP).
7. **مدير التحميلات المتطور (Download Manager & Queue):**
   - متابعة تقدم التنزيل في الوقت الفعلي مع مؤشر السرعة (Speedometer).
   - إمكانية الإيقاف المؤقت والاستئناف والإلغاء.
   - تبويب الملفات المكتملة وتشغيلها مباشرة بدون اتصال بالإنترنت.
8. **الخزنة الخاصة والمكتبة المحلية (Vault & Offline Library):**
   - تبويب لاستعراض ملفات الفيديو والصوت المحلية وإمكانية تأمين ملفاتك برمز سري.

---

## 🛠 التقنيات المستخدمة (Tech Stack)
- **Language:** Kotlin 1.9+
- **UI Toolkit:** Jetpack Compose (Material 3)
- **Media Engine:** AndroidX Media3 / ExoPlayer 1.3.1
- **Async & Reactive:** Kotlin Coroutines & StateFlow
- **Networking:** OkHttp 4.12
- **Image Loading:** Coil Compose
- **Build System:** Gradle Kotlin DSL (kts)

---

## 📱 كيفية التجميع والتشغيل (How to Build & Run)
1. افتح المجلد `anastube` مباشرة داخل **Android Studio** (نسخة Hedgehog أو أحدث).
2. سيقوم Android Studio بمزامنة ملفات Gradle تلقائياً (`Sync Project with Gradle Files`).
3. اختر محاكي أندرويد أو جهازك الحقيقي واضغط على **Run (Shift + F10)**.
4. لتوليد ملف الـ APK النهائي للإنتاج:
   ```bash
   ./gradlew assembleRelease
   ```
   أو للنسخة التجريبية:
   ```bash
   ./gradlew assembleDebug
   ```
   سيتوفر ملف الـ APK داخل: `app/build/outputs/apk/debug/app-debug.apk`
