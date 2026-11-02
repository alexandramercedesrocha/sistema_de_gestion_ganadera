-- =============================================================================
-- ARQUITECTURA DE BASE DE DATOS RELACIONAL (DDL SQL)
-- Proyecto: Sistema de Gestión y Comercialización de Ganado Bovino
-- Sociedad: Wilber Rocha & Yader Altamirano (Participación 50% - 50%)
-- Motor Sugerido: MySQL 8.0+ / PostgreSQL 15+
-- =============================================================================

-- 1. TABLA PROVEEDORES
CREATE TABLE proveedores (
    id_proveedor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(30)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. TABLA MATADEROS
CREATE TABLE mataderos (
    id_matadero BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_matadero VARCHAR(120) NOT NULL,
    ubicacion VARCHAR(255) NOT NULL,
    contacto_telefono VARCHAR(30)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. TABLA BASE GANADO (ANIMAL BOVINO)
-- Patrón de Herencia: JOINED TABLE INHERITANCE
CREATE TABLE ganado (
    arete VARCHAR(30) PRIMARY KEY,
    paletas INT NOT NULL CHECK (paletas IN (0, 2, 4, 6, 8)),
    color VARCHAR(40) NOT NULL,
    estado VARCHAR(30) NOT NULL CHECK (estado IN ('En Finca', 'Entregado a Matadero')),
    peso_entrada_kg DECIMAL(8,2) NOT NULL,
    categoria VARCHAR(20) NOT NULL CHECK (categoria IN ('Apto', 'Rechazo')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_ganado_estado (estado),
    INDEX idx_ganado_categoria (categoria)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. TABLA GANADO_RECHAZO (ESPECIALIZACIÓN / HERENCIA DE GANADO)
CREATE TABLE ganado_rechazo (
    arete VARCHAR(30) PRIMARY KEY,
    motivo_rechazo TEXT NOT NULL,
    fecha_rechazo DATE NOT NULL,
    peso_matadero DECIMAL(8,2) NOT NULL,
    CONSTRAINT fk_rechazo_ganado FOREIGN KEY (arete) 
        REFERENCES ganado(arete) 
        ON DELETE CASCADE 
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. TABLA COMPRAS DE GANADO EN PIE
CREATE TABLE compras (
    id_compra BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_compra DATE NOT NULL,
    total_cabezas INT NOT NULL CHECK (total_cabezas > 0),
    precio_kg DECIMAL(10,2),
    precio_estandar DECIMAL(10,2),
    total_kilos DECIMAL(10,2) NOT NULL,
    proveedor VARCHAR(150) NOT NULL,
    monto_total DECIMAL(12,2) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_compras_fecha (fecha_compra)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. TABLA ENTREGAS A MATADERO
CREATE TABLE entregas (
    id_entrega BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_entrega DATE NOT NULL,
    total_cabezas INT NOT NULL,
    peso_total DECIMAL(10,2) NOT NULL,
    matadero VARCHAR(120) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_entregas_fecha (fecha_entrega)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 7. TABLA SERVICIOS DE TRANSPORTE (VIAJES Y FLETES)
CREATE TABLE transportes (
    id_transporte BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_transportista VARCHAR(120) NOT NULL,
    fecha_viaje DATE NOT NULL,
    origen VARCHAR(200) NOT NULL,
    destino VARCHAR(200) NOT NULL,
    costo_viaje DECIMAL(10,2) NOT NULL,
    estado_pago VARCHAR(20) NOT NULL DEFAULT 'Pendiente' CHECK (estado_pago IN ('Pendiente', 'Pagado')),
    INDEX idx_transporte_estado (estado_pago)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 8. TABLA LIQUIDACIONES DE MATADEROS
CREATE TABLE liquidaciones (
    id_num_comprobante VARCHAR(50) PRIMARY KEY,
    empresa VARCHAR(120) NOT NULL,
    fecha_liquidacion DATE NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_liquidaciones_fecha (fecha_liquidacion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 9. TABLA GASTOS OPERATIVOS
CREATE TABLE gastos (
    id_gasto BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_gasto VARCHAR(120) NOT NULL,
    fecha_gasto DATE NOT NULL,
    descripcion TEXT,
    monto DECIMAL(10,2) NOT NULL,
    INDEX idx_gastos_fecha (fecha_gasto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 10. TABLA ARQUEOS MENSUALES
CREATE TABLE arqueos_mensuales (
    id_arqueo BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATE NOT NULL,
    monto_bruto DECIMAL(12,2) NOT NULL,
    monto_neto DECIMAL(12,2) NOT NULL,
    gastos_varios DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_arqueos_fecha (fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
