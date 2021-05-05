import BusinessException.BusinessException

interface Cliente {
    var saldo: Int
    var puntosPromocion: Int
    fun comprar(monto: Int)
    fun pagarVencimiento(monto: Int)
    fun esMoroso() = this.saldo > 0
}

class ClientePosta(override var saldo: Int = 0) : Cliente {
    var montoMaximoSafeShop = 50
    override var puntosPromocion = 0
    var adheridoPromocion = false
    var adheridoSafeShop = false

    companion object {
        var montoMinimoPromocion = 50
        var PUNTAJE_PROMOCION = 15
    }

    override fun comprar(monto: Int) {
        if (adheridoSafeShop && monto > montoMaximoSafeShop) {
            throw BusinessException("Debe comprar por menos de " + montoMaximoSafeShop)
        }
        saldo = saldo + monto
        if (adheridoPromocion && monto > montoMinimoPromocion) {
            puntosPromocion = puntosPromocion + PUNTAJE_PROMOCION
        }
    }

    override fun pagarVencimiento(monto: Int) {
        saldo = saldo - monto
    }
}

class ClienteBuilder(val cliente: ClientePosta) {

    fun safeShop(montoMaximo: Int): ClienteBuilder {
        cliente.adheridoSafeShop = true
        cliente.montoMaximoSafeShop = montoMaximo
        return this
    }

    fun promocion(): ClienteBuilder {
        cliente.adheridoPromocion = true
        return this
    }

    fun build(): Cliente {
        if (cliente.saldo <= 0) {
            throw BusinessException("El saldo debe ser positivo")
        }
        return cliente
    }
}