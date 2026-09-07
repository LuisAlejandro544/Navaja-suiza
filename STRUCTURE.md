# Estructura del Proyecto — Navaja Suiza 🇨🇭

Este documento detalla la estructura física y arquitectónica del código fuente, especificando la separación de responsabilidades entre las capas de presentación, dominio, datos y código nativo.

---

## 🏗️ Árbol de Directorios

```
/
├── app/
│   ├── build.gradle.kts           # Configuración del módulo de aplicación Android, NDK y dependencias
│   ├── proguard-rules.pro         # Reglas de optimización ProGuard/R8
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml # Manifiesto con configuración de temas y Application
│       │   ├── cpp/                # Código fuente Nativo (C / C++)
│       │   │   ├── CMakeLists.txt  # Script de compilación CMake 3.22+
│       │   │   ├── native-lib.cpp  # Exportación JNI y llamadas a Lua y C++20
│       │   │   └── lua/            # Código fuente original de Lua 5.4.7 (C ANSI puro)
│       │   │       ├── lua.h, luaconf.h, lualib.h, lauxlib.h
│       │   │       ├── lapi.c, lcode.c, lctype.c, ldebug.c, ldo.c, ldump.c
│       │   │       ├── lfunc.c, lgc.c, llex.c, lmem.c, lobject.c, lopcodes.c
│       │   │       ├── lparser.c, lstate.c, lstring.c, ltable.c, ltm.c
│       │   │       ├── lundump.c, lvm.c, lzio.c
│       │   │       └── lauxlib.c, lbaselib.c, lcorolib.c, ldblib.c, liolib.c
│       │   │           lmathlib.c, loadlib.c, loslib.c, lstrlib.c, ltablib.c, lutf8lib.c, linit.c
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt # Actividad principal y punto de entrada de Jetpack Compose
│       │   │   ├── data/           # Capa de Datos (Room Database)
│       │   │   │   ├── CalculationEntity.kt   # Entidad de historial de cálculos
│       │   │   │   ├── CalculationDao.kt      # Data Access Object con queries Room
│       │   │   │   ├── AppDatabase.kt         # Definición de la base de datos Room
│       │   │   │   └── CalculationRepository.kt# Repositorio que expone Flows reactivos
│       │   │   ├── domain/         # Capa de Dominio (Lógica de negocio matemática pura)
│       │   │   │   └── CalculatorEngine.kt    # Tokenizador, Shunting-Yard y evaluador RPN
│       │   │   ├── nativebridge/   # Capa de Enlace Nativo (JNI)
│       │   │   │   └── NativeEngine.kt        # Objeto Kotlin con funciones externas en C++/Lua/Rust
│       │   │   └── ui/             # Capa de Presentación (Jetpack Compose M3)
│       │   │       ├── CalculatorScreen.kt    # Pantalla, teclados, cabecera y BottomSheet de historial
│       │   │       ├── CalculatorViewModel.kt # ViewModel con MutableStateFlow y manejo de eventos
│       │   │       └── theme/                 # Diseño, colores, tipografía y formas M3
│       │   │           ├── Color.kt           # Paleta de colores Slate / Dark Modern
│       │   │           ├── Theme.kt           # ColorScheme y composable NavajaSuizaTheme
│       │   │           └── Type.kt            # Definición tipográfica
│       │   └── res/
│       │       ├── values/
│       │       │   ├── strings.xml            # Textos localizados en español
│       │       │   ├── colors.xml             # Colores de recursos
│       │       │   └── themes.xml             # Tema base de ventana y splash
│       │       └── mipmap-*/                  # Iconos adaptativos de la aplicación
├── gradle/
│   └── libs.versions.toml         # Catálogo de versiones centralizado de dependencias
├── build.gradle.kts               # Configuración Gradle raíz del proyecto
├── settings.gradle.kts            # Configuración de repositorios y módulos
├── .gitignore                     # Filtros de exclusión para C/C++, Rust, Lua y Gradle
├── metadata.json                  # Identificación del proyecto en AI Studio
├── README.md                      # Documentación principal
├── ROADMAP.md                     # Hoja de ruta de características
├── STRUCTURE.md                   # Este archivo (arquitectura y componentes)
├── AI_CONTEXT.md                  # Contexto para agentes de inteligencia artificial
└── AGENTS.md                      # Instrucciones operativas para agentes
```

---

## 🧩 Modelo de Datos (Room Database)

### Tabla `calculations`
Almacena las operaciones evaluadas con éxito para consulta histórica:

| Campo | Tipo | Restricciones | Descripción |
|---|---|---|---|
| `id` | `Long` | Clave primaria autoincremental (`autoGenerate = true`) | Identificador único del registro |
| `expression` | `String` | Not Null | Expresión matemática ingresada (ej: `sin(45) + 8 × 2`) |
| `result` | `String` | Not Null | Resultado formateado de la operación (ej: `16.70710678`) |
| `timestamp` | `Long` | Not Null, Indexado Descendente | Marca temporal en milisegundos (`System.currentTimeMillis()`) |

---

## ⚡ Flujo de Datos Arquitectónico (Unidirectional Data Flow)

```
[Usuario] -> Toca tecla en CalculatorScreen
    │
    ▼
[CalculatorViewModel] -> Procesa evento (onDigit, onOperator, onScientificFunction)
    │
    ├─► [CalculatorEngine] -> Sanitiza expresión, tokeniza, ejecuta Shunting-Yard
    │                           Retorna EvaluationResult.Success / Error
    │
    ├─► Emite estado actualizado mediante StateFlow<CalculatorUiState>
    │
    └─► (Al presionar "=") Guarda en [CalculationRepository] -> [CalculationDao] -> SQLite Room
            │
            ▼
        Emite lista actualizada de historial a través de Flow<List<CalculationEntity>>
            │
            ▼
    [CalculatorScreen] se recompone eficientemente mostrando la expresión, resultado e historial
```

---

## 🔌 Capa Nativa (C, C++, Rust, Lua)

1. **Lua 5.4 oficial (`app/src/main/cpp/lua/`):**
   - Compilado como biblioteca estática `lua_native` mediante CMake.
   - Código en C ANSI 100% puro sin envoltorios de terceros.
   - Permite instanciar un `lua_State`, registrar funciones nativas y evaluar scripts de forma determinista.

2. **C++20 NDK Bridge (`app/src/main/cpp/native-lib.cpp`):**
   - Compilado como biblioteca compartida `libswissknife_native.so`.
   - Implementa funciones exportadas vía `extern "C"` con firmas JNI estándar para interacción directa con Kotlin.

3. **Kotlin Native Bridge (`NativeEngine.kt`):**
   - Clase singleton que carga las librerías nativas con `System.loadLibrary` y expone métodos `external fun`.
