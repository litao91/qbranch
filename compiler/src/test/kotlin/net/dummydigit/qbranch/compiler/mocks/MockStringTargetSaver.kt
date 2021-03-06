package net.dummydigit.qbranch.compiler.mocks

import net.dummydigit.qbranch.compiler.TargetCodeSaver
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class MockStringTargetSaver(bufferSize : Int) : TargetCodeSaver {
    private val stream = ByteArrayOutputStream(bufferSize)
    var savedContent : String = ""
    var savedContentArray : Array<String> = arrayOf()

    override fun openStream(sourceName: String): OutputStream {
        return stream
    }

    override fun onSaveDone() {
        savedContent = stream.toString("UTF-8")
        savedContentArray = savedContent.split("\n").toTypedArray()
    }

    override fun resolvePath(sourceName: String): String {
        return sourceName
    }
}