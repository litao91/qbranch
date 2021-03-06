// Licensed under the MIT license. See LICENSE file in the project root
// for full license information.

package net.dummydigit.qbranch.compiler

import net.dummydigit.qbranch.compiler.codegen.KotlinTranslator
import net.dummydigit.qbranch.compiler.codegen.OneFilePerInputSourceWriter
import net.dummydigit.qbranch.compiler.codegen.Translator
import net.dummydigit.qbranch.compiler.exceptions.UnsupportedCodeGeneratorException
import net.dummydigit.qbranch.compiler.parser.*

/**
 * Main entry to compile Bond IDl to source code.
 */
class BondIdlCompiler(compilerSettings: Settings,
                      private val sourceCodeLoader : SourceCodeLoader,
                      private val targetCodeSaver : TargetCodeSaver) {
    private val settings = compilerSettings
    private val translator = getTranslatorByName(settings)

    constructor(compilerSettings: Settings)
            : this(compilerSettings,
            FileSourceCodeLoader(compilerSettings),
            FileTargetCodeSaver(compilerSettings))

    fun generateTargetCode(inputSource : String) {
        val parser = SourceTreeParser(sourceCodeLoader)
        val sourceTreeList = parser.parse(inputSource)
        val construct = IntermediateConstruct(sourceTreeList)
        val writer = OneFilePerInputSourceWriter(translator, targetCodeSaver)
        writer.generateTargetSource(construct)
    }

    private fun getTranslatorByName(settings: Settings) : Translator {
        val targetName = settings.targetSourceGen
        return when (targetName.toLowerCase()) {
            "kotlin" -> KotlinTranslator(settings)
            else -> throw UnsupportedCodeGeneratorException(targetName)
        }
    }
}