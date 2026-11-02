# Sistema de Gestión y Comercialización Ganadera (SGC)
## Sociedad Comercial Wilber Rocha & Yader Altamirano

Bienvenido al repositorio oficial del **Sistema de Gestión y Comercialización de Ganado Bovino**, diseñado para automatizar y transparentar el ciclo operativo y financiero de la sociedad ganadera constituida por **Wilber Rocha (50%)** y **Yader Altamirano (50%)**.

---

## 1. Arquitectura del Sistema

El proyecto sigue una arquitectura en capas desacoplada bajo las mejores prácticas de la ingeniería de software con **Java 17+** y **Spring Boot 3.3.x**:

```text
com.ganaderia.rochaaltamirano
 ├── controller/        # Controladores MVC web anotados con @Controller
 │    ├── HomeController.java         (Dashboard ejecutivo y KPIs principales)
 │    ├── GanadoController.java       (Inventario de ganado, filtros y rechazos)
 │    ├── CompraController.java       (Registro de compras en pie a proveedores)
 │    ├── TransporteController.java   (Control de fletes, rutas y estado de pago)
 │    └── FinanzasController.java     (Liquidaciones, gastos, arqueos y reparto 50-50)
 │
 ├── service/           # Capa de lógica de negocio y cálculos financieros
 │    ├── GanadoService.java
 │    ├── CompraService.java
 │    ├── TransporteService.java
 │    ├── FinanzasService.java        (Cálculo del balance consolidado y cuotas 50-50)
 │    ├── ProveedorService.java
 │    └── MataderoService.java
 │
 ├── repository/        # Abstracción de acceso a datos en memoria (Mock Repositories)
 │    ├── GanadoRepository.java       (Almacén concurrente y datos de prueba)
 │    ├── CompraRepository.java
 │    ├── TransporteRepository.java
 │    ├── LiquidacionRepository.java
 │    ├── GastoRepository.java
 │    ├── ArqueoMensualRepository.java
 │    ├── ProveedorRepository.java
 │    └── MataderoRepository.java
 │
 ├── model/             # Entidades del dominio ganadero y modelo conceptual
 │    ├── Ganado.java                 (Superclase con PK arete, paletas, peso, estado)
 │    ├── GanadoRechazo.java          (Hereda de Ganado: motivo, fecha, peso matadero)
 │    ├── Proveedor.java
 │    ├── Matadero.java               (MACESA, SAN MARTÍN, etc.)
 │    ├── Compra.java
 │    ├── Entrega.java
 │    ├── Transporte.java             (Servicio de transporte y fletes)
 │    ├── Liquidacion.java            (Comprobantes de pago de mataderos)
 │    ├── ArqueoMensual.java          (Cierres contables mensuales)
 │    └── Gasto.java                  (Gastos de manejo y finca)
 │
 └── dto/               # Data Transfer Objects
      └── BalanceSociedadDTO.java     (Consolidado financiero y reparto 50-50)
```

---

## 2. Modelo de Dominio y Reglas de Negocio

### A. Herencia entre `Ganado` y `GanadoRechazo`
- **Ganado (`Ganado.java`)**: Es la entidad raíz para cada bovino identificado por su `arete` (Primary Key). Almacena sus `paletas` (identificador clave de edad por dentición), `color`, `estado` (`En Finca`, `Entregado a Matadero`), `pesoEntradaKg` y `categoria` (`Apto`, `Rechazo`).
- **Ganado de Rechazo (`GanadoRechazo.java`)**: Especialización directa que hereda todos los atributos biométricos del animal y agrega:
  - `motivoRechazo` (golpes en canal, problemas sanitarios, etc.)
  - `fechaRechazo`
  - `pesoMatadero` (con cálculo automático de merma de peso respecto al peso de entrada).

### B. Ciclo de Negocio y Logística
1. **Compras en Pie**: Registro de lotes a proveedores con cabezas, peso en báscula, precio por kilo o estándar y dirección de origen.
2. **Transporte / Fletes**: Control de viajes contratados a transportistas locales hacia la finca o plantas industriales (MACESA en Juigalpa, SAN MARTÍN en Nandaime) con trazabilidad de estado (`Pendiente`, `Pagado`).
3. **Entregas y Liquidaciones**: Registro de comprobantes fiscales emitidos por los mataderos tras el faenado.
4. **Gastos Operativos**: Insumos de finca (sal mineralizada, desparasitantes, vacunas, peones, mantenimiento de corrales y báscula).

### C. Fórmulas del Balance Financiero 50 - 50
$$\text{Total Egresos} = \text{Compras} + \text{Fletes de Transporte} + \text{Gastos Varios}$$
$$\text{Utilidad Neta} = \text{Total Liquidaciones Matadero} - \text{Total Egresos}$$
$$\text{Cuota Wilber Rocha} = \text{Utilidad Neta} \times 0.50$$
$$\text{Cuota Yader Altamirano} = \text{Utilidad Neta} \times 0.50$$

---

## 3. Instrucciones de Compilación y Ejecución

### Prerrequisitos:
- **Java JDK 17 o superior** instalado y configurado en el sistema.
- **Apache Maven 3.8+** (o el script wrapper incluido).

### Pasos para iniciar el servidor:
1. Abra una terminal en la raíz del proyecto:
   ```powershell
   cd "C:\Users\LEGION\.gemini\antigravity\scratch\ganaderia-rocha-altamirano"
   ```
2. Ejecute la aplicación con Maven:
   ```powershell
   mvn spring-boot:run
   ```
   *(O bien `mvn clean package` y luego `java -jar target/ganaderia-rocha-altamirano-1.0.0-SNAPSHOT.jar`)*

3. Abra su navegador web en:
   ```text
   http://localhost:8080
   ```

---

## 4. Rutas Web Principales

| Módulo | Ruta URL | Descripción |
| :--- | :--- | :--- |
| **Dashboard** | `http://localhost:8080/` | Visión ejecutiva, métricas de inventario y reparto 50-50 |
| **Inventario Ganado** | `http://localhost:8080/ganado` | Tabla de aretes, edades por paletas, pesos y filtros |
| **Nuevo Animal** | `http://localhost:8080/ganado/nuevo` | Formulario de registro de cabeza bovina |
| **Ganado Rechazo** | `http://localhost:8080/ganado/rechazos` | Control especializado de animales rechazados y mermas |
| **Compras** | `http://localhost:8080/compras` | Listado y registro de compras de lotes en pie |
| **Transporte** | `http://localhost:8080/transporte` | Bitácora de viajes, rutas de traslado y pagos de flete |
| **Balance 50-50** | `http://localhost:8080/finanzas/balance` | Estado de resultados y división equitativa de utilidades |
| **Liquidaciones** | `http://localhost:8080/finanzas/liquidaciones` | Comprobantes de pago de MACESA y San Martín |
| **Gastos** | `http://localhost:8080/finanzas/gastos` | Gastos de mantenimiento, insumos y planilla de finca |
| **Arqueos Mensuales**| `http://localhost:8080/finanzas/arqueos` | Cierres contables mensuales consolidados |

---

## 5. Arquitectura de Base de Datos (Migración Futura)

El script DDL SQL completo para cuando decida conectar una base de datos relacional (MySQL / MariaDB / PostgreSQL) se encuentra ubicado en:
`src/main/resources/db/schema.sql`

Incorpora:
- Integridad referencial con claves primarias y foráneas.
- Estrategia de herencia relacional **Joined Table** entre `ganado` y `ganado_rechazo`.
- Restricciones de verificación (`CHECK constraints`) para paletas (`0, 2, 4, 6, 8`), estados y categorías comerciales.
