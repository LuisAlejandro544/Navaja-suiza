# Contexto de IA — Navaja Suiza 🇨🇭

Este documento sirve de guía contextual para modelos de lenguaje y agentes de código que colaboren en el desarrollo, mantenimiento y ampliación del proyecto **Navaja Suiza**.

---

## 🎯 Propósito del Proyecto
**Navaja Suiza** es una aplicación móvil Android diseñada para consolidar herramientas cotidianas de máxima utilidad en un único binario ligero, rápido, offline y sin dependencias de servicios externos en la nube. La primera herramienta principal es una **Calculadora Científica Moderna**, con planes de incorporar ejecutores de scripts Lua, utilidades de criptografía y conversiones de unidades.

---

## 💻 Entorno del Usuario y Restricciones Operativas
1. **Dispositivo del Usuario:** El usuario opera principalmente desde un **teléfono móvil** (sin acceso directo a una PC con terminal de desarrollo). Todo el código generado debe compilar directamente en el pipeline de AI Studio sin requerir comandos manuales complejos del lado del cliente.
2. **Canal de Distribución:** La aplicación se distribuirá en plataformas libres y tiendas de APK de terceros (como **Uptodown** o descarga directa), no a través de Google Play Store. No aplicar restricciones innecesarias de Play Store si limitan las capacidades locales del usuario.
3. **Manejo de Dependencias y Tamaño de APK:** Al usuario no le preocupa el peso final del APK siempre y cuando las herramientas y librerías sean **100% funcionales y robustas**. Evitar "reinventar la rueda" sin dependencias: utilizar siempre librerías estables y dependencias probadas.
4. **Protección de Marcas:** Prohibido nombrar archivos o variables utilizando marcas registradas comerciales que puedan comprometer legalmente al usuario.
5. **No Simulación de Rendimiento Peligrosa:** En caso de agregar utilidades para juegos o aceleradores, jamás emplear manipulaciones de variables como `persist.sys.*`.

---

## 🧠 Arquitectura de Software y Patrones de Diseño
- **Lenguaje Principal:** Kotlin moderno (100% Jetpack Compose para la interfaz de usuario).
- **Capa Nativa:** C (Lua 5.4 oficial), C++20 (NDK Clang) y Rust.
  - **REGLA CRÍTICA:** Siempre que se incluya C, C++, Rust o Lua, deben estar completamente enlazados y compilados en el ciclo de Gradle/CMake. Jamás crear funciones "fallback" o "mock" en Kotlin que sustituyan silenciosamente las bibliotecas nativas solicitadas por el usuario.
- **Manejo de Estado UI:** Jetpack Compose con `StateFlow` y `collectAsStateWithLifecycle()`. Los eventos fluyen hacia el `ViewModel` y el estado fluye hacia los Composables.
- **Persistencia:** Room Database con Kotlin Coroutines y Flow reactivo.
- **Evaluación Matemática:** Implementada en `CalculatorEngine.kt` utilizando el algoritmo **Shunting-Yard** (Dijkstra) con Notación Polaca Inversa (RPN), permitiendo extensión sencilla para nuevos operadores y funciones matemáticas.

---

## 🎨 Principios de Diseño UI (Inspiración y Estilo)
- **Tema:** Modo oscuro técnico moderno, inspirado en herramientas de precisión suizas.
- **Paleta de Colores (`Color.kt`):**
  - Fondo: Slate oscuro (`#0D0F14`, `#131722`).
  - Teclado numérico: Slate profundo (`#1A1F2C`).
  - Operadores: Coral cálido (`#FF6D3B`).
  - Funciones científicas: Azul cian (`#38BDF8`) y Teal (`#00B4D8`).
  - Acción / Borrado: Carmesí (`#EF4444`).
  - Igual / Éxito: Naranja ámbar vibrante (`#FF6D3B` / `#F97316`).
- **Ergonomía:**
  - Botones con formas suaves redondeadas (radio 20-22dp).
  - Feedback háptico en cada toque.
  - Indicadores claros de estado para **DEG / RAD** e **INV**.
  - Visualización dual: expresión superior con tamaño dinámico y vista previa del resultado en tiempo real en color de contraste.
