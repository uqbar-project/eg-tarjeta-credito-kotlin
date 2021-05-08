
# Clientes de una tarjeta de crédito

![credit card](./images/creditCardSmall.png)

[![build](https://github.com/uqbar-project/eg-tarjeta-credito-kotlin/actions/workflows/build.yml/badge.svg?branch=04-delegated-classes)](https://github.com/uqbar-project/eg-tarjeta-credito-kotlin/actions/workflows/build.yml) [![coverage](https://codecov.io/gh/uqbar-project/eg-tarjeta-credito-kotlin/branch/04-delegated-classes/graph/badge.svg)](https://codecov.io/gh/uqbar-project/eg-tarjeta-credito-kotlin/branch/04-delegated-classes/graph/badge.svg)

## Apunte original

- [Clientes de una tarjeta de crédito](https://docs.google.com/document/d/1Ijz8Pe-ci6bYwbxIn-VZDV1QcijDy2JuAUQtohNX0oA/edit#heading=h.30j0zll)

## Variante con decorators usando delegated classes

En la variante anterior necesitamos crear una superclase que definía una referencia `cliente` solo para delegar los mensajes en los que los decoradores no agregaban comportamiento:

```kt
abstract class ClienteConCondicionComercial(val cliente: Cliente) : Cliente {
    override fun pagarVencimiento(monto: Int) {
        cliente.pagarVencimiento(monto)
    }
    override fun sumarPuntos(puntos: Int) =  cliente.sumarPuntos(puntos)
    override fun saldo() = cliente.saldo()
    override fun puntosPromocion() = cliente.puntosPromocion()
}
```

Kotlin trae el concepto [**delegation**](https://kotlinlang.org/docs/delegation.html), que permite envolver (_wrappear_ o decorar) un objeto y modificarle comportamiento, de la siguiente manera:

```kt
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
```

En la siguiente línea:

```kt
class Promocion(val cliente : Cliente) : Cliente by cliente {
```

estamos recibiendo el cliente como parámetro en el constructor, pero también agregamos el modificador `by cliente`, de manera que las definiciones de los principales métodos

- sumarPuntos
- pagarVencimiento
- puntosPromocion
- saldo

etc. se toman del objeto cliente, que es de tipo `Cliente` (no sabemos si será un decorador o un cliente posta). Eso permite encadenar los decoradores sin tener que definir código _boilerplate_ que delegue al objeto decorado, como hicimos antes.

### El resto de la solución no necesita cambios

El Builder, el cliente posta y los tests no es necesario modificarlos, la solución funciona correctamente.

### Variantes similares en otros lenguajes

- [Property & Method missing in Groovy](https://www.baeldung.com/groovy-metaprogramming)
- [Method missing in Ruby](https://medium.com/podiihq/method-missing-in-ruby-af4c6edd5130)
