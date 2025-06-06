# PromptFlow - Contexto del Proyecto 📋

> **Archivo de contexto para asistente IA** - Este archivo contiene información esencial sobre el proyecto para mantener continuidad entre conversaciones.

## 📋 Información General

- **Nombre del Proyecto:** PromptFlow
- **Descripción:** Aplicación moderna de teleprompter para Android
- **Propietario:** Colin Moreno-Burgess (@ColinBurgess)
- **Repositorio:** https://github.com/ColinBurgess/PromptFlow
- **Idioma de código:** Inglés (por regla del usuario)
- **Idioma de comunicación:** Español (por regla del usuario)
- **Fecha de inicio:** 6 de junio de 2025

## 🎯 Decisiones de Diseño Tomadas

### Nombre de la Aplicación
- **Elegido:** PromptFlow
- **Razones:**
  - Sugiere flujo suave del texto
  - Fácil de recordar y pronunciar
  - Profesional y moderno
- **Rechazados:** SpeechCue, TeleScript, FlowText, CueCard

### Tecnologías Seleccionadas
- **Lenguaje:** Kotlin
- **UI Framework:** Jetpack Compose
- **Arquitectura:** MVVM (preparada para implementar)
- **Base de datos:** Room (dependencia añadida)
- **Tema:** Material Design 3
- **Target SDK:** 34
- **Min SDK:** 24

### Orientación y UX
- **Orientación:** Landscape forzada (típico de teleprompters)
- **Fondo:** Negro (profesional para teleprompters)
- **Texto:** Blanco sobre negro
- **Ubicación controles:** Parte inferior con transparencia

## 🚀 Estado Actual del Desarrollo

### ✅ Completado
- [x] Estructura inicial del proyecto Android
- [x] Configuración de Gradle con todas las dependencias
- [x] Pantalla principal (`TeleprompterScreen`) con funcionalidad básica
- [x] Sistema de temas (Color, Typography, Theme)
- [x] MainActivity con Jetpack Compose
- [x] AndroidManifest con permisos y configuración
- [x] Recursos (strings.xml, themes.xml)
- [x] Repositorio Git inicializado
- [x] Repositorio GitHub creado y sincronizado
- [x] README bilingüe (español/inglés)
- [x] .gitignore para Android

### 🎛️ Funcionalidades Implementadas
- [x] **Control de reproducción:** Play/Pause con FAB
- [x] **Velocidad ajustable:** Slider de 1x a 10x
- [x] **Tamaño de fuente:** Slider de 16sp a 48sp
- [x] **Editor de texto:** Panel de configuración con TextField
- [x] **Auto-scroll:** Animación suave basada en velocidad
- [x] **Reset:** Funcionalidad para reiniciar posición
- [x] **UI responsive:** Controles bien distribuidos en landscape

## 🏗️ Arquitectura del Código

### Estructura de Paquetes
```
com.promptflow.android/
├── MainActivity.kt
└── ui/
    ├── screen/
    │   └── TeleprompterScreen.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

### Componentes Principales
- **MainActivity:** Activity principal con Scaffold y Surface
- **TeleprompterScreen:** Composable principal con toda la lógica
- **PromptFlowTheme:** Tema personalizado con Material Design 3

### Estado Manejado
- `isPlaying: Boolean` - Control de reproducción
- `speed: Float` - Velocidad de scroll (1f-10f)
- `fontSize: Float` - Tamaño de fuente (16f-48f)
- `showSettings: Boolean` - Visibilidad del panel de configuración
- `text: String` - Contenido del teleprompter
- `listState: LazyListState` - Estado del scroll

## 📋 TODOs y Próximos Pasos

### 🔥 Prioridad Alta
- [ ] **Modo espejo:** Implementar texto espejado para teleprompters físicos
- [ ] **Persistencia:** Guardar texto y configuración con DataStore
- [ ] **Gestos:** Control por tap/swipe además de botones
- [ ] **Pantalla completa:** Ocultar barra de estado y navegación

### 🎯 Prioridad Media
- [ ] **Control remoto:** Implementar con WebSocket o Bluetooth
- [ ] **Múltiples textos:** Sistema de scripts guardados
- [ ] **Marcadores:** Sistema de bookmarks en el texto
- [ ] **Configuración avanzada:** Colores, fuentes, etc.

### 💡 Futuras Mejoras
- [ ] **Export/Import:** Funcionalidad para compartir scripts
- [ ] **Prompts de IA:** Integración para generar/mejorar textos
- [ ] **Analytics:** Métricas de uso y velocidad de lectura
- [ ] **Widget:** Control desde notificación o widget

## 🔧 Configuración Técnica

### Dependencias Clave
```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose:1.8.2")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

// Navigation (añadida para futuro uso)
implementation("androidx.navigation:navigation-compose:2.7.5")

// Room (añadida para persistencia futura)
implementation("androidx.room:room-runtime:2.6.1")
implementation("androidx.room:room-ktx:2.6.1")
```

### Permisos
- `android.permission.WAKE_LOCK` - Mantener pantalla encendida durante uso

### Configuración Específica
- **Orientación:** `android:screenOrientation="landscape"`
- **Tema:** `Theme.PromptFlow` (sin ActionBar)
- **Target API:** 31+ con soporte para tema dinámico

## 📝 Notas de Implementación

### Auto-scroll Algorithm
```kotlin
LaunchedEffect(isPlaying, speed) {
    if (isPlaying) {
        while (isPlaying) {
            delay((100 / speed).toLong())
            if (listState.canScrollForward) {
                listState.animateScrollBy(1f)
            } else {
                isPlaying = false // Auto-stop al final
            }
        }
    }
}
```

### Texto de Ejemplo
Se incluye texto de ejemplo explicando las funcionalidades de la app.

## 🐛 Problemas Conocidos

### Resueltos
- [x] **Firma de commits:** Resuelto usando `--no-gpg-sign`
- [x] **GITHUB_TOKEN:** Resuelto limpiando variable de entorno en fish shell
- [x] **Ubicación archivos:** Inicialmente creados en directorio incorrecto, movidos exitosamente

### Por Resolver
- [ ] Ninguno conocido actualmente

## 📊 Métricas del Proyecto

- **Archivos de código:** 14 archivos
- **Líneas de código:** ~640 líneas (commit inicial)
- **Commit hash inicial:** 9e49283
- **Tamaño del repositorio:** 8.43 KiB

## 🔄 Instrucciones para Próximas Conversaciones

### Para el asistente IA:
1. Leer este archivo al inicio de cada conversación
2. Actualizar este archivo cuando se tomen nuevas decisiones importantes
3. Mantener el TODO list actualizado con progreso
4. Documentar nuevos problemas y sus soluciones
5. Recordar las reglas: código en inglés, comunicación en español

### Para el desarrollador:
- Este archivo debe actualizarse manualmente o por el asistente
- Sirve como documentación viva del proyecto
- Útil para onboarding de nuevos colaboradores

---
**Última actualización:** 6 de junio de 2025
**Actualizado por:** Claude (Asistente IA inicial)
**Próxima revisión:** Al implementar nuevas funcionalidades importantes