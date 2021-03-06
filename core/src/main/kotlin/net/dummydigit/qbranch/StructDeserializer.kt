// Licensed under the MIT license. See LICENSE file in the project root
// for full license information.

package net.dummydigit.qbranch

import net.dummydigit.qbranch.annotations.FieldId
import net.dummydigit.qbranch.exceptions.UnsupportedBondTypeException
import net.dummydigit.qbranch.generic.*
import net.dummydigit.qbranch.protocols.TaggedProtocolReader
import java.lang.reflect.Field
import java.util.*

internal class StructDeserializer(private val typeArg : StructT<*>,
                                  private val isBaseClass : Boolean) : DeserializerBase {
    private val refObj = typeArg.newInstance()
    private val cls = refObj.javaClass
    private val declaredFieldDeserializerMap = buildDeclaredFieldDeserializer()
    private val inheritanceChainDeserializer = buildInheritanceChainDeserializer()

    init { ensureClsIsQBranchGeneratedStruct(cls) }

    override fun deserialize(reader: TaggedProtocolReader) : Any {
        val obj = typeArg.newInstance()
        inheritanceChainDeserializer.forEach {
            it.deserializeDeclaredFields(it.cls.cast(obj), reader)
        }
        deserializeDeclaredFields(obj, reader)
        return obj
    }

    private fun buildInheritanceChainDeserializer() : List<StructDeserializer> {
        val deserializers = arrayListOf<StructDeserializer>()
        var baseClass = cls.superclass
        while (baseClass != Object::class.java) {
            ensureClsIsQBranchGeneratedStruct(baseClass)
            deserializers.add(StructDeserializer(typeArg.baseClassT!!, true))
            baseClass = cls.superclass
        }
        return deserializers.reversed() // Start from base class
    }

    private fun deserializeDeclaredFields(preCreatedObj: Any, reader: TaggedProtocolReader) : Any {
        val stopSign = if (isBaseClass) {
            BondDataType.BT_STOP_BASE
        } else {
            BondDataType.BT_STOP
        }

        var fieldInfo = reader.parseNextField()
        while (fieldInfo.typeId != stopSign) {
            val valueSetter = declaredFieldDeserializerMap[fieldInfo.fieldId]
            if (valueSetter != null) {
                valueSetter.set(preCreatedObj, reader)
            } else {
                reader.skipField(fieldInfo.typeId)
            }
            fieldInfo = reader.parseNextField()
        }

        return preCreatedObj
    }

    private fun getFieldTypeArg(field : Field) : QTypeArg<*>? {
        val foundCreatorField = typeArg.javaClass.declaredFields.find { it.name == field.name }
        if (foundCreatorField != null) {
            foundCreatorField.isAccessible = true
            return foundCreatorField.get(typeArg) as QTypeArg<*>
        }
        return null
    }

    private fun buildDeclaredFieldDeserializer() : Map<Int, ValueSetter> {
        val fieldDeserializerMap = HashMap<Int, ValueSetter>()

        cls.declaredFields.forEach {
            if (it.name == "QTypeDef") {
                // Companion object for defining asQTypeArg
                return@forEach
            }

            it.isAccessible = true
            val fieldIdAnnotation = it.getDeclaredAnnotation(FieldId::class.java)
            if (fieldIdAnnotation != null) {

                val fieldTypeArg = getFieldTypeArg(it)
                val deserializer = if (fieldTypeArg != null) {
                    // All container, generated types go there.
                    DeserializerBase.createDeserializerByTypeArg(fieldTypeArg)
                } else {
                    // Primitive types, enums go there.
                    getDeserializerOfPrimitiveType(it)
                }
                val valueSetter = ValueSetter(it, deserializer)
                fieldDeserializerMap.put(fieldIdAnnotation.id, valueSetter)
            } else {
                throw UnsupportedBondTypeException(cls)
            }
        }
        return fieldDeserializerMap
    }

    private fun getDeserializerOfPrimitiveType(field : Field) : DeserializerBase {
        return when (field.genericType) {
            Boolean::class.java -> BuiltinTypeDeserializer.Bool
            Byte::class.java -> BuiltinTypeDeserializer.Int8
            Short::class.java -> BuiltinTypeDeserializer.Int16
            Int::class.java -> BuiltinTypeDeserializer.Int32
            Long::class.java -> BuiltinTypeDeserializer.Int64
            UnsignedByte::class.java -> BuiltinTypeDeserializer.UInt8
            UnsignedShort::class.java -> BuiltinTypeDeserializer.UInt16
            UnsignedInt::class.java -> BuiltinTypeDeserializer.UInt32
            UnsignedLong::class.java -> BuiltinTypeDeserializer.UInt64
            Float::class.java -> BuiltinTypeDeserializer.Float
            Double::class.java -> BuiltinTypeDeserializer.Double
            String::class.java -> BuiltinTypeDeserializer.WString
            ByteString::class.java -> BuiltinTypeDeserializer.ByteString
            else -> {
                throw NotImplementedError()
            }
        }
    }

    private fun ensureClsIsQBranchGeneratedStruct(cls : Class<*>) {
        if (!cls.isQBranchGeneratedStruct()) {
            throw UnsupportedBondTypeException(cls)
        }
    }
}