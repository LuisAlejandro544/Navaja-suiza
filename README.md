# Navaja Suiza 🇨🇭

**Navaja Suiza** es una aplicación móvil Android de herramientas esenciales multiusos diseñada con Kotlin, Jetpack Compose y una arquitectura híbrida de alto rendimiento con código nativo compilado en **C (Lua 5.4 oficial)**, **C++20** y **Rust**.

---

## 📱 Módulos y Funcionalidades

### 1. Calculadora Moderna & Científica
- **Diseño Ergonómico y Pulido:** Inspirado en calculadoras minimalistas con paleta oscura de alto contraste, tipografía monoespaciada para lectura técnica y acentos cromáticos distintivos (naranja coral, azul cian y ámbar).
- **Operaciones Estándar:** Suma, resta, multiplicación, división, porcentajes, cambio de signo (±) y manejo inteligente de paréntesis.
- **Panel Científico Desplegable:** 
  - Funciones trigonométricas (`sin`, `cos`, `tan`) con soporte para grados (**DEG**) y radianes (**RAD**).
  - Modo Inverso (**INV**): funciones trigonométricas inversas (`sin⁻¹`, `cos⁻¹`, `tan⁻¹`), potencias de diez (`10ˣ`), exponencial natural (`eˣ`) y cuadrados (`x²`).
  - Logaritmos (`log`, `ln`), raíces cuadradas (`√`), potencias (`^`), factoriales (`x!`), constantes matemáticas (`π`, `e`) e inversos (`1/x`).
- **Vista Previa en Tiempo Real:** Evaluación reactiva continua mientras se teclea la expresión sin necesidad de presionar igual.
- **Historial Persistente con Room:** Almacenamiento local SQLite mediante Room de cada operación con su timestamp y expresión completa.
- **Acciones Rápidas:** Copiar resultado al portapapeles con un toque, reutilizar cualquier expresión o resultado previo desde la hoja deslizable inferior (Bottom Sheet).

### 2. Motor Nativo Políglota (C, C++, Rust, Lua)
- **C:** Lua 5.4 oficial compilado estáticamente desde el código fuente original en C (`src/main/cpp/lua/`).
- **C++20:** Capa JNI de alto rendimiento que conecta el motor nativo con el entorno Android Clang NDK.
- **Rust:** Módulo nativo compilado mediante JNI (`swiss_knife_core`) para algoritmos de cálculo numérico y seguridad de memoria.
- **Kotlin & Jetpack Compose:** Capa visual declarativa, gestión de estado unidireccional (MVI/MVVM con `StateFlow`) y diseño Material 3 adaptativo.

---

## 🛠️ Requisitos Previos y Entorno

- **Sistema Operativo:** Android 7.0 (API nivel 24) o superior.
- **Java Development Kit:** JDK 17 o 21.
- **Android Gradle Plugin:** 8.9+
- **Gradle:** 8.11+
- **Android NDK:** 26.x o superior con soporte para CMake 3.22.1+.
- **Arquitecturas Compatibles:** `arm64-v8a`, `armeabi-v7a`, `x86_64`, `x86`.

---

## 🚀 Compilación e Instalación

### Compilar e Instalar el APK de Debug
```bash
./gradlew assembleDebug
```
El archivo APK generado se ubicará en:
`app/build/outputs/apk/debug/app-debug.apk`

### Instalar directamente en un dispositivo o emulador:
```bash
./gradlew installDebug
```

### Ejecutar Pruebas Locales (Robolectric / JUnit):
```bash
./gradlew testDebugUnitTest
```

---

## 📂 Estructura del Repositorio

```
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── cpp/                # Código fuente en C (Lua 5.4) y C++20 con CMake
│   │   │   │   ├── lua/            # Fuentes originales de Lua 5.4 (lapi.c, lvm.c, etc.)
│   │   │   │   ├── CMakeLists.txt  # Configuración CMake para compilar lua_native y swissknife_native
│   │   │   │   └── native-lib.cpp  # JNI bindings en C++ hacia Android
│   │   │   ├── java/com/example/
│   │   │   │   ├── data/           # Base de datos Room (Entidades, DAO, Repositorio)
│   │   │   │   ├── domain/         # Motor de cálculo Shunting-Yard (CalculatorEngine)
│   │   │   │   ├── nativebridge/   # Interfaz JNI en Kotlin (NativeEngine)
│   │   │   │   └── ui/             # Jetpack Compose Screens, ViewModels y Tema M3
│   │   │   └── res/                # Recursos XML, cadenas y drawables
│   └── build.gradle.kts            # Configuración de dependencias y NDK/CMake
├── gradle/                         # Wrapper de Gradle y Version Catalog (libs.versions.toml)
├── metadata.json                   # Metadatos de la plataforma
├── README.md                       # Documentación general del proyecto
├── ROADMAP.md                      # Plan de evolución y siguientes funcionalidades
├── STRUCTURE.md                    # Arquitectura técnica y modelo de datos
├── AI_CONTEXT.md                   # Contexto de negocio y técnico para IAs colaboradoras
└── AGENTS.md                       # Reglas operativas y directivas de desarrollo
```

---

## 📄 Licencia y Distribución
Desarrollado para distribución independiente en tiendas de APKs y plataformas libres de distribución móvil.
