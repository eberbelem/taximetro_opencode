# BUILD - Taxímetro Digital

## Requisitos mínimos

- Android Studio Hedgehog (2023.1.1) ou superior
- Android SDK 34
- JDK 17
- Dispositivo Android com API 26+ (Android 8.0+)

## Passos para gerar APK Debug

O mapa usa OpenStreetMap (osmdroid) — gratuito, sem API key.

### 1. Inicializar Gradle Wrapper (primeira vez)

O repositório contém `gradlew.bat` e `gradle-wrapper.properties`, mas o JAR do wrapper precisa ser gerado:

**Opção A - Deixe o Android Studio gerar ao abrir o projeto:**
- File > Open > selecionar a pasta
- O Studio fará o download automático do Gradle

**Opção B - Via terminal (requer Gradle instalado):**
```bash
# No diretório do projeto
gradle wrapper --gradle-version 8.5
```

### 2. Abrir no Android Studio

```bash
# Pelo terminal
studio "E:\projetos\taximetro opencode"

# Ou manualmente: File > Open > selecionar a pasta
```

### 3. Gerar APK Debug

**Opção A - Pelo Android Studio:**

```
Build > Build Bundle(s) / APK(s) > Build APK(s)
```

O APK será gerado em:
`app/build/outputs/apk/debug/app-debug.apk`

**Opção B - Pelo terminal:**

```bash
# Windows (PowerShell)
cd E:\projetos\taximetro opencode
./gradlew assembleDebug
```

APK gerado em: `app\build\outputs\apk\debug\app-debug.apk`

### 4. Instalar no dispositivo

```bash
# Com o dispositivo conectado via USB (debug USB habilitado)
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

Ou copie o APK para o dispositivo e instale manualmente.

## Estrutura do APK Debug

- `applicationId`: com.taximetro
- `versionName`: 1.0.0
- `minSdk`: 26
- `targetSdk`: 34
- `debuggable`: true (logs habilitados)

## Logs de GPS

Os logs de GPS são salvos em:
`Android/data/com.taximetro/files/gps_logs/`

Formato CSV:
```
timestamp,latitude,longitude,speed_kmh,accuracy_m
```

## Executar testes unitários

```bash
./gradlew testDebugUnitTest
```

Relatório HTML em: `app/build/reports/tests/testDebugUnitTest/index.html`
