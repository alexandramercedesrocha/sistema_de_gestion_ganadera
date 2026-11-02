# Sistema de Gestión y Comercialización Ganadera
**Sociedad Wilber Rocha (50%) & Yader Altamirano (50%)**  
*Plataforma Web desarrollada con Spring Boot 3, Thymeleaf y MySQL*

---

## 🚀 Requisitos Previos

1. **Java JDK 17 o 21** instalado en el sistema.
2. **MySQL / MariaDB** (ej. XAMPP o MySQL Server).

---

## 🛠️ Configuración Rápida en 1 Clic (Windows)

1. Abre tu panel de control de **XAMPP** y dale **Start a MySQL** (puerto 3306).
2. Si es la primera vez o tu base de datos está vacía, dale doble clic al archivo:
   ```cmd
   importar_base_datos.bat
   ```
   *Esto creará la base de datos `sgg` con sus 17 tablas normalizadas en 1 segundo.*
3. Inicia la aplicación con:
   ```cmd
   iniciar.bat
   ```
4. Abre tu navegador en: [http://localhost:8080](http://localhost:8080).

---

## ⚙️ ¿Tienes una configuración distinta de MySQL? (Contraseña o Puerto diferente)

El proyecto está diseñado para funcionar con cualquier configuración sin alterar el código compartido:

* **Por defecto (XAMPP):** `localhost:3306`, usuario `root`, sin contraseña.
* **Si usas contraseña o puerto diferente:** Puedes definir las siguientes variables de entorno o crear un archivo `application-local.properties` (guiándote de `application-local.properties.example`):
  * `DB_HOST`: Host de MySQL (default: `localhost`)
  * `DB_PORT`: Puerto de MySQL (default: `3306`)
  * `DB_NAME`: Nombre de la base de datos (default: `sgg`)
  * `DB_USER`: Usuario de base de datos (default: `root`)
  * `DB_PASSWORD`: Contraseña de tu MySQL (default: vacío)

---

## 📱 Diseño Responsivo y Separación CSS / HTML
* Todo el diseño estilístico reside en `src/main/resources/static/css/styles.css`.
* Las interfaces están adaptadas para teléfonos móviles (360px - 480px), tablets y pantallas de escritorio.
