# Nefis Lokanta Android uygulama kaynak projesi

Bu Android Studio projesi, uygulama açıldığında yalnızca `https://nefislokanta.com/mobil/` adresini yükleyen WebView tabanlı bir uygulamadır. Mobil şifreyi APK içinde saklamaz; şifre, sitedeki Nefis Mobil Menü eklentisinin ayarlarından belirlenir ve sunucuda doğrulanır.

## APK oluşturma
1. Android Studio (güncel sürüm) kurun.
2. Android Studio'da `nefis-android-app` klasörünü **Open** ile açın.
3. Gradle Sync tamamlanınca `Build > Build Bundle(s) / APK(s) > Build APK(s)` seçin.
4. APK, `app/build/outputs/apk/debug/app-debug.apk` konumunda oluşur.
5. APK'yı telefona aktarın. Telefonda bu kaynaktan uygulama yüklemeye izin verin ve APK'ya dokunarak yükleyin.

Bu kaynak proje derlenmiş APK değildir. Derleme için Android Studio ve Android SDK gerekir.


## Hosting üzerinden güncelleme kontrolü

Uygulama açılışta şu herkese açık, salt-okunur JSON adresini kontrol eder: `https://nefislokanta.com/wp-json/nefis-mobil/v1/app-update`. Sürüm, APK bağlantısı ve güncelleme notu Nefis Mobil Menü eklentisinin yönetim sayfasından yönetilir. Yeni APK'yı aynı `applicationId` ve aynı imzalama anahtarıyla derlemek gerekir. Her güncellemede Android `versionCode` değerini artırın; eklentideki **Sürüm kodu** alanı da APK içindeki `versionCode` ile aynı olmalıdır.

Uygulama yeni bir sürüm bulursa **Güncelle** penceresi gösterir ve APK URL'sini açar. Android güvenlik nedeniyle indirme/kurulum onayı isteyebilir; uygulama kendini sessizce kuramaz. APK dosyası HTTPS üzerinden erişilebilir olmalı.


## Android 9 (API 28) compatibility
- `minSdk 23` means Android 6.0 and newer, including Android 9, are supported.
- Java and Kotlin bytecode targets are explicitly set to JVM 17 to avoid inconsistent JVM-target build errors. Use JDK 17 in Android Studio / GitHub Actions.
- This source is configured as version `1.1.0` (`versionCode 2`) for the first updater-enabled release. Set the same version code/name in the Nefis Mobil Menü update settings.
- If Android says “Uygulama yüklenmedi”, an existing install signed with a different key is a common cause. An APK must use the same signing key to update an installed app; otherwise uninstall the old app before installing (this may clear local app data).
- Source configuration has not been device-tested on a physical Android 9 phone.
