/**
 * SGC Ganadería Rocha & Altamirano - Scripts Auxiliares
 */

document.addEventListener('DOMContentLoaded', function () {
    // Cálculo automático para el formulario de compras
    const inputCabezas = document.getElementById('totalCabezas');
    const inputPrecioKg = document.getElementById('precioKg');
    const inputTotalKilos = document.getElementById('totalKilos');
    const inputMontoTotal = document.getElementById('montoTotal');

    function actualizarMontoEstimado() {
        if (!inputMontoTotal) return;
        const precio = parseFloat(inputPrecioKg ? inputPrecioKg.value : 0) || 0;
        const kilos = parseFloat(inputTotalKilos ? inputTotalKilos.value : 0) || 0;

        if (precio > 0 && kilos > 0) {
            const calculado = (precio * kilos).toFixed(2);
            inputMontoTotal.value = calculado;
        }
    }

    if (inputPrecioKg && inputTotalKilos) {
        inputPrecioKg.addEventListener('input', actualizarMontoEstimado);
        inputTotalKilos.addEventListener('input', actualizarMontoEstimado);
    }
});
