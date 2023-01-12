package org.jire.easyffm

import org.jire.easyffm.win32.Kernel32
import org.jire.easyffm.win32.Tlhelp32
import org.jire.easyffm.win32.Tlhelp32.PROCESSENTRY32

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val kernel32 = foreignLibrary(Kernel32::class)

        val snapshot = kernel32.CreateToolhelp32Snapshot(Tlhelp32.TH32CS_SNAPALL, 0)
        try {
            val entry = foreignStruct(PROCESSENTRY32::class)
            while (kernel32.Process32Next(snapshot, entry.segment)) {
                println("${entry.th32ProcessID}: \"${entry.segment.getUtf8String(44)}\"")
            }
        } finally {
            kernel32.CloseHandle(snapshot)
        }
    }

}
