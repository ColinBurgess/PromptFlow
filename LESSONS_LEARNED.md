# Lessons Learned: Android Compose Development

## 📋 CHECKLIST OBLIGATORIO ANTES DE COMPILAR

### ✅ **IMPORTS - Verificar SIEMPRE antes de edit_file**
- [ ] `import androidx.compose.foundation.clickable` - para funciones clickable
- [ ] `import androidx.compose.ui.unit.sp` - para tamaños de fuente
- [ ] `import androidx.compose.ui.platform.LocalContext` - para contexto Android
- [ ] `import androidx.compose.ui.platform.LocalConfiguration` - para detección de orientación
- [ ] `import androidx.compose.ui.text.style.TextOverflow` - para texto cortado
- [ ] `import androidx.compose.ui.text.style.TextAlign` - para alineación de texto
- [ ] Verificar que TODOS los Icons.Default.* estén disponibles

### ✅ **FUNCIONES @Composable - Reglas Críticas**
- [ ] **NUNCA** llamar `LocalContext.current` dentro de onClick lambda
- [ ] **SIEMPRE** capturar `LocalContext.current` en el nivel @Composable: `val context = LocalContext.current`
- [ ] **NUNCA** llamar funciones @Composable desde callbacks no-composable
- [ ] Verificar que todas las funciones helper (@Composable) estén definidas ANTES de usarlas

### ✅ **PARÁMETROS DE FUNCIÓN - Consistencia Vital**
- [ ] Verificar que los parámetros de función coincidan con las llamadas
- [ ] **AuthState + AuthViewModel** siempre van juntos para pantallas de autenticación
- [ ] **TextLibraryState + TextLibraryViewModel** siempre van juntos para biblioteca
- [ ] NO mezclar parámetros de diferentes funcionalidades

### ✅ **TEXTO Y LAYOUT - Nuevas Reglas Críticas**
- [ ] **NUNCA** usar `maxLines = 1` para texto que puede ser largo
- [ ] **SIEMPRE** incluir `TextOverflow.Ellipsis` para texto que puede cortarse
- [ ] **CONSIDERAR** `maxLines = 2` para labels en espacios pequeños
- [ ] **VERIFICAR** dimensiones reales antes de hardcodear tamaños

### ✅ **EDIT_FILE - Instrucciones Precisas**
- [ ] Ser EXTREMADAMENTE específico sobre qué parámetros cambiar
- [ ] Incluir el tipo completo: `authState: com.promptflow.android.viewmodel.AuthState`
- [ ] Verificar que la función target tenga los parámetros correctos ANTES del edit
- [ ] Si hay duda, leer la función completa primero con read_file

## 🚨 ERRORES ESPECÍFICOS COMETIDOS EN ESTA CONVERSACIÓN

### **Error #1: Import faltante**
```kotlin
// ❌ ERROR: Usé clickable sin import
.clickable { onTabSelected(index) }

// ✅ SOLUCIÓN: Siempre agregar import ANTES de usar
import androidx.compose.foundation.clickable
```

### **Error #2: LocalContext en lugar incorrecto**
```kotlin
// ❌ ERROR: LocalContext.current en onClick
onClick = { authViewModel.signInWithGoogle(LocalContext.current) }

// ✅ SOLUCIÓN: Capturar context en nivel @Composable
@Composable
private fun MyFunction() {
    val context = LocalContext.current
    // ...
    onClick = { authViewModel.signInWithGoogle(context) }
}
```

### **Error #3: Parámetros inconsistentes**
```kotlin
// ❌ ERROR: Función definida con parámetros incorrectos
private fun AccountTabHorizontal(
    user: FirebaseUser?,
    textLibraryState: TextLibraryState, // ❌ Parámetros incorrectos
    // ...
)

// ✅ SOLUCIÓN: Verificar parámetros necesarios ANTES de edit
private fun AccountTabHorizontal(
    user: FirebaseUser?,
    authState: AuthState, // ✅ Parámetros correctos
    authViewModel: AuthenticationViewModel,
    // ...
)
```

### **Error #4: Función no definida**
```kotlin
// ❌ ERROR: Usar BenefitItem sin verificar si existe
BenefitItem(Icons.Default.Cloud, "título", "descripción")

// ✅ SOLUCIÓN: Verificar con grep_search si la función existe ANTES de usarla
```

### **Error #5: Texto cortado por maxLines = 1**
```kotlin
// ❌ ERROR: Texto largo cortado con maxLines = 1
Text(
    text = "Biblioteca", // Se corta
    maxLines = 1
)

// ✅ SOLUCIÓN: Usar maxLines = 2 + TextOverflow
Text(
    text = "Biblioteca",
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
    textAlign = TextAlign.Center
)
```

### **Error #6: Espacios en blanco por padding excesivo**
```kotlin
// ❌ ERROR: Padding excesivo desperdicia espacio
Column(
    modifier = Modifier.padding(20.dp) // Demasiado
)

// ✅ SOLUCIÓN: Optimizar padding según el contexto
Column(
    modifier = Modifier.padding(8.dp) // Optimizado
)
```

### **Error #7: Múltiples intentos fallidos en mismo problema**
```kotlin
// ❌ PATRÓN DE ERROR: Cambiar dimensiones sin atacar raíz
.width(100.dp) -> .width(120.dp) -> .width(140.dp) // Pero seguía cortándose

// ✅ SOLUCIÓN: Identificar la causa raíz PRIMERO
// El problema era maxLines = 1, no el ancho
```

### **Error #8: API incorrecta para `LazyListState` (uso de `animateScrollBy`)**
```kotlin
// ❌ ERROR: Se intentó usar animateScrollBy, que no existe en LazyListState
// listState.animateScrollBy(value = scrollIncrement, ...) // Error de compilación

// ✅ SOLUCIÓN: Usar listState.scroll { scrollBy(value) } para LazyListState
LaunchedEffect(isPlaying, speed) {
    if (isPlaying) {
        while (isPlaying) {
            // ...
            try {
                listState.scroll { // Función suspendida
                    scrollBy(scrollIncrement) // Desplaza por píxeles
                }
            } catch (e: Exception) {
                // ...
            }
            // ...
        }
    }
}
```
**Lección Clave:** Siempre verificar la documentación o los métodos disponibles para el tipo específico de `State` de Compose que se está utilizando (ej. `LazyListState` vs. `ScrollState`), ya que sus APIs para control programático pueden diferir significativamente.

## 📖 REGLAS FUNDAMENTALES

### **REGLA #1: VERIFICACIÓN PREVIA OBLIGATORIA**
**ANTES** de cualquier edit_file que agregue funcionalidad:
1. `grep_search` para verificar imports existentes
2. `grep_search` para verificar funciones helper existentes
3. `read_file` para verificar parámetros de función target

### **REGLA #2: COMPILACIÓN INCREMENTAL**
- **NUNCA** hacer cambios masivos en un solo edit
- Hacer UN cambio, compilar, siguiente cambio
- Si el change es grande, dividir en pasos pequeños

### **REGLA #3: IDENTIFICAR CAUSA RAÍZ**
- **NUNCA** asumir que el problema es el que parece obvio
- **SIEMPRE** leer el código para entender la verdadera causa
- **NO** hacer múltiples intentos ciegos de la misma "solución"

### **REGLA #4: PATTERNS ANDROID COMPOSE**
```kotlin
// ✅ PATTERN CORRECTO para contexto
@Composable
fun MyScreen() {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    // Usar context y configuration después
}

// ✅ PATTERN CORRECTO para texto que puede cortarse
Text(
    text = textoPotencialmenteLargo,
    maxLines = 2,
    overflow = TextOverflow.Ellipsis,
    textAlign = TextAlign.Center
)

// ✅ PATTERN CORRECTO para auth
@Composable
fun AuthRelatedComponent(
    authState: AuthState,           // Siempre juntos
    authViewModel: AuthenticationViewModel,
    onLoginRequest: () -> Unit,
    onLogoutRequest: () -> Unit
)

// ✅ PATTERN CORRECTO para library
@Composable
fun LibraryRelatedComponent(
    textLibraryState: TextLibraryState,    // Siempre juntos
    textLibraryViewModel: TextLibraryViewModel,
    onTextSelected: (String) -> Unit,
    onDeleteText: (SavedText) -> Unit
)
```

### **REGLA #5: IMPORTS CRÍTICOS COMUNES**
```kotlin
// Para UI básico
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Para texto y layout
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

// Para contexto Android
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration

// Para navegación y estado
import androidx.lifecycle.viewmodel.compose.viewModel
```

### **REGLA #6: DEBUGGING EFICIENTE**
- Si compilation falla con "Unresolved reference": import faltante
- Si falla con "@Composable invocations": contexto @Composable incorrecto
- Si falla con parámetros: verificar signature de función
- Si texto se corta: verificar maxLines y overflow
- Si hay espacios en blanco: verificar padding excesivo

## 🎯 CHECKLIST ANTES DE CADA EDIT_FILE

1. **¿Necesito imports nuevos?** → grep_search para verificar
2. **¿Estoy usando funciones helper?** → grep_search para verificar existencia
3. **¿Estoy cambiando parámetros de función?** → read_file para verificar signature actual
4. **¿Estoy usando LocalContext/LocalConfiguration?** → Verificar que esté en nivel @Composable
5. **¿Es un cambio grande?** → Dividir en cambios más pequeños
6. **¿Hay texto que puede cortarse?** → Verificar maxLines y overflow
7. **¿Estoy optimizando layout?** → Verificar padding y dimensiones

## 🔧 COMANDOS ÚTILES PARA VERIFICACIÓN

```bash
# Verificar imports faltantes
grep_search "import androidx.compose.foundation.clickable"
grep_search "import androidx.compose.ui.text.style.TextOverflow"

# Verificar funciones existentes
grep_search "private fun BenefitItem"

# Verificar signature de función antes de cambiar
read_file líneas X-Y para ver parámetros actuales

# Compilar frecuentemente
./gradlew build (cada cambio pequeño)
```

## 💡 PRINCIPIOS MENTALES

1. **"Verificar antes de editar"** - Nunca asumir que algo existe
2. **"Un cambio, una compilación"** - Cambios incrementales siempre
3. **"Leer antes de escribir"** - Entender el estado actual antes de modificar
4. **"Contexts son sagrados"** - LocalContext solo en nivel @Composable
5. **"Parámetros son contratos"** - Verificar signature antes de cambiar llamadas
6. **"Identificar causa raíz"** - No hacer intentos ciegos múltiples
7. **"Texto puede cortarse"** - Siempre considerar maxLines y overflow

## 🏆 CASOS DE ÉXITO RECIENTES

### **✅ Optimización Layout Horizontal (Diciembre 2024)**
**Problema**: TopAppBar muy grande, espacios en blanco, tabs laterales con texto cortado

**Solución Exitosa**:
- TopAppBar adaptativo: 48dp para tablet horizontal vs estándar para portrait
- Tabs laterales optimizados: 140dp ancho, maxLines=2, TextOverflow.Ellipsis
- Padding optimizado: Eliminación de espacios innecesarios

**Lección**: La causa raíz del texto cortado era `maxLines = 1`, no el ancho insuficiente

---

**ÚLTIMA REGLA: Si tengo duda, prefiero hacer una verificación extra que una compilación fallida.**