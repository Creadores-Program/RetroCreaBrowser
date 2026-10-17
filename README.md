# RetroCreaBrowser 🌐📱

**RetroCreaBrowser** es un navegador web experimental y ligero diseñado para revivir dispositivos antiguos con **Android 2.1 hasta Android 7.1**. 
A diferencia de otros navegadores, **RetroCreaBrowser** implementa un sistema de parcheo integral sobre el WebView del sistema que intercepta y adapta código moderno (JavaScript, módulos, HTML5, CSS y Polyfills) para hacerlo interpretable por motores web vetustos, todo ello manteniendo un consumo de recursos mucho menor que alternativas pesadas como Chrome 81.
---
## 🛠️ ¿Cómo Funciona el Parcheo?
El motor de RetroCreaBrowser no se limita a la capa de red; transforma el contenido web en tiempo real:
* **Parcheo de Sintaxis y JS:** Convierte sintaxis moderna de JavaScript y maneja compatibilidad para `<script type="module">`.
* **Inyección de Polyfills:** Añade automáticamente métodos y APIs modernas faltantes en WebViews antiguos.
* **Compatibilidad HTML y CSS:** Adapta etiquetas y propiedades CSS modernas para evitar que rompan el renderizado en motores AOSP antiguos.
* **Soporte TLS 1.2 / 1.3:**
  * **Android 2.3+:** Soporte nativo/parcheado de TLS 1.2 / 1.3 en la capa de red.
  * **Android 2.1 - 2.2:** Permite habilitar soporte para TLS 1.2 / 1.3 integrando [Wolfius](https://github.com/gohoski/Wolfius).
> ⚠️ **Estado del Proyecto y Advertencia de Uso:**
> El parcheo es **experimental e inestable**. En ocasiones, ciertas reglas de parcheo se ignoran o fallan según la estructura del sitio web. **No todas las páginas funcionarán mágicamente**; el objetivo del proyecto es lograr que la web sea *suficientemente funcional* para tareas esenciales en hardware retro, no garantizar compatibilidad perfecta al 100%.
---
## 📊 Comparativa de Rendimiento y Compatibilidad

| Característica / Navegador | Navegador AOSP Nativo | Chrome 81 (Android 4.1+) | RetroCreaBrowser |
| :--- | :--- | :--- | :--- |
| **Consumo de Memoria / RAM** | Muy Bajo | Muy Alto (Lento / Crashes) | **Optimizado / Ligero** |
| **Soporte TLS 1.2 / 1.3** | Obsoleto | Sí | **Sí (2.3+ y 2.1-2.2 vía Wolfius)** |
| **Parcheo JS / CSS / Polyfills** | No | No (Solo soporte nativo) | **Sí (En tiempo de ejecución)** |
| **Estabilidad en HW Antiguo** | Rota en web moderna | Cuelgues por falta de RAM | **Aceptable / Funcional** |

---
## ⚙️ Personalización e Interfaz
RetroCreaBrowser incluye una interfaz rápida para cambiar el tema visual de la barra de la app directamente desde el inicio:
* **Oscuro (Por defecto)**
* **Verde Android**
* **Azul Retro**
* **Rojo Carmín**
* **Blanco**
Además, cuenta con accesos rápidos a **Marcadores**, **Historial**, **Extensiones** y **Opciones avanzadas**.
---
## 📥 Instalación
1. Descarga la última versión en formato `.apk` desde la sección de **Releases**.
2. Si usas **Android 2.1 - 2.2**, consulta la documentación de integración con [Wolfius](https://github.com/gohoski/Wolfius) para habilitar cifrado TLS moderno.
3. Asegúrate de permitir la instalación desde "Fuentes desconocidas" en los ajustes de tu dispositivo.
4. Instala y ejecuta la aplicación.
---
## 🤝 Contribuir
¡Toda ayuda es bienvenida! Si quieres aportar parches de sintaxis JS, mejorar la inyección de polyfills, corregir bugs o probar sitios web en hardware real, puedes abrir un *Issue* o enviar un *Pull Request*.
---
## 📄 Licencia
Este proyecto está bajo la licencia [MIT](LICENSE).
