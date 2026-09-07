# Roadmap — Navaja Suiza 🇨🇭

Este documento traza la hoja de ruta para la evolución de la aplicación multitarea, organizando las fases de desarrollo según el ciclo de vida del software y priorizando la robustez técnica sobre el volumen superfluo.

---

## 📍 Fase 1: Base de la Calculadora & Motor Políglota (Completada ✅)
- [x] Motor de cálculo matemático en Kotlin con algoritmo Shunting-Yard para evaluación de expresiones complejas respetando precedencia de operadores.
- [x] Soporte para operaciones científicas: funciones trigonométricas (`sin`, `cos`, `tan`), logarítmicas (`ln`, `log`), exponenciales, potencias (`^`), factoriales (`!`), raíces (`√`) y constantes universales (`π`, `e`).
- [x] Conmutador de unidades angulares: Grados sexagesimales (**DEG**) y Radianes (**RAD**).
- [x] Modo inverso (**INV**) para funciones trigonométricas e inversas algebraicas.
- [x] Persistencia de cálculos en base de datos local SQLite utilizando **Room** con timestamps y recuperación directa.
- [x] Interfaz de usuario moderna con paleta oscura de precisión, animación fluida en panel científico y respuesta háptica.
- [x] Integración de **Lua 5.4 oficial** en C nativo compilado mediante CMake y Android NDK.
- [x] Integración de puente **C++20** con JNI para interconexión fluida con Kotlin.
- [x] Estructura base para librerías nativas en **Rust** orientadas a operaciones numéricas y criptográficas seguras.
- [x] Limpieza rigurosa de artefactos en `.gitignore` para C, C++, Rust y Lua.

---

## 📍 Fase 2: Ejecución de Scripts y Sandbox Lua / Rust (Siguiente Hito 🔄)
- [ ] **Consola Lua Integrada:** Interfaz de usuario para ejecutar scripts rápidos en lenguaje Lua 5.4 nativo directamente desde el dispositivo sin necesidad de PC.
- [ ] **Biblioteca de Fórmulas en Lua:** Posibilidad de guardar funciones personalizadas del usuario (ej: conversiones físicas, cálculos financieros) escritas en sintaxis Lua.
- [ ] **Aceleración Numérica en Rust:** Migración de operaciones matriciales y cálculos de alta precisión (BigInt / números arbitrariamente grandes) al núcleo Rust.

---

## 📍 Fase 3: Conversor de Unidades & Monedas
- [ ] **Conversor Universal Offline:**
  - Longitud, masa, volumen, temperatura, velocidad, presión y área.
  - Almacenamiento local de ratios de conversión en Room.
- [ ] **Conversor de Bases Numéricas:**
  - Conversión simultánea en tiempo real entre Decimal, Binario, Octal y Hexadecimal con manipulación de bits a nivel binario (AND, OR, XOR, NOT, desplazamientos lógicos y aritméticos).

---

## 📍 Fase 4: Herramientas de Productividad y Texto
- [ ] **Utilidades de Criptografía y Hash:** Generador de hashes MD5, SHA-256, SHA-512 y codificador/decodificador Base64 y URL.
- [ ] **Contador y Analizador de Texto:** Extractor de estadísticas (palabras, caracteres, líneas, tiempo de lectura estimado).
- [ ] **Cronómetro y Temporizador de Precisión:** Con soporte para vueltas (laps) e indicación visual mediante Compose.

---

## 📍 Fase 5: Optimización & Distribución
- [ ] Optimización de compilación NDK con LTO (Link Time Optimization) en binarios C/C++/Rust para reducir el tamaño final de las librerías compartidas.
- [ ] Empaquetado y firma de APK optimizado para distribución directa en tiendas de terceros y repositorios libres (Uptodown, APKPure, F-Droid).
- [ ] Soporte para temas personalizables (Ambar, Obsidiana, Cyberpunk, Monocromo).
