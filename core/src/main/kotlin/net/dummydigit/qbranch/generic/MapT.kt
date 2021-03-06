// Licensed under the MIT license. See LICENSE file in the project root
// for full license information.

package net.dummydigit.qbranch.generic

class MapT<K : Any, V: Any>(val keyT : QTypeArg<K>,
                            val valueT : QTypeArg<V>) : QTypeArg<MutableMap<K, V>>, KvpContainerTypeArg<K, V> {
    private val refObj = newInstance()
    override fun newInstance(): MutableMap<K, V> = mutableMapOf()
    override fun getGenericType() : Class<MutableMap<K, V>> = refObj.javaClass
    override fun newKey(): K = keyT.newInstance()
    override fun newValue() : V = valueT.newInstance()
}