# Nefis Lokanta Android uygulama kaynak projesi

Bu Android Studio projesi, uygulama açıldığında yalnızca `https://nefislokanta.com/mobil/` adresini yükleyen WebView tabanlı bir uygulamadır. Mobil şifreyi APK içinde saklamaz; şifre, sitedeki Nefis Mobil Menü eklentisinin ayarlarından belirlenir ve sunucuda doğrulanır.

## APK oluşturma
1. Android Studio (güncel sürüm) kurun.
2. Android Studio'da `nefis-android-app` klasörünü **Open** ile açın.
3. Gradle Sync tamamlanınca `Build > Build Bundle(s) / APK(s) > Build APK(s)` seçin.
4. APK, `app/build/outputs/apk/debug/app-debug.apk` konumunda oluşur.
5. APK'yı telefona aktarın. Telefonda bu kaynaktan uygulama yüklemeye izin verin ve APK'ya dokunarak yükleyin.

Bu kaynak proje derlenmiş APK değildir. Derleme için Android Studio ve Android SDK gerekir.
