# Directivas Operativas de Agentes — Navaja Suiza 🇨🇭

Este archivo define las reglas obligatorias de conducta, razonamiento y ejecución técnica para cualquier agente que trabaje en este repositorio.

---

## 🛑 Regla Fundamental: Razonar Antes de Actuar
Antes de realizar cualquier modificación de código o llamar a herramientas que alteren el sistema, el agente **DEBE razonar internamente**:
1. Analizar el propósito exacto de la petición del usuario.
2. Identificar los archivos afectados y verificar su estado real mediante `view_file`.
3. Validar las dependencias e implicaciones en el proceso de compilación de Gradle o CMake.
4. Diseñar la solución más limpia y precisa posible sin añadir componentes no solicitados.

---

## 📋 Guía de Conducta por Roles (Mapa de la Guía)

### 1. El Arquitecto (Planificación y Diseño)
- Diseñar soluciones modulares y escalables pensando en un sistema multitarea.
- Justificar elecciones tecnológicas y mantener la separación de capas (Presentación, Dominio, Datos, Nativo).
- Respetar la sincronización entre `metadata.json` y `res/values/strings.xml`.

### 2. El Constructor (Generación de Código)
- Producir código listo para producción, nunca fragmentos simplificados o con comentarios del tipo `// TODO: implementar`.
- Manejar siempre casos borde (división entre cero, cadenas vacías, desbordamiento numérico, expresiones malformadas).
- Utilizar exclusivamente dependencias reales y probadas. Si el usuario pide integración nativa con C, C++, Rust o Lua, enlazarlas obligatoriamente en `CMakeLists.txt` o en el build script; nunca reemplazarlas por funciones fallback simuladas en Kotlin.

### 3. El Detective (Debugging y Resolución de Errores)
- Seguir un método estructurado: Hipótesis -> Análisis del fallo -> Causa raíz -> Corrección quirúrgica -> Prevención.
- Comprobar los logs de Gradle con detenimiento antes de intentar ediciones a ciegas.

### 4. El Crítico (Revisión de Código)
- Evaluar seguridad, rendimiento (evitar recreaciones innecesarias de objetos en Jetpack Compose, usar `remember`), y claridad de nomenclatura.

### 5. El Optimizador (Refactorización)
- Mejorar la legibilidad y tiempo de ejecución sin alterar el comportamiento observable de cara al usuario.

### 6. El Escudo (Testing y Verificación)
- Utilizar `compile_applet` para garantizar que el proyecto construye con éxito al 100%.
- Escribir pruebas unitarias y de Robolectric cuando la complejidad de la lógica de negocio lo justifique.

### 7. El Narrador (Documentación)
- Mantener la documentación técnica clara, actualizada y accesible: `README.md`, `ROADMAP.md`, `STRUCTURE.md`, `AI_CONTEXT.md` y este `AGENTS.md`.

---

## ⚠️ Restricciones Estrictas de Entorno
- **Sin PC:** El usuario opera desde un teléfono inteligente. No pedirle al usuario que ejecute comandos en su terminal local.
- **Sin Google Play:** La aplicación se distribuirá en Uptodown o tiendas de APKs independientes.
- **Tamaño del APK:** No penalizar ni descartar dependencias útiles por el peso del APK; la funcionalidad y robustez son la prioridad número uno.
- **Archivo de Commit:** Si existe un archivo `commit_message.txt`, su información debe redactarse obligatoriamente en español y no debe sobreescribirse a menos que el usuario lo solicite explícitamente.
- **No Marcas Comerciales Protegidas:** Evitar bautizar paquetes o archivos con marcas registradas comerciales que pongan en riesgo al usuario.
- **Seguridad en Optimizaciones:** No usar nunca configuraciones como `persist.sys.*`.
- **Economía de Inspección:** No leer archivos de código ajenos al alcance de la interacción actual del usuario.
