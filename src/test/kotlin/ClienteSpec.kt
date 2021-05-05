import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClienteSpec: DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Dado un cliente sin condiciones comerciales") {
        val cliente = ClienteBuilder(ClientePosta(50))
            .build()
        it("al pagar el vencimiento deja de ser moroso") {
            cliente.esMoroso() shouldBe true
            cliente.pagarVencimiento(50)
            cliente.esMoroso() shouldBe false
        }
        it("al comprar sube el saldo") {
            cliente.comprar(50)
            cliente.saldo shouldBe 100
        }
    }
})