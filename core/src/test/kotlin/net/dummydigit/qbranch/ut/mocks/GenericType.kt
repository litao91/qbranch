// Licensed under the MIT license. See LICENSE file in the project root
// for full license information.

package net.dummydigit.qbranch.ut.mocks

import net.dummydigit.qbranch.annotations.*
import net.dummydigit.qbranch.generic.*

@QBranchGeneratedCode("mock", "version.mock")
class GenericType<T1 : Any, T2 : Any>(tArgT1 : TypeArg<T1>, tArgT2 : TypeArg<T2>) {
    @Transient private val fieldT1Creator = tArgT1
    @Transient private val fieldT2Creator = tArgT2
    @Transient private val vectorT1Creator = VectorT(tArgT1)
    @Transient private val mapT1T2Creator = MapT(tArgT1, tArgT2)
    @Transient private val setIntFieldCreator = SetT(BuiltinTypeArg.Int32T)

    companion object {
        @JvmStatic fun <T1 : Any, T2 : Any> asQTypeArg(tArgT1 : TypeArg<T1>, tArgT2 : TypeArg<T2>) : TypeArg<GenericType<T1, T2>> {
            return StructT({ GenericType(tArgT1, tArgT2) })
        }
    }

    @FieldId(0) @JvmField var fieldT1 : T1 = fieldT1Creator.newInstance()
    @FieldId(1) @JvmField var fieldT2 : T2 = fieldT2Creator.newInstance()
    @FieldId(2) @JvmField var vectorT1 : ArrayList<T1> = vectorT1Creator.newInstance()
    @FieldId(3) @JvmField var mapT1T2 : MutableMap<T1, T2> = mapT1T2Creator.newInstance()
    @FieldId(4) @JvmField var setIntField : MutableSet<Int> = setIntFieldCreator.newInstance()
}