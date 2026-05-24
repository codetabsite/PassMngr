# PassMngr

Minimalist Android şifre yöneticisi. AES-256-GCM şifreleme, biyometrik kilit, Room DB.

## Özellikler

- AES-256-GCM ile şifrelenmiş yerel depolama (Android Keystore)
- Parmak izi / yüz tanıma ile uygulama kilidi
- Güçlü şifre üretici + entropiye dayalı güç göstergesi
- Kategori filtresi (Sosyal Medya, Banka, Oyun, E-posta, İş, Diğer)
- Arama / filtreleme
- Tek dokunuşla kopyala (30 sn sonra panoya otomatik temizleme)
- CSV yedekleme / dışa aktarma
- Karanlık tema, sıfır reklam, sıfır izin (ağ yok)

## Kurulum

```bash
git clone https://github.com/KULLANICI/PassMngr.git
cd PassMngr
./gradlew assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`

## GitHub Actions — Release APK İmzalama

Repository → Settings → Secrets → Actions:

| Secret | Açıklama |
|---|---|
| `KEYSTORE_BASE64` | `base64 -i my.jks` çıktısı |
| `KEYSTORE_PASSWORD` | Keystore şifresi |
| `KEY_ALIAS` | Key alias |
| `KEY_PASSWORD` | Key şifresi |

Tag push ile otomatik release APK oluşturulur:

```bash
git tag v1.0.0
git push origin v1.0.0
```

## Mimari

```
PassMngrApp
├── data/
│   ├── model/       Password entity, Category enum
│   ├── db/          Room DAO + Database + TypeConverters
│   └── repository/  PasswordRepository (şifreleme burada)
├── ui/
│   ├── auth/        Biyometrik giriş
│   ├── home/        Liste + arama + filtre
│   ├── add/         Ekle / düzenle
│   └── detail/      Detay + kopyala
└── util/
    ├── CryptoManager   AES-256-GCM (Android Keystore)
    ├── PasswordGenerator  Güvenli şifre + entropiye dayalı güç skoru
    ├── ClipboardUtil   Kopyala + 30sn sonra temizle
    └── ExportManager   CSV dışa aktarma
```

## Güvenlik Notları

- Şifreler yalnızca Android Keystore'a bağlı AES-256-GCM anahtarıyla şifrelenir
- Düz metin şifreler hiçbir zaman diske yazılmaz
- `allowBackup="false"` — sistem yedeğinden dışlanır
- Ağ izni yok — veri asla cihaz dışına çıkmaz
- Pano 30 saniye sonra otomatik temizlenir
