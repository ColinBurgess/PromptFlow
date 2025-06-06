# PromptFlow - Contexto del Proyecto

## 📱 **Descripción del Proyecto**

PromptFlow es una aplicación Android de teleprompter profesional con sincronización en la nube. Permite a los usuarios crear, editar y visualizar textos que se desplazan automáticamente en pantalla, ideal para presentaciones, grabaciones de video y discursos. **Ahora con soporte completo para orientación horizontal y vertical, optimizada especialmente para tablets con layouts adaptativos eficientes.**

## 🎯 **Características Principales**

### ✅ **Implementadas y Funcionando**
- **Teleprompter Core**: Desplazamiento suave de texto con controles de velocidad (1x-25x) y tamaño de fuente (16sp-48sp)
- **Multi-Orientación Optimizada**: Soporte completo para orientación horizontal y vertical con layouts adaptativos específicos por dispositivo
- **Layout Horizontal Optimizado**: TopAppBar compacto (48dp), tabs laterales sin texto cortado, eliminación de espacios en blanco
- **Optimización para Tablets**: Interfaz específica para tablets en modo vertical con controles laterales
- **Interfaz Adaptativa**: Detecta automáticamente el tamaño de pantalla y orientación para mostrar el layout óptimo
- **Autenticación Google**: Integración completa con Firebase Auth y Google Sign-In
- **Almacenamiento Local**: Sistema robusto de guardado/carga de textos usando SharedPreferences + Gson
- **Biblioteca de Textos**: Gestión completa (crear, editar, eliminar, seleccionar textos)
- **Manejo de Errores**: Mensajes amigables para usuario con debugging completo
- **Estados de Interfaz**: Empty states informativos, loading states, error recovery
- **Configuración**: Persistencia de velocidad y tamaño de fuente predeterminados
- **Localización**: Interfaz en español mejorada para mejor accesibilidad
- **UI Responsiva**: Todos los elementos se adaptan correctamente sin texto cortado

### 📱 **Soporte Multi-Orientación (Optimizado Diciembre 2024)**
- **Teléfonos Horizontal**: Layout tradicional con controles en la parte inferior
- **Teléfonos Vertical**: Navegación por tabs tradicional con controles de teleprompter en la parte inferior
- **Tablets Horizontal**:
  - TopAppBar compacto (48dp vs 56dp estándar) para aprovechar espacio vertical
  - Tabs laterales optimizados (140dp ancho, maxLines=2, TextOverflow.Ellipsis)
  - Eliminación de padding excesivo en área de contenido
  - Layout de 2 columnas en configuraciones para mejor aprovechamiento del espacio
- **Tablets Vertical**: Panel de control lateral que no interfiere con la lectura del texto
- **Detección Automática**: Cambio de layout basado en orientación y tamaño de pantalla (>600dp para tablets)

### 🚧 **Parcialmente Implementadas**
- **Google Drive Integration**:
  - ✅ UI actualizada para mostrar "Google Drive"
  - ✅ Simulación de migración local → cloud
  - ❌ API real de Google Drive pendiente
  - ❌ Creación de archivos .txt en Drive pendiente

### 📋 **Pendientes de Implementar**
- **Google Drive API Real**: Implementación completa de upload/download de archivos .txt
- **Sincronización Bidireccional**: Detección de cambios entre dispositivos
- **Edición de Textos**: Funcionalidad para editar textos existentes
- **Búsqueda**: Filtrado de textos en la biblioteca
- **Exportar/Importar**: Funciones para backup y restore
- **Controles Verticales**: Sliders verticales personalizados para tablets en modo vertical

## 🏗️ **Arquitectura Técnica**

### **Patrón de Arquitectura**: MVVM (Model-View-ViewModel)
- **View**: Composables de Jetpack Compose con layouts adaptativos
- **ViewModel**: Gestión de estado con StateFlow
- **Model**: Data classes y repositorios

### **Tecnologías Core**
- **UI Framework**: Jetpack Compose + Material Design 3
- **Responsive Design**: LocalConfiguration para detección de orientación y tamaño
- **Arquitectura**: Android Architecture Components
- **Estado**: StateFlow + Coroutines
- **Navegación**: Navigation Compose + NavigationRail para tablets
- **Autenticación**: Firebase Auth + Google Sign-In
- **Almacenamiento Local**: SharedPreferences + Gson
- **Almacenamiento Cloud**: Firebase (migración a Google Drive en progreso)

### **Configuración de Java/JDK**
- **Java Version**: OpenJDK 21 (incluido con Android Studio)
- **JAVA_HOME**: `/Applications/Android Studio.app/Contents/jbr/Contents/Home`
- **Configuración**: `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`
- **Ventajas**: Usa la misma versión de Java que Android Studio, garantiza compatibilidad

### **Dependencias Principales**
```kotlin
// Core Android
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
implementation("androidx.navigation:navigation-compose:2.7.5")

// Firebase & Auth
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("androidx.credentials:credentials:1.2.0")
implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

// Storage
implementation("com.google.code.gson:gson:2.10.1")
// TODO: Google Drive API dependencies (comentadas temporalmente)
```

## 🔧 **Configuración del Proyecto**

### **Configuración de Orientación**
- **AndroidManifest**: `android:configChanges="orientation|screenSize|keyboardHidden"`
- **Layouts Adaptativos**: Detección automática con `LocalConfiguration`
- **Breakpoints**:
  - Tablets: `screenWidth > 600.dp`
  - Detección de orientación: `Configuration.ORIENTATION_LANDSCAPE`

### **Firebase Setup**
- **Proyecto**: `promptflow-55398`
- **Package Name**: `com.promptflow.android`
- **Web Client ID**: `421864875906-257v99qbn9v2sn2vud8edjjb1o92gkg7.apps.googleusercontent.com`
- **SHA-1 Debug**: `2F:10:AD:A8:9F:DC:F2:84:7D:49:C0:1E:5A:B2:71:C3:57:42:7E:B4`

### **Estructura de Archivos**
```
app/src/main/java/com/promptflow/android/
├── MainActivity.kt                 // Punto de entrada, navegación principal
├── ui/
│   ├── screen/
│   │   ├── TeleprompterScreen.kt  // Pantalla principal con layouts adaptativos
│   │   └── SettingsScreen.kt      // Configuración con NavigationRail y tabs
│   └── theme/
│       └── Theme.kt               // Material Design 3 theming
├── viewmodel/
│   ├── AuthenticationViewModel.kt // Gestión de Google Sign-In
│   └── TextLibraryViewModel.kt    // Gestión de textos y almacenamiento
└── google-services.json           // Configuración Firebase
```

### **Arquitectura de Layouts**
```kotlin
// Detección de configuración
val configuration = LocalConfiguration.current
val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
val screenWidth = configuration.screenWidthDp.dp
val isTablet = screenWidth > 600.dp

// Layouts adaptativos
if (isLandscape || !isTablet) {
    LandscapeLayout() // Teléfonos o landscape
} else {
    PortraitTabletLayout() // Tablets en vertical
}
```

### **Arquitectura de Datos**
```kotlin
data class SavedText(
    val id: String,
    val title: String,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date,
    val userId: String,
    val isLocal: Boolean,
    val driveFileId: String? = null
)

data class AuthState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
    val isSignedIn: Boolean = false
)

data class TextLibraryState(
    val isLoading: Boolean = false,
    val savedTexts: List<SavedText> = emptyList(),
    val localTexts: List<SavedText> = emptyList(),
    val error: String? = null
)
```

## 🚀 **Estado Actual de la Aplicación**

### **Experiencia Multi-Dispositivo**

#### **Teléfonos en Vertical**
1. Navegación por tabs en la parte inferior
2. Teleprompter con controles en la parte inferior
3. Texto alineado al centro para mejor lectura

#### **Teléfonos en Horizontal**
1. Teleprompter en pantalla completa
2. Controles flotantes que se ocultan automáticamente
3. Experiencia inmersiva para presentaciones

#### **Tablets en Vertical**
1. **Teleprompter**: Panel de control lateral (120dp) que no bloquea el texto
2. **Configuraciones**: NavigationRail para navegación eficiente
3. **Editor de Texto**: Área más grande con mejor espaciado de líneas
4. **Texto**: Alineado a la izquierda para mejor lectura en vertical

#### **Tablets en Horizontal**
1. **Teleprompter**: Similar a teléfonos pero optimizado para pantallas grandes
2. **Configuraciones**: NavigationRail para aprovechar el espacio horizontal
3. **Controles**: Mejor espaciado y tamaños de botones optimizados

### **Flujo de Usuario Sin Login**
1. Usuario abre la app → TeleprompterScreen adaptativo según orientación
2. Usuario va a Settings → Layout adaptativo (tabs o NavigationRail)
3. Usuario va a Library → Ve "Almacenamiento Local", puede guardar textos
4. **Rotación**: La app adapta automáticamente el layout sin perder estado

### **Flujo de Usuario Con Login**
1. Usuario va a Account Tab → Interfaz mejorada con Google branding
2. Usuario hace clic en "Continuar con Google" → Autenticación exitosa
3. UI cambia a mostrar "Google Drive" en lugar de "Almacenamiento Local"
4. **Orientación**: Todos los layouts se adaptan manteniendo la funcionalidad cloud

### **Manejo de Errores Implementado**
- **Sin cuentas Google**: Mensaje detallado con pasos para configurar cuenta
- **Conexión cancelada**: Mensaje amigable para reintentar
- **Errores de red**: Verificación de conectividad con opciones de retry
- **Estados de carga**: Indicadores visuales durante operaciones async
- **Rotación**: Manejo de errores consistente en todas las orientaciones

## 📊 **Debugging y Logging**

### **Sistema de Debug Implementado**
```kotlin
// Emojis para fácil filtrado en Android Studio Logcat
println("🔵 BUTTON CLICKED! Starting Google Sign-In process...")
println("🔍 CredentialManager created successfully")
println("✅ Text saved to Google Drive: $title")
println("❌ Error saving to Drive: ${e.message}")
println("📱 Layout: ${if (isTablet) "Tablet" else "Phone"} - ${if (isLandscape) "Landscape" else "Portrait"}")
```

### **Filtros de Logcat Recomendados**
- `PromptFlow` - Todos los logs de la app
- `🔵|🔍|✅|❌|📱` - Solo logs importantes con emojis
- `Layout|Orientation` - Logs específicos de layouts adaptativos

### **Compilación y Build**
```bash
# Configurar Java (Android Studio JBR)
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"

# Verificar configuración
java -version  # Debería mostrar OpenJDK 21

# Compilar proyecto
./gradlew build

# Ejecutar en dispositivo
./gradlew installDebug
```

## 🎯 **Próximos Pasos Prioritarios**

### **1. Implementación Real de Google Drive (Alta Prioridad)**
```kotlin
// TODO: Implementar en TextLibraryViewModel.kt
- OAuth 2.0 token refresh flow
- REST API calls para crear carpeta "PromptFlow"
- Upload de archivos .txt usando multipart/form-data
- Download y parsing de archivos de Drive
- Sincronización bidireccional
```

### **2. Mejoras de UX Multi-Orientación (Media Prioridad)**
```kotlin
// TODO: Funcionalidades específicas para orientación
- Sliders verticales personalizados para tablets en vertical
- Gestos de navegación específicos para cada layout
- Transiciones animadas entre orientaciones
- Configuración de preferencias de layout por dispositivo
```

### **3. Optimizaciones (Baja Prioridad)**
```kotlin
// TODO: Performance y polish
- Lazy loading para bibliotecas grandes
- Offline-first architecture mejorada
- Background sync
- Push notifications para cambios
- Sharing entre usuarios
- Analytics de uso por orientación
```

## 🔐 **Configuración de Seguridad**

### **Firebase Security Rules** (Futuro)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/texts/{document=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 📱 **Compatibilidad de Dispositivos**

### **Tamaños de Pantalla Soportados**
- **Pequeño (< 600dp)**: Layout de teléfono
- **Mediano (600-840dp)**: Layout de tablet con controles adaptativos
- **Grande (> 840dp)**: Experiencia completa de tablet

### **Orientaciones Soportadas**
- ✅ **Retrato en Teléfono**: Tabs + controles inferiores
- ✅ **Paisaje en Teléfono**: Pantalla completa + controles flotantes
- ✅ **Retrato en Tablet**: Panel lateral + NavigationRail
- ✅ **Paisaje en Tablet**: NavigationRail + controles optimizados

### **Resoluciones Testadas**
- **Teléfonos**: 360x640dp, 411x731dp, 375x667dp
- **Tablets**: 600x960dp, 768x1024dp, 820x1180dp

## 🏆 **Logros Técnicos**

### **Responsive Design**
- Detección automática de orientación y tamaño de pantalla
- Layouts completamente adaptativos sin duplicación de código
- Transiciones suaves entre orientaciones sin pérdida de estado

### **UX Optimizada**
- Controles posicionados óptimamente para cada orientación
- Texto alineado según el contexto (centro para landscape, izquierda para portrait)
- Navegación intuitiva adaptada al tamaño de pantalla

### **Arquitectura Escalable**
- Componentes modulares reutilizables
- Lógica de layout centralizada y mantenible
- Preparado para futuras mejoras específicas de orientación

## 📝 **Notas de Desarrollo**

### **Comandos Útiles**
```bash
# Compilar y buildear
./gradlew build

# Obtener SHA-1 para Firebase
./gradlew signingReport

# Limpiar build cache
./gradlew clean

# Instalar en dispositivo/emulador
./gradlew installDebug
```

### **Archivos Críticos No Commitear**
- `app/google-services.json` (contiene claves)
- `local.properties` (rutas locales)
- `.env` files (si se agregan)

### **URLs de Referencia**
- [Firebase Console](https://console.firebase.google.com/project/promptflow-55398)
- [Google Drive API Docs](https://developers.google.com/drive/api/v3/reference)
- [Material Design 3](https://m3.material.io/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

**Última actualización**: 6 de junio, 2025
**Estado**: ✅ Compilando, 🚧 Drive integration pendiente
**Próxima prioridad**: Implementar Google Drive API real