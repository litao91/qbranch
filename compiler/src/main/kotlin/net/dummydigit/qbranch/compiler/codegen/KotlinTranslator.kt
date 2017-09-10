// Licensed under the MIT license. See LICENSE file in the project root
// for full license information.

package net.dummydigit.qbranch.compiler.codegen

import net.dummydigit.qbranch.compiler.ParsingUtil
import net.dummydigit.qbranch.compiler.Settings
import net.dummydigit.qbranch.compiler.symbols.*

internal class KotlinTranslator(settings : Settings) : Translator {
    private val compilerInfo = ParsingUtil.readCompilerInfoFromManifest()
    private val compilerName = compilerInfo.first
    private val compilerVersion = compilerInfo.second

    val builtinTypeMap = hashMapOf(
            "int8" to "Byte",
            "int16" to "Short",
            "int32" to "Int",
            "int64" to "Long",
            "uint8" to "UnsignedByte",
            "uint16" to "UnsignedShort",
            "uint32" to "UnsignedInt",
            "uint64" to "UnsignedLong",
            "string" to "ByteString",
            "wstring" to "String",
            "float" to "Float",
            "double" to "Double",
            "blob" to "Blob"
    )

    val builtinContainerMap = hashMapOf(
            "vector" to "java.util.ArrayList",
            "list" to "java.util.LinkedList",
            "map" to "java.util.HashMap"
    )

    val defaultValueForBuiltinType = hashMapOf(
            "int8" to "0",
            "int16" to "0",
            "int32" to "0",
            "int64" to "0",
            "uint8" to "UnsignedByte()",
            "uint16" to "UnsignedShort()",
            "uint32" to "UnsignedInt()",
            "uint64" to "UnsignedLong()",
            "string" to "ByteString()",
            "wstring" to "\"\"",
            "float" to "0.0f",
            "double" to "0.0",
            "vector" to "arrayListOf()",
            "list" to "java.util.LinkedList()",
            "map" to "hashMapOf()"
    )

    override fun generateHeader(): String {
        return "// Generated by $compilerName, version $compilerVersion\n"
    }

    override fun generate(typeParamDef: GenericTypeParamDef) : String {
        return typeParamDef.name
    }

    override fun generate(builtinTypeDef: BuiltinTypeDef) : String {
        return builtinTypeMap[builtinTypeDef.name]!!
    }

    override fun generate(containerType : BuiltinContainerDef) : String {
        return ""
    }

    override fun generate(builtinKvpContainerType : BuiltinKvpContainerDef) : String {
        return ""
    }

    override fun generate(attr : AttributeDef) : String {
        // return "@AttributeDef(\"${attr.name}\", \"${attr.value}\")"
        return ""
    }

    override fun generate(enumDef : EnumDef) : String {
        val enumDefHeader = "${generateCodeGenTag()}\nenum class ${enumDef.name}(val num : Int) {"
        val enumFields = enumDef.valueList.map {
            val enumName = it.first
            val enumValue = it.second
            "    $enumName($enumValue),"
        }.joinToString("\n")
        val enumDefEnd = "}"
        return "$enumDefHeader\n$enumFields\n$enumDefEnd"
    }

    override fun generate(importDef : ImportDef) : String {
        // KotlinTranslator/Java uses namespaces to reference objects,
        // so it's generally not needed to write import statement.
        // It's kept here just for readability purpose.
        //
        // NOTE: Official Bond gbc compiler does not really
        // support importing a bond IDL with different namespace.
        return "// Imported: ${importDef.importPath}\n"
    }

    override fun generate(namespaceDef : NamespaceDef) : String {
        return "package ${namespaceDef.namespace}\n"
    }

    override fun generate(structDef : StructDef) : String {
        if (!structDef.isViewOf) {
            val structHeader = "${generateCodeGenTag()}\nopen class ${structDef.name} {"
        }
        return "" // TODO
    }

    override fun generate(structField: StructFieldDef) : String {
        val name = structField.name
        val typeName = if (structField.isNullable) {
            structField.typeRef?.name
        } else {
            "${structField.typeRef?.name}?"
        }
        val typeBaseName = structField.typeRef?.name
        val modifierTag = when (structField.modifier) {
            StructFieldModifier.Optional -> "@Optional"
            StructFieldModifier.Required -> "@Required"
            StructFieldModifier.RequiredOptional -> "@RequiredOptional"
        }

        val fieldIdTag = "@FieldId(${structField.fieldOrderId})"
        val nullableTag = if (typeBaseName == "nullable") { "@Nullable" } else { "" }
        val attributeList = generateAttributeList(structField.attributeList)

        val assignedValue = if (structField.isValueAssigned) {
            " = ${structField.assignedValue}"
        } else {
            // TODO Not implemented.
            ""
        }

        return "$attributeList $fieldIdTag $nullableTag $modifierTag var $name : $typeName$assignedValue"
    }

    private fun generateCodeGenTag() : String {
        return "@QBranchGeneratedCode(\"$compilerName\", \"$compilerVersion\")"
    }

    private fun generateAttributeList(attrs : List<AttributeDef>) : String {
        return attrs.map { generate(it) }.joinToString("\n")
    }
}