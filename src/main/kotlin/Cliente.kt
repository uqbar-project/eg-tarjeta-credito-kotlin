import BusinessException.BusinessException

interface Cliente {
    fun comprar(monto: Int)
    fun pagarVencimiento(monto: Int)
    fun sumarPuntos(puntos: Int)
    fun saldo(): Int
    fun puntosPromocion(): Int
    fun esMoroso() = this.saldo() > 0
}

class ClientePosta(var saldo: Int = 0) : Cliente {
    var puntosPromocion = 0

    override fun comprar(monto: Int) {
        saldo = saldo + monto
    }

    override fun pagarVencimiento(monto: Int) {
        saldo = saldo - monto
    }

    override fun sumarPuntos(puntos: Int) {
        puntosPromocion = puntosPromocion + puntos
    }

    override fun saldo() = saldo
    override fun puntosPromocion() = puntosPromocion
}

abstract class ClienteConCondicionComercial(val cliente: Cliente) : Cliente {
    override fun pagarVencimiento(monto: Int) {
        cliente.pagarVencimiento(monto)
    }
    override fun sumarPuntos(puntos: Int) =  cliente.sumarPuntos(puntos)
    override fun saldo() = cliente.saldo()
    override fun puntosPromocion() = cliente.puntosPromocion()
}

class SafeShop(val montoMaximo: Int, cliente: Cliente) : ClienteConCondicionComercial(cliente) {
    override fun comprar(monto: Int) {
        if (monto > montoMaximo) {
            throw BusinessException("Debe comprar por menos de " + montoMaximo)
        }
        cliente.comprar(monto)
    }
}

class Promocion(cliente: Cliente) : ClienteConCondicionComercial(cliente) {
    companion object {
        var montoMinimoPromocion = 50
        var PUNTAJE_PROMOCION = 15
    }

    override fun comprar(monto: Int) {
        cliente.comprar(monto)
        if (monto > montoMinimoPromocion) {
            cliente.sumarPuntos(PUNTAJE_PROMOCION)
        }
    }
}

class ClienteBuilder(var cliente: Cliente) {
    fun safeShop(montoMaximo: Int): ClienteBuilder {
        cliente = SafeShop(montoMaximo, cliente)
        return this
    }

    fun promocion(): ClienteBuilder {
        cliente = Promocion(cliente)
        return this
    }

    fun build(): Cliente {
        if (cliente.saldo() <= 0) {
            throw BusinessException("El saldo debe ser positivo")
        }
        return cliente
    }
}
