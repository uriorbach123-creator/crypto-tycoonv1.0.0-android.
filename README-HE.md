# Crypto Tycoon — Android

הפרויקט הופך את משחק ה-HTML לאפליקציית Android באמצעות WebView, ומחבר את כפתור הפרסומת לפרסומת מתגמלת של Google AdMob.

## מה כלול
- משחק המקור בתוך `app/src/main/assets/index.html`.
- פרויקט Android Gradle מלא.
- Google Mobile Ads **Next-Gen SDK** (לא ה-SDK הישן).
- פרסומת מתגמלת: בונוס ×2 לדקה מתקבל רק כאשר AdMob מאשר שהמשתמש קיבל תגמול.
- טיפול בכפתור Back לסגירת החלוניות במשחק.
- GitHub Actions שבונה APK אוטומטית בכל Push ל-main/master וגם מאפשר הפעלה ידנית.

## בדיקה ראשונית
הפרויקט מוגדר כרגע עם מזהי הבדיקה הרשמיים של Google:
- App ID: `ca-app-pub-3940256099942544~3347511713`
- Rewarded Ad Unit: `ca-app-pub-3940256099942544/5224354917`

אל תשתמש במזהי פרסום אמיתיים בזמן בדיקות. Google מזהירה ששימוש במודעות אמיתיות בזמן פיתוח עלול להביא להשעיית החשבון.

## איך להוציא APK דרך GitHub מהטלפון
1. חלץ את קובץ ה-ZIP בטלפון.
2. צור Repository חדש ב-GitHub בשם `crypto-tycoon-android`.
3. העלה את **תוכן התיקייה** שחילצת (לא את קובץ ה-ZIP עצמו) אל ה-Repository, כך ש-`app`, `build.gradle.kts`, `settings.gradle.kts` ו-`.github` יהיו בשורש.
4. ודא שהענף נקרא `main`. כל Push ל-main מפעיל את הבנייה אוטומטית; אין צורך ללחוץ Run workflow.
5. ב-GitHub פתח את לשונית **Actions**, בחר `Build Crypto Tycoon APK`, והמתן לסיום ירוק.
6. פתח את הריצה שהסתיימה, גלול ל-**Artifacts**, והורד `crypto-tycoon-debug-apk`.
7. חלץ את ה-Artifact והתקן את `app-debug.apk` בטלפון. ייתכן שתצטרך לאשר התקנה ממקור זה.

## חיבור לחשבון AdMob שלך
לפני פרסום:
1. צור/רשום את האפליקציה ב-AdMob עם שם החבילה `com.cryptotycoon.game`.
2. צור יחידת מודעה מסוג **Rewarded**.
3. החלף את `ADMOB_APP_ID` ואת `REWARDED_UNIT_ID` בקובץ:
   `app/src/main/java/com/cryptotycoon/game/MainActivity.java`
4. החלף את `android:value` של `com.google.android.gms.ads.APPLICATION_ID` בקובץ:
   `app/src/main/AndroidManifest.xml`
5. בצע Push חדש ל-GitHub. ה-APK החדש ייבנה אוטומטית.

## פרטים טכניים
- Package name: `com.cryptotycoon.game`
- minSdk: 24 (Android 7.0)
- targetSdk / compileSdk: 35
- Java 17 עם desugaring, Gradle 8.11.1, Android Gradle Plugin 8.9.2
- SDK: `com.google.android.libraries.ads.mobile.sdk:ads-mobile-sdk:1.6.0`

## חשוב לפני Google Play
זהו APK מסוג debug לצורכי בדיקה. לפרסום בחנות צריך ליצור חתימה release, להגדיר מדיניות פרטיות/הסכמות פרסום (כולל UMP לפי הצורך), למלא פרטי Data safety ותוכן, ולבדוק את כל דרישות Google Play. הפרויקט לא נרשם אוטומטית ב-AdMob ולא מייצר הכנסות עד שמחברים מזהים אמיתיים ומאשרים את האפליקציה.
