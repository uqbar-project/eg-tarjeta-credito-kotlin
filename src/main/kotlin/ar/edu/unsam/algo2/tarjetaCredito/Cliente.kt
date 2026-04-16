package ar.edu.unsam.algo2.tarjetaCredito

interface Cliente {
    fun comprar(monto: Int)
    fun pagarVencimiento(monto: Int)
    fun sumarPuntos(puntos: Int)
    fun saldo(): Int
    fun puntosPromocion(): Int
    fun esMoroso() = this.saldo() > 0
}

class ClientePosta(var saldo: Int = 0) : Cliente {
    var montoMaximoSafeShop = 50
    var puntosPromocion = 0
    var adheridoPromocion = false
    var adheridoSafeShop = false

    companion object {
        var montoMinimoPromocion = 50
        var PUNTAJE_PROMOCION = 15
    }

    override fun sumarPuntos(puntos: Int) {
        this.puntosPromocion = this.puntosPromocion + puntos
    }

    override fun saldo() = saldo

    override fun puntosPromocion() = puntosPromocion

    override fun comprar(monto: Int) {
        if (adheridoSafeShop && monto > montoMaximoSafeShop) {
            throw BusinessException("Debe comprar por menos de " + montoMaximoSafeShop)
        }
        saldo = saldo + monto
        if (adheridoPromocion && monto > montoMinimoPromocion) {
            this.sumarPuntos(PUNTAJE_PROMOCION)
        }
    }

    override fun pagarVencimiento(monto: Int) {
        saldo = saldo - monto
    }

}