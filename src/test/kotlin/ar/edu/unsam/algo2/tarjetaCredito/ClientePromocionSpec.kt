package ar.edu.unsam.algo2.tarjetaCredito

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClientePromocionSpec: DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Dado un cliente que tiene únicamente promoción como condición comercial") {
        val cliente = ClienteBuilder(ClientePosta(40))
            .promocion()
            .build()
        it("al comprar por debajo del límite necesario para acumular puntos, no acumula puntos de promoción") {
            cliente.comprar(50)
            cliente.puntosPromocion shouldBe 0
        }
        it("al comprar por arriba del monto necesario para acumular puntos, acumula puntos de promoción") {
            cliente.comprar(60)
            cliente.puntosPromocion shouldBe 15
        }
    }
})