-- Creación y selección de la base de datos
CREATE DATABASE IF NOT EXISTS SGG;
USE SGG;

-- Desactivar temporalmente la verificación de llaves foráneas para una creación limpia
SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================
-- NIVEL 1: Tablas independientes (sin llaves foráneas)
-- ============================================================

CREATE TABLE proveedor (
    id_proveedor INT PRIMARY KEY AUTO_INCREMENT,
    nombre       VARCHAR(100) NOT NULL
    -- Las ubicaciones del proveedor se gestionan en la tabla `ubicacion`
);

CREATE TABLE matadero (
    id_matadero      INT PRIMARY KEY AUTO_INCREMENT,
    nombre_matadero  VARCHAR(100) NOT NULL
    -- Las ubicaciones del matadero se gestionan en la tabla `ubicacion`
);

CREATE TABLE conductor (
    cedula            VARCHAR(20) PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL,
    url_foto_licencia VARCHAR(255)
);

CREATE TABLE camion (
    num_placa            VARCHAR(20) PRIMARY KEY,
    url_foto_circulacion VARCHAR(255)
);

CREATE TABLE categoria_gasto (
    id_categoria     INT PRIMARY KEY AUTO_INCREMENT,
    nombre_categoria VARCHAR(100) NOT NULL
);

CREATE TABLE arqueos_mensuales (
    id_arqueo INT PRIMARY KEY AUTO_INCREMENT,
    fecha     DATE
);

-- ============================================================
-- NIVEL 2: Tablas dependientes de nivel 1
-- ============================================================

CREATE TABLE ubicacion (
    id_ubicacion  INT PRIMARY KEY AUTO_INCREMENT,
    nombre_lugar  VARCHAR(100) NOT NULL,
    tipo_lugar    VARCHAR(50),
    -- url almacena la coordenada en formato https://www.google.com/maps?q=LAT,LNG
    -- generada automáticamente al hacer clic en el mapa Leaflet en el catálogo de Ubicaciones.
    -- No se captura desde los formularios de Proveedor ni Matadero.
    url           VARCHAR(255),
    id_proveedor  INT,
    id_matadero   INT,
    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor),
    FOREIGN KEY (id_matadero)  REFERENCES matadero(id_matadero)
);

CREATE TABLE telefono (
    id_telefono      INT PRIMARY KEY AUTO_INCREMENT,
    telefono         VARCHAR(20) NOT NULL,
    compañia         VARCHAR(50),
    id_proveedor     INT,
    id_matadero      INT,
    cedula_conductor VARCHAR(20),
    FOREIGN KEY (id_proveedor)     REFERENCES proveedor(id_proveedor),
    FOREIGN KEY (id_matadero)      REFERENCES matadero(id_matadero),
    FOREIGN KEY (cedula_conductor) REFERENCES conductor(cedula)
);

CREATE TABLE lote (
    id_lote           INT PRIMARY KEY AUTO_INCREMENT,
    fecha_compra      DATE NOT NULL,
    precio_de_rechazo DECIMAL(10,2),
    precio_estandar   DECIMAL(10,2),
    id_proveedor      INT,
    FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor)
);

CREATE TABLE entrega (
    id_entrega      INT PRIMARY KEY AUTO_INCREMENT,
    fecha_entrega   DATE NOT NULL,
    precio_acordado DECIMAL(10,2),
    precio_rechazo  DECIMAL(10,2),
    id_matadero     INT,
    FOREIGN KEY (id_matadero) REFERENCES matadero(id_matadero)
    -- Una entrega puede tener MÚLTIPLES viajes (ver tabla `viaje`)
);

CREATE TABLE liquidacion (
    id_num_comprobante VARCHAR(20) PRIMARY KEY,
    fecha_liquidacion  DATE NOT NULL,
    monto              DECIMAL(10,2),
    id_matadero        INT,
    id_arqueo          INT,
    FOREIGN KEY (id_matadero) REFERENCES matadero(id_matadero),
    FOREIGN KEY (id_arqueo)   REFERENCES arqueos_mensuales(id_arqueo)
);

-- ============================================================
-- NIVEL 3: Tablas que dependen de las anteriores
-- ============================================================

CREATE TABLE viaje (
    id_viaje             INT PRIMARY KEY AUTO_INCREMENT,
    -- id_entrega es OPCIONAL: NULL = viaje interno sin entrega asociada
    -- Muchos viajes pueden apuntar a la misma entrega (relación N:1 viaje→entrega)
    id_entrega           INT NULL,
    estado_pago          VARCHAR(50),
    fecha_pago           DATE,
    id_ubicacion_origen  INT,
    id_ubicacion_destino INT,
    cedula_conductor     VARCHAR(20),
    num_placa            VARCHAR(20),
    FOREIGN KEY (id_entrega)           REFERENCES entrega(id_entrega) ON DELETE SET NULL,
    FOREIGN KEY (id_ubicacion_origen)  REFERENCES ubicacion(id_ubicacion),
    FOREIGN KEY (id_ubicacion_destino) REFERENCES ubicacion(id_ubicacion),
    FOREIGN KEY (cedula_conductor)     REFERENCES conductor(cedula),
    FOREIGN KEY (num_placa)            REFERENCES camion(num_placa)
);

CREATE TABLE ganado (
    arete            VARCHAR(20) PRIMARY KEY,
    paletas          VARCHAR(50),
    color            VARCHAR(50),
    peso_kg          DECIMAL(8,2),
    condicion_compra VARCHAR(50),
    precio_compra    DECIMAL(10,2),
    arete_anterior   VARCHAR(20),
    id_lote          INT,
    fecha_ingreso    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_lote)        REFERENCES lote(id_lote),
    FOREIGN KEY (arete_anterior) REFERENCES ganado(arete)
);

CREATE TABLE pago_lote (
    id_pago            INT PRIMARY KEY AUTO_INCREMENT,
    comprobante_voucher VARCHAR(100),
    estado_pago        VARCHAR(50),
    id_lote            INT,
    FOREIGN KEY (id_lote) REFERENCES lote(id_lote)
);

-- ============================================================
-- NIVEL 4: Tablas que dependen del nivel 3
-- ============================================================

CREATE TABLE detalle_entrega (
    id_detalle_entrega INT PRIMARY KEY AUTO_INCREMENT,
    peso_matadero_kg   DECIMAL(8,2),
    estado_dictamen    VARCHAR(50),
    arete              VARCHAR(20),
    id_entrega         INT,
    FOREIGN KEY (arete)      REFERENCES ganado(arete),
    FOREIGN KEY (id_entrega) REFERENCES entrega(id_entrega)
);

CREATE TABLE gastos (
    id_gasto    INT PRIMARY KEY AUTO_INCREMENT,
    fecha_gasto DATE NOT NULL,
    descripcion TEXT,
    monto       DECIMAL(10,2),
    id_categoria INT,
    id_arqueo    INT,
    id_pago      INT,
    id_viaje     INT,
    FOREIGN KEY (id_categoria) REFERENCES categoria_gasto(id_categoria),
    FOREIGN KEY (id_arqueo)    REFERENCES arqueos_mensuales(id_arqueo),
    FOREIGN KEY (id_pago)      REFERENCES pago_lote(id_pago),
    FOREIGN KEY (id_viaje)     REFERENCES viaje(id_viaje)
);

-- ============================================================
-- NIVEL 5: Tablas de detalle final
-- ============================================================

CREATE TABLE ganado_rechazado (
    id_rechazo         INT PRIMARY KEY AUTO_INCREMENT,
    motivo_rechazo     TEXT,
    id_detalle_entrega INT,
    FOREIGN KEY (id_detalle_entrega) REFERENCES detalle_entrega(id_detalle_entrega)
);

-- Reactivar la verificación de llaves foráneas
SET FOREIGN_KEY_CHECKS = 1;