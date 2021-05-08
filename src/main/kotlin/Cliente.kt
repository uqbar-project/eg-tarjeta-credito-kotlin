import BusinessException.BusinessException

interface Cliente {
    var saldo: Int
    var puntosPromocion: Int
    fun comprar(monto: Int)
    fun pagarVencimiento(monto: Int)
    fun sumarPuntos(puntos: Int)
    fun esMoroso() = this.saldo > 0
}

class ClientePosta(override var saldo: Int = 0) : Cliente {
    override var puntosPromocion = 0
    val condicionesComerciales = mutableListOf<CondicionComercial>()

    override fun comprar(monto: Int) {
        condicionesComerciales
            .sortedBy { it.order() }
            .forEach { condicionComercial ->  condicionComercial.comprar(monto, this) }
        saldo = saldo + monto
    }

    override fun pagarVencimiento(monto: Int) {
        saldo = saldo - monto
    }

    override fun sumarPuntos(puntos: Int) {
        puntosPromocion = puntosPromocion + puntos
    }

    fun agregarCondicionComercial(condicionComercial: CondicionComercial) {
        condicionesComerciales.add(condicionComercial)
    }
}

interface CondicionComercial {
    fun comprar(monto: Int, cliente: Cliente)
    fun order(): Int
}

class SafeShop(val montoMaximo: Int) : CondicionComercial {
    override fun comprar(monto: Int, cliente: Cliente) {
        if (monto > montoMaximo) {
            throw BusinessException("Debe comprar por menos de " + montoMaximo)
        }
    }
    override fun order() = 1
}

class Promocion : CondicionComercial {
    companion object {
        var montoMinimoPromocion = 50
        var PUNTAJE_PROMOCION = 15
    }

    override fun comprar(monto: Int, cliente: Cliente) {
        if (monto > montoMinimoPromocion) {
            cliente.sumarPuntos(PUNTAJE_PROMOCION)
        }
    }
    override fun order() = 2
}

class ClienteBuilder(val cliente: ClientePosta) {

    fun safeShop(montoMaximo: Int): ClienteBuilder {
        cliente.agregarCondicionComercial(SafeShop(montoMaximo))
        return this
    }

    fun promocion(): ClienteBuilder {
        cliente.agregarCondicionComercial(Promocion())
        return this
    }

    fun build(): Cliente {
        if (cliente.saldo <= 0) {
            throw BusinessException("El saldo debe ser positivo")
        }
        return cliente
    }
}
