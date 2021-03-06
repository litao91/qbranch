// Licensed under the MIT license. See LICENSE file in the project root
// for full license information.

package net.dummydigit.qbranch.compiler.exceptions

import net.dummydigit.qbranch.compiler.SourceCodeInfo

open class CompilationError(val hint : String,
                       val sourceCodeInfo: SourceCodeInfo)
    : RuntimeException("CompilationError:msg=$hint,file=${sourceCodeInfo.path},line=${sourceCodeInfo.lineNo}")