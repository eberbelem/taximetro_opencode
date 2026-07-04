# BUILD - Taxímetro Digital

## Requisitos mínimos

- Android Studio Hedgehog (2023.1.1) ou superior
- Android SDK 34
- JDK 17
- Dispositivo Android com API 26+ (Android 8.0+)
- Google Maps API Key

## Passos para gerar APK Debug

### 1. Configurar Google Maps API Key

Abra `app/src/main/res/values/strings.xml` e substitua:

```xml
<string name="maps_api_key">SUA_API_KEY_AQUI</string>
```

Por sua chave de API do Google Maps (Android).

Para obter uma chave:
1. Acesse https://console.cloud.google.com/
2. Crie um projeto ou selecione existente
3. Habilite a API "Maps SDK for Android"
4. Crie uma credencial de "Chave de API"
5. Restrinja a chave para Android com o SHA-1 do seu keystore de debug:
   - `keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android`

### 2. Inicializar Gradle Wrapper (primeira vez)

O repositório contém `gradlew.bat` e `gradle-wrapper.properties`, mas o JAR do wrapper precisa ser gerado:

**Opção A - Deixe o Android Studio gerar ao abrir o projeto:**
- File > Open > selecionar a pasta
- O Studio fará o download automático do Gradle

**Opção B - Via terminal (requer Gradle instalado):**
```bash
# No diretório do projeto
gradle wrapper --gradle-version 8.5
```

### 3. Abrir no Android Studio

```bash
# Pelo terminal
studio "E:\projetos\taximetro opencode"

# Ou manualmente: File > Open > selecionar a pasta
```

### 4. Gerar APK Debug

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

### 5. Instalar no dispositivo

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
