<div align="center">

# <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f0cf.svg" width="30"/> Proyecto Android - Baraja de Cartas <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/2663.svg" width="30"/>

**Aplicación desarrollada en Kotlin + Jetpack Compose**  
Integrando la API pública [Deck of Cards API](https://deckofcardsapi.com)

</div>

---

## <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f4d6.svg" width="25"/> Descripción

Esta app Android demuestra el consumo de una API REST en tiempo real utilizando **Retrofit** y **StateFlow** con arquitectura limpia.

---

## <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f4f1.svg" width="25"/> Capturas de Pantalla

<div align="center">
  <img src="assets/deck1.png" alt="Inicio" width="250" style="margin-right:12px;border-radius:8px;"/>
  <img src="assets/deck2.png" alt="Baraja" width="250" style="border-radius:8px;"/>
</div>

---

## <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f3d7.svg" width="25"/> Arquitectura

```
UI (Compose)
   ↓
ViewModel (DeckViewModel)
   ↓
Repository (DeckRepository)
   ↓
Retrofit → Deck of Cards API
```

---

## <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f4e6.svg" width="25"/> Dependencias

| Tipo | Librería | Descripción |
|------|----------|-------------|
| <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f9e0.svg" width="20"/> Arquitectura | `androidx.lifecycle.viewmodel.ktx` | Soporte ViewModel con Kotlin coroutines |
| <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f50c.svg" width="20"/> Networking | `retrofit2, okhttp3, gson` | API REST |
| <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f3a8.svg" width="20"/> UI | `androidx.compose.material3, coil-compose` | Interfaz moderna y carga de imágenes |
| <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f9ea.svg" width="20"/> Test | `strikt, kotlinx-coroutines-test` | Pruebas unitarias declarativas |

---

## <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f465.svg" width="25"/> Equipo de Desarrollo

<table>
  <tr>
    <th>Foto</th>
    <th>Nombre</th>
    <th>Código</th>
    <th>Rol</th>
  </tr>
  <tr>
    <td><img src="assets/mariana.png" width="80" style="border-radius:50%;"/></td>
    <td>Mariana Osorio</td>
    <td><code>MO-001</code></td>
    <td><img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f3a8.svg" width="18"/> UI / Compose</td>
  </tr>
  <tr>
    <td><img src="assets/david.png" width="80" style="border-radius:50%;"/></td>
    <td>David Mantilla</td>
    <td><code>DM-002</code></td>
    <td><img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f4bb.svg" width="18"/> Arquitectura / ViewModel / Tests</td>
  </tr>
  <tr>
    <td><img src="assets/jhovanny.png" width="80" style="border-radius:50%;"/></td>
    <td>Jhovanny Quiceno</td>
    <td><code>JQ-003</code></td>
    <td><img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f50c.svg" width="18"/> Networking / Retrofit</td>
  </tr>
</table>

---

## <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/2699.svg" width="25"/> Requisitos

- Android Studio Iguana o superior
- Kotlin 1.9+
- Gradle 8+
- Compose habilitado
- Permiso de internet

---

## <img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f9ea.svg" width="25"/> Ejemplo de Test

```kotlin
@Test
fun `viewModel initializes with default empty state`() {
    val vm = DeckViewModel()
    expectThat(vm.deckState.value).isNull()
    expectThat(vm.cardsState.value).isEmpty()
}
```

---

<div align="center">

<img src="https://cdnjs.cloudflare.com/ajax/libs/twemoji/14.0.2/svg/1f393.svg" width="25"/> **Proyecto académico - Universidad EAM © 2025**

</div>
