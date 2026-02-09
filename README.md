# 🏗️ The Master Builder AR

[**Español**](#descripción-es) | [**English**](#description-en)

---

<a name="descripción-es"></a>
## 🇪🇸 Descripción (Español)

**The Master Builder AR** es una aplicación nativa para Android diseñada para arquitectos e ingenieros AEC que buscan llevar el modelado BIM al campo de batalla. Optimizada específicamente para el **Samsung S23**, la app transforma el dispositivo en una herramienta de escaneo y validación MEP (Mecánica, Eléctrica y Plomería) de alta precisión.

### 🚀 Características Principales
- **Lógica Espacial MEP (Offline)**: Escaneo y modelado sin conexión mediante el ARCore Depth API para generación de mallas en tiempo real.
- **Motor de Alturas Estándar**: Base de datos local que valida las alturas de instalación según la habitación:
    - **Cocina**: Contactos (1.10m - 1.20m), Refrigerador (0.60m).
    - **Baño**: Toma de agua WC (0.20m), Lavabo (0.55m - 0.60m).
    - **General**: Apagadores (1.10m - 1.20m), Contactos generales (0.30m - 0.40m).
- **Detalle Técnico de Instalaciones**:
    - **Hidráulica**: Soporte para agua fría (Azul) y caliente (Rojo) con shaders de flujo animado.
    - **Eléctrica**: Cableado por capas (Fase, Neutro, Tierra) y soporte para conductos corrugados de 3/8", 1/2" y 3/4".
- **Inventario y BOM en Tiempo Real**: Seguimiento de cada codo, tee, metro de tubería y cajas eléctricas (chalupas) colocadas en la escena.
- **Modo Feedback (Híbrido)**: Auditoría técnica mediante IA (Gemini Pro) que analiza los datos locales para detectar errores (ej. cruce de fase/neutro) y optimizar rutas.

### 📱 Optimización Samsung S23
Diseñada para aprovechar la tasa de refresco de **120Hz** y la fusión de sensores de alta precisión (giroscopio/acelerómetro), garantizando una persistencia de objetos AR sin latencia.

---

<a name="description-en"></a>
## 🇺🇸 Description (English)

**The Master Builder AR** is a native Android application designed for AEC architects and engineers looking to bring BIM modeling to the field. Specifically optimized for the **Samsung S23**, the app transforms the device into a high-precision MEP (Mechanical, Electrical, and Plumbing) scanning and validation tool.

### 🚀 Core Features
- **Spatial MEP Logic (Offline)**: Connectivity-free scanning and modeling using ARCore Depth API for real-time mesh generation.
- **Standard Heights Engine**: Local database for validating installation heights based on room type:
    - **Kitchen**: Countertop outlets (1.10m - 1.20m), Fridge (0.60m).
    - **Bathroom**: WC water inlet (0.20m), Sink (0.55m - 0.60m).
    - **General**: Switches (1.10m - 1.20m), General outlets (0.30m - 0.40m).
- **Advanced Technical Details**:
    - **Hydraulic**: Support for Cold (Blue) and Hot (Red) water with animated flow shaders.
    - **Electrical**: Layered wiring (Phase, Neutral, Ground) and support for 3/8", 1/2", and 3/4" corrugated conduits.
- **Real-time Inventory & BOM**: Tracking of every elbow, tee, pipe meter, and electrical box placed in the AR scene.
- **Feedback Mode (Hybrid)**: Technical audit via AI (Gemini Pro) that analyzes local data to detect errors (e.g., phase/neutral swap) and optimize routing.

### 📱 Samsung S23 Optimization
Engineered to leverage the **120Hz** refresh rate and high-precision sensor fusion (gyroscope/accelerometer), ensuring latency-free AR object persistence.

---

## 🛠️ Instalación / Installation

1. **Requisitos / Requirements**: Android 8.0+, ARCore compatible device (Samsung S23 recommended).
2. **Build**: 
   ```bash
   git clone https://github.com/brauliofv/The-Master-Builder-AR.git
   ```
   Abre en **Android Studio Hedgehog+** / Open in **Android Studio Hedgehog+**.

---

## 🗺️ Roadmap
- [ ] Multi-user Cloud Anchors (Colaboración en tiempo real).
- [ ] Soporte para escaneo Lidar (iPhone/iPad port logic).
- [ ] Exportación directa a archivos `.rvt` o `.ifc`.

---

## 💖 Soporte / Support

Si este proyecto te ha sido útil, considera apoyar su desarrollo continuo. / If this project helped you, consider supporting its continued development.

[![Support on Ko-fi](https://img.shields.io/badge/Support%20on-Ko--fi-F16061?style=for-the-badge&logo=ko-fi&logoColor=white)](https://ko-fi.com/brauliofv)

---

## 📜 Licencia y Créditos / License & Credits

Este proyecto está bajo la licencia **MIT**. / This project is licensed under **MIT**.

**Importante / Important**: Cualquier uso o demostración de esta aplicación debe otorgar crédito claro al creador original: **BraulioFV**. / Any use or demonstration of this application must provide clear credit to the original creator: **BraulioFV**.

---

*Desarrollado con ❤️ para la construcción del futuro. / Developed with ❤️ for the construction of the future.*
