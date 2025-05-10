# WorldCurrency - Android Currency Converter App

WorldCurrency es una aplicación Android desarrollada en Kotlin que permite convertir montos entre diferentes monedas en tiempo real, consultar el historial de conversiones y ver detalles de cada transacción.

## Características

- Ingreso de monto con validación y soporte de hasta 2 decimales.
- Selección de moneda origen y destino con búsqueda filtrada.
- Conversión de divisas en tiempo real utilizando una API externa.
- Almacenamiento local del historial de conversiones.
- Pantalla de historial con detalle de cada conversión.
- Navegación intuitiva entre pantallas usando Jetpack Navigation.


##  Tecnologías y Arquitectura

- **Lenguaje**: Kotlin
- **Arquitectura**: MVVM + Clean Architecture
- **UI**: Jetpack Compose + Material 3
- **Base de datos local**: Room
- **Consumo de red**: Retrofit + Kotlinx Serialization
- **DI**: Hilt
- **Estados**: StateFlow

- a Mejorar:

  Historial por fecha	Mostrar agrupado por día / semana o con filtro temporal
  Undo al borrar	Snackbar con "deshacer" al eliminar una conversión
  Comparativa de tasas	Mostrar gráfica simple del cambio de valor en el tiempo (línea de tendencia)
  Tests unitarios + UI	Pruebas de ViewModel, UseCases y UI con Compose Testing y MockK
  Internacionalización (i18n)	Soporte multilenguaje (strings.xml por idioma)
  CI/CD	Automatizar builds, pruebas y releases con GitHub Actions o Bitrise
  Crash reporting	Integrar Firebase Crashlytics o Sentry
  Analytics	Agregar seguimiento de eventos (conversión, uso de historial)
  Añadir login básico para historial personalizado
  Reemplazar API pública por propia para control total
  Consultar valores del días anteriores
  Graficas con valores entre fechas
