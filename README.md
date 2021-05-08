
# Clientes de una tarjeta de crédito

![credit card](./images/creditCardSmall.png)

[![build](https://github.com/uqbar-project/eg-tarjeta-credito-kotlin/actions/workflows/build.yml/badge.svg?branch=02-strategy)](https://github.com/uqbar-project/eg-tarjeta-credito-kotlin/actions/workflows/build.yml) [![codecov](https://codecov.io/gh/uqbar-project/eg-tarjeta-credito-kotlin/branch/02-strategy/graph/badge.svg?token=7fKK9riTkh)](https://codecov.io/gh/uqbar-project/eg-tarjeta-credito-kotlin)

## Apunte original

- [Clientes de una tarjeta de crédito](https://docs.google.com/document/d/1Ijz8Pe-ci6bYwbxIn-VZDV1QcijDy2JuAUQtohNX0oA/edit#heading=h.30j0zll)

## Variante con strategies

Cada condición comercial se representa como una estrategia dentro de la compra. Agregamos entonces una colección de condiciones comerciales en el cliente:

```kt
class ClientePosta(override var saldo: Int = 0) : Cliente {
    override var puntosPromocion = 0
    val condicionesComerciales = mutableListOf<CondicionComercial>()
```

Como ventaja, desaparecen atributos para manejar el monto máximo de safe shop y los flags por cada condición comercial.

### Implementación de las condiciones comerciales

Tanto `SafeShop` como `Promocion` son clases que implementan la interfaz `CondicionComercial`, ya que Kotlin trabaja con [tipado nominal](https://wiki.uqbar.org/wiki/articles/esquemas-de-tipado.html) [(vs. el estructural)](https://blog.koalite.com/2018/01/tipados-nominal-y-tipado-estructural/):

```kt
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
```

Algunos comentarios:

- decidimos dejar los puntos de promoción dentro del cliente, para simplificar su uso (podés pensar qué pasaría si el test tuviera que conocer los puntos a través de la promoción directamente, cómo podría llegar a esa referencia sin pasar por el cliente)
- como consecuencia de esta última decisión, tuvimos que agregar un mensaje más en la interfaz Cliente:

```kt
interface Cliente {
    ...
    fun sumarPuntos(puntos: Int)
```

- también necesitamos establecer un orden para las condiciones comerciales, ya que no es lo mismo que primero esté la promoción y luego la compra segura que al revés. En el primer caso podría pasar que primero se sume puntos a una compra que en realidad no debería estar permitida.

### Cómo queda el método comprar

El método comprar debe incorporar la llamada a las condiciones comerciales **antes** de sumar el saldo:

```kt
override fun comprar(monto: Int) {
    condicionesComerciales
        .sortedBy { it.order() }
        .forEach { condicionComercial ->  condicionComercial.comprar(monto, this) }
    saldo = saldo + monto
}
```

Además como dijimos antes, hay que ordenar las condiciones comerciales para asegurarnos de que la compra segura tenga prioridad sobre las otras condiciones.

### Cambios al builder

El builder solo debe modificar la forma en la que se generan las condiciones comerciales:

```kt
class ClienteBuilder(val cliente: ClientePosta) {

    fun safeShop(montoMaximo: Int): ClienteBuilder {
        cliente.agregarCondicionComercial(SafeShop(montoMaximo))
        return this
    }

    fun promocion(): ClienteBuilder {
        cliente.agregarCondicionComercial(Promocion())
        return this
    }
```

### Los tests quedan igual

Gracias al diseño del ClienteBuilder, no debemos hacer ningún cambio en los tests y éstos pasan satisfactoriamente.
