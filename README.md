
# Clientes de una tarjeta de crédito

![credit card](./images/creditCardSmall.png)

[![build](https://github.com/uqbar-project/eg-tarjeta-credito-kotlin/actions/workflows/build.yml/badge.svg)](https://github.com/uqbar-project/eg-tarjeta-credito-kotlin/actions/workflows/build.yml) [![coverage](https://codecov.io/gh/uqbar-project/eg-tarjeta-credito-kotlin/branch/01-builder/graph/badge.svg)](https://codecov.io/gh/uqbar-project/eg-tarjeta-credito-kotlin/branch/01-builder/graph/badge.svg)

## Apunte original

- [Clientes de una tarjeta de crédito](https://docs.google.com/document/d/1Ijz8Pe-ci6bYwbxIn-VZDV1QcijDy2JuAUQtohNX0oA/edit#heading=h.30j0zll)

## Definiendo una interfaz más rica

Queremos que la interfaz de Cliente

- tenga una definición de moroso, 
- y defina propiedades saldo y puntosPromocion

ya que las interfaces de Kotlin pueden definir código default (solo que no pueden definir estado, simplemente fuerzan a las clases que la implementan a que existan esos atributos)

```kt
interface Cliente {
    var saldo: Int
    var puntosPromocion: Int
    fun comprar(monto: Int)
    fun pagarVencimiento(monto: Int)
    fun esMoroso() = this.saldo > 0
}
```

Ahora la clase ClientePosta toma la definición `esMoroso()` de la interfaz Cliente, y debe indicar los atributos que corresponden por dicha interfaz:

```kt
class ClientePosta(override var saldo: Int = 0) : Cliente {
    var montoMaximoSafeShop = 50
    override var puntosPromocion = 0
```

## Creando clientes con un builder

Otra idea que permite simplificar la instanciación de un cliente es la utilización de un **builder** u objeto que sabe construir un cliente. Por el momento pareciera un caso de sobrediseǹo, pero a priori nos permite ahorrar la sincronización de los booleanos `adheridoSafeShop` y `adheridoPromocion`.

El builder tiene como característica

- recibir un cliente
- tener métodos que permiten agregar condiciones comerciales, y en cada uno de ellos se devuelve el propio builder. Eso permite encadenar los mensajes en los tests
- por último, en el método `build()` se pueden hacer validaciones asegurando la consistencia del objeto creado

Vemos el código

```kt
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
```

Por otra parte el uso en el test que trabaja con un cliente con las dos condiciones comerciales es:

```kt
val cliente = ClienteBuilder(ClientePosta(50))
            .promocion()
            .safeShop(montoMaximoSafeShopCliente)
            .build()
```

Más adelante esta definición nos será muy útil.
