# PromptFlow - Contexto del Proyecto

## 📱 **Descripción del Proyecto**

PromptFlow es una aplicación Android de teleprompter profesional con sincronización en la nube. Permite a los usuarios crear, editar y visualizar textos que se desplazan automáticamente en pantalla, ideal para presentaciones, grabaciones de video y discursos.

## 🎯 **Características Principales**

### ✅ **Implementadas y Funcionando**
- **Teleprompter Core**: Desplazamiento suave de texto con controles de velocidad (1x-25x) y tamaño de fuente (16sp-48sp)
- **Interfaz de Usuario**: Diseño moderno con Material Design 3, tabs para diferentes secciones
- **Autenticación Google**: Integración completa con Firebase Auth y Google Sign-In
- **Almacenamiento Local**: Sistema robusto de guardado/carga de textos usando SharedPreferences + Gson
- **Biblioteca de Textos**: Gestión completa (crear, editar, eliminar, seleccionar textos)
- **Manejo de Errores**: Mensajes amigables para usuario con debugging completo
- **Estados de Interfaz**: Empty states informativos, loading states, error recovery
- **Configuración**: Persistencia de velocidad y tamaño de fuente predeterminados

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

## 🏗️ **Arquitectura Técnica**

### **Patrón de Arquitectura**: MVVM (Model-View-ViewModel)
- **View**: Composables de Jetpack Compose
- **ViewModel**: Gestión de estado con StateFlow
- **Model**: Data classes y repositorios

### **Tecnologías Core**
- **UI Framework**: Jetpack Compose + Material Design 3
- **Arquitectura**: Android Architecture Components
- **Estado**: StateFlow + Coroutines
- **Navegación**: Navigation Compose
- **Autenticación**: Firebase Auth + Google Sign-In
- **Almacenamiento Local**: SharedPreferences + Gson
- **Almacenamiento Cloud**: Firebase (migración a Google Drive en progreso)

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

### **Firebase Setup**
- **Proyecto**: `promptflow-55398`
- **Package Name**: `com.promptflow.android`
- **Web Client ID**: `***REMOVED***`
- **SHA-1 Debug**: `***REMOVED***`

### **Estructura de Archivos**
```
app/src/main/java/com/promptflow/android/
├── MainActivity.kt                 // Punto de entrada, navegación principal
├── ui/
│   ├── screen/
│   │   ├── TeleprompterScreen.kt  // Pantalla principal del teleprompter
│   │   └── SettingsScreen.kt      // Configuración, biblioteca, cuenta
│   └── theme/
│       └── Theme.kt               // Material Design 3 theming
├── viewmodel/
│   ├── AuthenticationViewModel.kt // Gestión de Google Sign-In
│   └── TextLibraryViewModel.kt    // Gestión de textos y almacenamiento
└── google-services.json           // Configuración Firebase
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

### **Flujo de Usuario Sin Login**
1. Usuario abre la app → TeleprompterScreen con texto de ejemplo
2. Usuario va a Settings → Puede editar texto, configurar velocidad/fuente
3. Usuario va a Library → Ve "Almacenamiento Local", puede guardar textos
4. Textos se guardan en SharedPreferences como JSON
5. Usuario puede seleccionar textos guardados para el teleprompter

### **Flujo de Usuario Con Login**
1. Usuario va a Account Tab → Ve interfaz mejorada con Google branding
2. Usuario hace clic en "Continue with Google" → Autenticación exitosa
3. UI cambia a mostrar "Google Drive" en lugar de "Almacenamiento Local"
4. Textos locales se "migran" automáticamente al estado cloud
5. Nuevos textos se muestran con icono ☁️ (cloud) en lugar de 📱 (local)
6. **Nota**: Actualmente los textos siguen guardándose localmente, Google Drive API pendiente

### **Manejo de Errores Implementado**
- **Sin cuentas Google**: Mensaje detallado con pasos para configurar cuenta
- **Conexión cancelada**: Mensaje amigable para reintentar
- **Errores de red**: Verificación de conectividad con opciones de retry
- **Estados de carga**: Indicadores visuales durante operaciones async

## 📊 **Debugging y Logging**

### **Sistema de Debug Implementado**
```kotlin
// Emojis para fácil filtrado en Android Studio Logcat
println("🔵 BUTTON CLICKED! Starting Google Sign-In process...")
println("🔍 CredentialManager created successfully")
println("✅ Text saved to Google Drive: $title")
println("❌ Error saving to Drive: ${e.message}")
```

### **Filtros de Logcat Recomendados**
- `PromptFlow` - Todos los logs de la app
- `🔵|🔍|✅|❌` - Solo logs importantes con emojis

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

### **2. Mejoras de UX (Media Prioridad)**
```kotlin
// TODO: Funcionalidades adicionales
- Edición in-place de textos existentes
- Búsqueda y filtrado en biblioteca
- Organización por carpetas/tags
- Export/import de textos
- Configuración avanzada de teleprompter
```

### **3. Optimizaciones (Baja Prioridad)**
```kotlin
// TODO: Performance y polish
- Lazy loading para bibliotecas grandes
- Offline-first architecture
- Background sync
- Push notifications para cambios
- Sharing entre usuarios
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

### **Google Drive Scopes Necesarios**
```kotlin
// Scope mínimo requerido
"https://www.googleapis.com/auth/drive.file"
// Solo archivos creados por la app
```

## 📱 **Testing y QA**

### **Casos de Prueba Críticos**
1. **Login Flow**: Sin cuenta → Error amigable → Configurar cuenta → Login exitoso
2. **Storage Flow**: Local → Login → Migración → Cloud storage
3. **Teleprompter Core**: Velocidades variables, tamaños de fuente, pausa/play
4. **Biblioteca**: Crear, seleccionar, eliminar textos
5. **Estados Edge**: Sin internet, logout durante sync, errores de Drive

### **Dispositivos de Prueba**
- ✅ Emulador Android API 34 con Google Play Services
- 📋 TODO: Dispositivos físicos con diferentes versiones Android
- 📋 TODO: Tablets y diferentes tamaños de pantalla

## 🎨 **Design System**

### **Colores y Tema**
- **Material Design 3** con dynamic theming
- **Google Blue** (`#4285F4`) para branding de Google
- **Error colors** para manejo de errores
- **Surface variants** para cards y elevación

### **Tipografía**
- **Headlines**: Para títulos de secciones
- **Body**: Para texto general
- **Display**: Para texto del teleprompter (escalable 16sp-48sp)

## 📊 **Métricas de Éxito**

### **KPIs Técnicos**
- ✅ App compila sin errores
- ✅ Google Sign-In funciona al 100%
- ✅ Almacenamiento local robusto
- 🚧 Google Drive integration (0% real, 100% UI)
- ✅ Error handling profesional

### **KPIs de Usuario**
- ✅ Teleprompter suave y responsivo
- ✅ Interfaz intuitiva y clara
- ✅ Onboarding sin fricción
- ✅ Recuperación de errores evidente

## 🔄 **Versionado**

### **v1.0.0 (Actual)**
- Core teleprompter functionality
- Local storage + Google Sign-In
- Material Design 3 UI

### **v1.1.0 (Próxima)**
- Real Google Drive integration
- Edit functionality
- Search and filtering

### **v2.0.0 (Futuro)**
- Multi-device sync
- Advanced teleprompter features
- Collaboration tools

---

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