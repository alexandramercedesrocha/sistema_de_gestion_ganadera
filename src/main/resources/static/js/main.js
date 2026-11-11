/**
 * SGC Ganadería Rocha & Altamirano - Scripts Auxiliares y Validaciones Globales
 */

document.addEventListener('DOMContentLoaded', function () {
    // Cálculo automático para el formulario de compras (si existe en la vista)
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

// =========================================================================
// CONTROL Y BLOQUEO GLOBAL ESTRICTO CONTRA VALORES NUMÉRICOS NEGATIVOS
// Aplica a precios, pesos, montos, gastos, fletes y liquidaciones en todo el sistema.
// =========================================================================

// 1. Bloqueo en pulsación de teclas: impide escribir '-', 'e', 'E', '+'
document.addEventListener('keydown', function (e) {
    const target = e.target;
    if (target && target.matches && target.matches('input[type="number"]')) {
        // Bloquear '-' (signo negativo), 'e' / 'E' (notación exponencial), '+'
        if (e.key === '-' || e.key === 'Minus' || e.code === 'NumpadSubtract' || 
            e.key === 'e' || e.key === 'E' || e.key === '+') {
            e.preventDefault();
        }
    }
}, true);

// 2. Bloqueo en pegado de texto (Paste): impide pegar texto que contenga signos negativos
document.addEventListener('paste', function (e) {
    const target = e.target;
    if (target && target.matches && target.matches('input[type="number"]')) {
        const clipboardData = e.clipboardData || window.clipboardData;
        if (clipboardData) {
            const pastedText = clipboardData.getData('text');
            if (pastedText && (pastedText.includes('-') || parseFloat(pastedText) < 0)) {
                e.preventDefault();
                alert('No se permiten valores negativos en los campos numéricos.');
            }
        }
    }
}, true);

// 3. Sanitización reactiva en 'input': si por cualquier motivo el valor resultante es negativo, lo corrige al mínimo permitido o 0
document.addEventListener('input', function (e) {
    const target = e.target;
    if (target && target.matches && target.matches('input[type="number"]')) {
        if (target.value && target.value.trim() !== '') {
            const val = parseFloat(target.value);
            if (!isNaN(val) && val < 0) {
                target.value = (target.min && parseFloat(target.min) >= 0) ? target.min : '0';
            }
        }
    }
}, true);

// 4. Validación en cambio 'change'
document.addEventListener('change', function (e) {
    const target = e.target;
    if (target && target.matches && target.matches('input[type="number"]')) {
        if (target.value && target.value.trim() !== '') {
            const val = parseFloat(target.value);
            if (!isNaN(val) && val < 0) {
                target.value = (target.min && parseFloat(target.min) >= 0) ? target.min : '0';
            }
        }
    }
}, true);

// 5. Barrera de seguridad antes del envío de cualquier formulario
document.addEventListener('submit', function (e) {
    const form = e.target;
    if (form && form.querySelectorAll) {
        const numberInputs = form.querySelectorAll('input[type="number"]');
        for (let input of numberInputs) {
            if (input.value !== '' && input.value !== null) {
                const val = parseFloat(input.value);
                if (!isNaN(val) && val < 0) {
                    e.preventDefault();
                    e.stopPropagation();
                    alert('Error: No se permiten registros con números negativos (' + (input.name || 'campo numérico') + '). Por favor ingrese un valor positivo válido.');
                    input.focus();
                    return false;
                }
            }
        }
    }
}, true);
