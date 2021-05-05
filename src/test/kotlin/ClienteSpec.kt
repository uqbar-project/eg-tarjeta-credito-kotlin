import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClienteSpec: DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest

    describe("Dado un cliente sin condiciones comerciales") {
        val cliente = ClientePosta(50)
        it("al pagar el vencimiento deja de ser moroso") {
            cliente.pagarVencimiento(50)
            cliente.esMoroso() shouldBe false
        }
        it("al comprar sube el saldo") {
            cliente.comprar(50)
            cliente.saldo shouldBe 100
        }
    }
})