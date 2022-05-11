package ar.edu.unsam.algo2.tarjetaCredito

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClienteMixtoSpec: DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    val montoMaximoSafeShopCliente = 80

    describe("Dado un cliente que tiene tanto safe shop como promoción como condiciones comerciales") {
        val cliente = ClienteBuilder(ClientePosta(50))
            .promocion()
            .safeShop(montoMaximoSafeShopCliente)
            .build()
        it("Al comprar por arriba del límite de promoción y por debajo del safe shop, acumula puntos y la compra funciona ok") {
            cliente.comprar(60)
            cliente.saldo() shouldBe 110
            cliente.puntosPromocion() shouldBe 15
        }
        it("Al comprar por arriba del límite de safe shop, la compra se cancela y no acumula puntos") {
            shouldThrow<BusinessException> { -> cliente.comprar(montoMaximoSafeShopCliente + 1) }
            cliente.saldo() shouldBe 50
            cliente.puntosPromocion() shouldBe 0
        }
    }
})