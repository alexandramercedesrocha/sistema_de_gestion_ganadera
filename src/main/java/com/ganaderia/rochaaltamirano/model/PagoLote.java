package com.ganaderia.rochaaltamirano.model;

import jakarta.persistence.*;

/**
 * Entidad PagoLote (Tabla: pago_lote)
 * Control de pagos y comprobantes bancarios por compra de lotes.
 */
@Entity
@Table(name = "pago_lote")
public class PagoLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer idPago;

    @Column(name = "comprobante_voucher", length = 100)
    private String comprobanteVoucher;

    @Column(name = "estado_pago", length = 50)
    private String estadoPago; // ej. Pendiente, Pagado, En Tránsito

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lote")
    private Lote lote;

    public PagoLote() {
    }

    public PagoLote(Integer idPago, String comprobanteVoucher, String estadoPago, Lote lote) {
        this.idPago = idPago;
        this.comprobanteVoucher = comprobanteVoucher;
        this.estadoPago = estadoPago;
        this.lote = lote;
    }

    public Integer getIdPago() {
        return idPago;
    }

    public void setIdPago(Integer idPago) {
        this.idPago = idPago;
    }

    public String getComprobanteVoucher() {
        return comprobanteVoucher;
    }

    public void setComprobanteVoucher(String comprobanteVoucher) {
        this.comprobanteVoucher = comprobanteVoucher;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }
}

