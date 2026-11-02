-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 02, 2026 at 03:40 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sgg`
--

-- --------------------------------------------------------

--
-- Table structure for table `arqueos_mensuales`
--

CREATE TABLE `arqueos_mensuales` (
  `id_arqueo` int(11) NOT NULL,
  `fecha` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `camion`
--

CREATE TABLE `camion` (
  `num_placa` varchar(20) NOT NULL,
  `url_foto_circulacion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `categoria_gasto`
--

CREATE TABLE `categoria_gasto` (
  `id_categoria` int(11) NOT NULL,
  `nombre_categoria` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `conductor`
--

CREATE TABLE `conductor` (
  `cedula` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `url_foto_licencia` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `detalle_entrega`
--

CREATE TABLE `detalle_entrega` (
  `id_detalle_entrega` int(11) NOT NULL,
  `peso_matadero_kg` decimal(8,2) DEFAULT NULL,
  `estado_dictamen` varchar(50) DEFAULT NULL,
  `arete` varchar(20) DEFAULT NULL,
  `id_entrega` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `entrega`
--

CREATE TABLE `entrega` (
  `id_entrega` int(11) NOT NULL,
  `fecha_entrega` date NOT NULL,
  `precio_acordado` decimal(10,2) DEFAULT NULL,
  `precio_rechazo` decimal(10,2) DEFAULT NULL,
  `id_matadero` int(11) DEFAULT NULL,
  `id_viaje` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ganado`
--

CREATE TABLE `ganado` (
  `arete` varchar(20) NOT NULL,
  `paletas` varchar(50) DEFAULT NULL,
  `color` varchar(50) DEFAULT NULL,
  `peso_kg` decimal(8,2) DEFAULT NULL,
  `condicion_compra` varchar(50) DEFAULT NULL,
  `arete_anterior` varchar(20) DEFAULT NULL,
  `id_lote` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ganado_rechazado`
--

CREATE TABLE `ganado_rechazado` (
  `id_rechazo` int(11) NOT NULL,
  `motivo_rechazo` text DEFAULT NULL,
  `id_detalle_entrega` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `gastos`
--

CREATE TABLE `gastos` (
  `id_gasto` int(11) NOT NULL,
  `fecha_gasto` date NOT NULL,
  `descripcion` text DEFAULT NULL,
  `monto` decimal(10,2) DEFAULT NULL,
  `id_categoria` int(11) DEFAULT NULL,
  `id_arqueo` int(11) DEFAULT NULL,
  `id_pago` int(11) DEFAULT NULL,
  `id_viaje` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `liquidacion`
--

CREATE TABLE `liquidacion` (
  `id_num_comprobante` varchar(50) NOT NULL,
  `fecha_liquidacion` date NOT NULL,
  `monto` decimal(10,2) DEFAULT NULL,
  `id_matadero` int(11) DEFAULT NULL,
  `id_entrega` int(11) DEFAULT NULL,
  `id_arqueo` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `lote`
--

CREATE TABLE `lote` (
  `id_lote` int(11) NOT NULL,
  `fecha_compra` date NOT NULL,
  `precio_de_rechazo` decimal(10,2) DEFAULT NULL,
  `precio_estandar` decimal(10,2) DEFAULT NULL,
  `id_proveedor` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `matadero`
--

CREATE TABLE `matadero` (
  `id_matadero` int(11) NOT NULL,
  `nombre_matadero` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `pago_lote`
--

CREATE TABLE `pago_lote` (
  `id_pago` int(11) NOT NULL,
  `comprobante_voucher` varchar(100) DEFAULT NULL,
  `estado_pago` varchar(50) DEFAULT NULL,
  `id_lote` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `proveedor`
--

CREATE TABLE `proveedor` (
  `id_proveedor` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `telefono`
--

CREATE TABLE `telefono` (
  `id_telefono` int(11) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `compañia` varchar(50) DEFAULT NULL,
  `id_proveedor` int(11) DEFAULT NULL,
  `id_matadero` int(11) DEFAULT NULL,
  `cedula_conductor` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `ubicacion`
--

CREATE TABLE `ubicacion` (
  `id_ubicacion` int(11) NOT NULL,
  `nombre_lugar` varchar(100) NOT NULL,
  `tipo_lugar` varchar(50) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `id_proveedor` int(11) DEFAULT NULL,
  `id_matadero` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `viaje`
--

CREATE TABLE `viaje` (
  `id_viaje` int(11) NOT NULL,
  `estado_pago` varchar(50) DEFAULT NULL,
  `fecha_pago` date DEFAULT NULL,
  `id_ubicacion_origen` int(11) DEFAULT NULL,
  `id_ubicacion_destino` int(11) DEFAULT NULL,
  `cedula_conductor` varchar(20) DEFAULT NULL,
  `num_placa` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `arqueos_mensuales`
--
ALTER TABLE `arqueos_mensuales`
  ADD PRIMARY KEY (`id_arqueo`);

--
-- Indexes for table `camion`
--
ALTER TABLE `camion`
  ADD PRIMARY KEY (`num_placa`);

--
-- Indexes for table `categoria_gasto`
--
ALTER TABLE `categoria_gasto`
  ADD PRIMARY KEY (`id_categoria`);

--
-- Indexes for table `conductor`
--
ALTER TABLE `conductor`
  ADD PRIMARY KEY (`cedula`);

--
-- Indexes for table `detalle_entrega`
--
ALTER TABLE `detalle_entrega`
  ADD PRIMARY KEY (`id_detalle_entrega`),
  ADD KEY `arete` (`arete`),
  ADD KEY `id_entrega` (`id_entrega`);

--
-- Indexes for table `entrega`
--
ALTER TABLE `entrega`
  ADD PRIMARY KEY (`id_entrega`),
  ADD KEY `id_matadero` (`id_matadero`);

--
-- Indexes for table `ganado`
--
ALTER TABLE `ganado`
  ADD PRIMARY KEY (`arete`),
  ADD KEY `id_lote` (`id_lote`),
  ADD KEY `arete_anterior` (`arete_anterior`);

--
-- Indexes for table `ganado_rechazado`
--
ALTER TABLE `ganado_rechazado`
  ADD PRIMARY KEY (`id_rechazo`),
  ADD KEY `id_detalle_entrega` (`id_detalle_entrega`);

--
-- Indexes for table `gastos`
--
ALTER TABLE `gastos`
  ADD PRIMARY KEY (`id_gasto`),
  ADD KEY `id_categoria` (`id_categoria`),
  ADD KEY `id_arqueo` (`id_arqueo`),
  ADD KEY `id_pago` (`id_pago`),
  ADD KEY `id_viaje` (`id_viaje`);

--
-- Indexes for table `liquidacion`
--
ALTER TABLE `liquidacion`
  ADD PRIMARY KEY (`id_num_comprobante`),
  ADD KEY `id_matadero` (`id_matadero`),
  ADD KEY `id_arqueo` (`id_arqueo`);

--
-- Indexes for table `lote`
--
ALTER TABLE `lote`
  ADD PRIMARY KEY (`id_lote`),
  ADD KEY `id_proveedor` (`id_proveedor`);

--
-- Indexes for table `matadero`
--
ALTER TABLE `matadero`
  ADD PRIMARY KEY (`id_matadero`);

--
-- Indexes for table `pago_lote`
--
ALTER TABLE `pago_lote`
  ADD PRIMARY KEY (`id_pago`),
  ADD KEY `id_lote` (`id_lote`);

--
-- Indexes for table `proveedor`
--
ALTER TABLE `proveedor`
  ADD PRIMARY KEY (`id_proveedor`);

--
-- Indexes for table `telefono`
--
ALTER TABLE `telefono`
  ADD PRIMARY KEY (`id_telefono`),
  ADD KEY `id_proveedor` (`id_proveedor`),
  ADD KEY `id_matadero` (`id_matadero`),
  ADD KEY `cedula_conductor` (`cedula_conductor`);

--
-- Indexes for table `ubicacion`
--
ALTER TABLE `ubicacion`
  ADD PRIMARY KEY (`id_ubicacion`),
  ADD KEY `id_proveedor` (`id_proveedor`),
  ADD KEY `id_matadero` (`id_matadero`);

--
-- Indexes for table `viaje`
--
ALTER TABLE `viaje`
  ADD PRIMARY KEY (`id_viaje`),
  ADD KEY `id_ubicacion_origen` (`id_ubicacion_origen`),
  ADD KEY `id_ubicacion_destino` (`id_ubicacion_destino`),
  ADD KEY `cedula_conductor` (`cedula_conductor`),
  ADD KEY `num_placa` (`num_placa`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `arqueos_mensuales`
--
ALTER TABLE `arqueos_mensuales`
  MODIFY `id_arqueo` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `categoria_gasto`
--
ALTER TABLE `categoria_gasto`
  MODIFY `id_categoria` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `detalle_entrega`
--
ALTER TABLE `detalle_entrega`
  MODIFY `id_detalle_entrega` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `entrega`
--
ALTER TABLE `entrega`
  MODIFY `id_entrega` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ganado_rechazado`
--
ALTER TABLE `ganado_rechazado`
  MODIFY `id_rechazo` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `gastos`
--
ALTER TABLE `gastos`
  MODIFY `id_gasto` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `lote`
--
ALTER TABLE `lote`
  MODIFY `id_lote` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `matadero`
--
ALTER TABLE `matadero`
  MODIFY `id_matadero` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `pago_lote`
--
ALTER TABLE `pago_lote`
  MODIFY `id_pago` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `proveedor`
--
ALTER TABLE `proveedor`
  MODIFY `id_proveedor` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `telefono`
--
ALTER TABLE `telefono`
  MODIFY `id_telefono` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `ubicacion`
--
ALTER TABLE `ubicacion`
  MODIFY `id_ubicacion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `viaje`
--
ALTER TABLE `viaje`
  MODIFY `id_viaje` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `detalle_entrega`
--
ALTER TABLE `detalle_entrega`
  ADD CONSTRAINT `detalle_entrega_ibfk_1` FOREIGN KEY (`arete`) REFERENCES `ganado` (`arete`),
  ADD CONSTRAINT `detalle_entrega_ibfk_2` FOREIGN KEY (`id_entrega`) REFERENCES `entrega` (`id_entrega`);

--
-- Constraints for table `entrega`
--
ALTER TABLE `entrega`
  ADD CONSTRAINT `entrega_ibfk_1` FOREIGN KEY (`id_matadero`) REFERENCES `matadero` (`id_matadero`);

--
-- Constraints for table `ganado`
--
ALTER TABLE `ganado`
  ADD CONSTRAINT `ganado_ibfk_1` FOREIGN KEY (`id_lote`) REFERENCES `lote` (`id_lote`),
  ADD CONSTRAINT `ganado_ibfk_2` FOREIGN KEY (`arete_anterior`) REFERENCES `ganado` (`arete`);

--
-- Constraints for table `ganado_rechazado`
--
ALTER TABLE `ganado_rechazado`
  ADD CONSTRAINT `ganado_rechazado_ibfk_1` FOREIGN KEY (`id_detalle_entrega`) REFERENCES `detalle_entrega` (`id_detalle_entrega`);

--
-- Constraints for table `gastos`
--
ALTER TABLE `gastos`
  ADD CONSTRAINT `gastos_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria_gasto` (`id_categoria`),
  ADD CONSTRAINT `gastos_ibfk_2` FOREIGN KEY (`id_arqueo`) REFERENCES `arqueos_mensuales` (`id_arqueo`),
  ADD CONSTRAINT `gastos_ibfk_3` FOREIGN KEY (`id_pago`) REFERENCES `pago_lote` (`id_pago`),
  ADD CONSTRAINT `gastos_ibfk_4` FOREIGN KEY (`id_viaje`) REFERENCES `viaje` (`id_viaje`);

--
-- Constraints for table `liquidacion`
--
ALTER TABLE `liquidacion`
  ADD CONSTRAINT `liquidacion_ibfk_1` FOREIGN KEY (`id_matadero`) REFERENCES `matadero` (`id_matadero`),
  ADD CONSTRAINT `liquidacion_ibfk_2` FOREIGN KEY (`id_arqueo`) REFERENCES `arqueos_mensuales` (`id_arqueo`);

--
-- Constraints for table `lote`
--
ALTER TABLE `lote`
  ADD CONSTRAINT `lote_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`);

--
-- Constraints for table `pago_lote`
--
ALTER TABLE `pago_lote`
  ADD CONSTRAINT `pago_lote_ibfk_1` FOREIGN KEY (`id_lote`) REFERENCES `lote` (`id_lote`);

--
-- Constraints for table `telefono`
--
ALTER TABLE `telefono`
  ADD CONSTRAINT `telefono_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`),
  ADD CONSTRAINT `telefono_ibfk_2` FOREIGN KEY (`id_matadero`) REFERENCES `matadero` (`id_matadero`),
  ADD CONSTRAINT `telefono_ibfk_3` FOREIGN KEY (`cedula_conductor`) REFERENCES `conductor` (`cedula`);

--
-- Constraints for table `ubicacion`
--
ALTER TABLE `ubicacion`
  ADD CONSTRAINT `ubicacion_ibfk_1` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`),
  ADD CONSTRAINT `ubicacion_ibfk_2` FOREIGN KEY (`id_matadero`) REFERENCES `matadero` (`id_matadero`);

--
-- Constraints for table `viaje`
--
ALTER TABLE `viaje`
  ADD CONSTRAINT `viaje_ibfk_1` FOREIGN KEY (`id_ubicacion_origen`) REFERENCES `ubicacion` (`id_ubicacion`),
  ADD CONSTRAINT `viaje_ibfk_2` FOREIGN KEY (`id_ubicacion_destino`) REFERENCES `ubicacion` (`id_ubicacion`),
  ADD CONSTRAINT `viaje_ibfk_3` FOREIGN KEY (`cedula_conductor`) REFERENCES `conductor` (`cedula`),
  ADD CONSTRAINT `viaje_ibfk_4` FOREIGN KEY (`num_placa`) REFERENCES `camion` (`num_placa`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
