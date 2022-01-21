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

class SafeShop(val maximo: Int, val cliente : Cliente) : Cliente by cliente {
    override fun comprar(monto: Int) {
        if (monto > maximo) throw BusinessException("No puede comprar por más de " + monto)
        cliente.comprar(monto)
    }
}

class Promocion(val cliente : Cliente) : Cliente by cliente {
    override fun comprar(monto: Int) {
        cliente.comprar(monto)
        if (monto > 50) cliente.sumarPuntos(15)
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
