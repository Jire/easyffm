package org.jire.easyffm.win32

import org.jire.easyffm.ForeignStruct
import org.jire.easyffm.ForeignStruct.FieldOrder
import org.jire.easyffm.ForeignStruct.StructArray

interface Tlhelp32 {

    @FieldOrder(
        ["dwSize", "cntUsage", "th32ProcessID", "th32DefaultHeapID", "th32ModuleID",
            "cntThreads", "th32ParentProcessID", "pcPriClassBase", "dwFlags", "szExeFile"]
    )
    interface PROCESSENTRY32 : ForeignStruct {
        var dwSize: Int
        var cntUsage: Int
        var th32ProcessID: Int
        var th32DefaultHeapID: Long
        var th32ModuleID: Int
        var cntThreads: Int
        var th32ParentProcessID: Int
        var pcPriClassBase: Int
        var dwFlags: Int

        @get:StructArray(260)
        var szExeFile: CharArray
    }

    companion object {
        const val TH32CS_SNAPHEAPLIST = 0x00000001
        const val TH32CS_SNAPPROCESS = 0x00000002
        const val TH32CS_SNAPTHREAD = 0x00000004
        const val TH32CS_SNAPMODULE = 0x00000008
        const val TH32CS_SNAPMODULE32 = 0x00000010
        const val TH32CS_SNAPALL = TH32CS_SNAPHEAPLIST or
                TH32CS_SNAPPROCESS or
                TH32CS_SNAPTHREAD or
                TH32CS_SNAPMODULE
    }

}
