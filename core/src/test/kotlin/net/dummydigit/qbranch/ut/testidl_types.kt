
//------------------------------------------------------------------------------
// This code was generated by a tool.
//
//   Tool : Bond Compiler 0.8.0.0
//   File : testidl_types.kt
//
// Changes to this file may cause incorrect behavior and will be lost when
// the code is regenerated.
// <auto-generated />
//------------------------------------------------------------------------------


package net.dummydigit.qbranch.ut

import net.dummydigit.qbranch.annotations.*
import net.dummydigit.qbranch.types.*



@QBranchGeneratedCode("gbc", "0.8.0.0")
open class PrimitiveStruct
{
    @FieldId(1) var int8value : Byte = 0

    @FieldId(3) var int16value : Short = 0

    @FieldId(5) var int32value : Int = 0

    @FieldId(7) var int64value : Long = 0L

    @FieldId(9) var uint8value : UnsignedByte = UnsignedByte()

    @FieldId(11) var uint16value : UnsignedShort = UnsignedShort()

    @FieldId(13) var uint32value : UnsignedInt = UnsignedInt()

    @FieldId(15) var uint64value : UnsignedLong = UnsignedLong()

    @FieldId(17) var stringvalue : ByteString = ByteString("")

    @FieldId(19) var wstringvalue : String = ""

    @FieldId(21) var floatvalue : Float = 0.0f

    @FieldId(23) var doublevalue : Double = 0.0
}

// End of package net.dummydigit.qbranch.ut