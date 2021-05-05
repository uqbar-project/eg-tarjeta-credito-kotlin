import BusinessException.BusinessException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClienteSafeShopSpec: DescribeSpec({
    isolationMode = IsolationMode.InstancePerTest
    val montoMaximoSafeShopCliente = 30

    describe("Dado un cliente que tiene únicamente safe shop como condición comercial") {
        val cliente = ClienteBuilder(ClientePosta(50))
            .safeShop(montoMaximoSafeShopCliente)
            .build()
        it("no debe poder comprar por más del valor permitido ni debe aumentar el saldo") {
            shouldThrow<BusinessException> { -> cliente.comprar(montoMaximoSafeShopCliente + 1) }
            cliente.saldo shouldBe 50
        }
        it("debe poder comprar hasta el valor límite") {
            cliente.comprar(montoMaximoSafeShopCliente)
            cliente.saldo shouldBe 50 + montoMaximoSafeShopCliente
        }
    }
})