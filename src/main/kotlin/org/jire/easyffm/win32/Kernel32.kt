package org.jire.easyffm.win32

import java.lang.foreign.Addressable
import java.lang.foreign.MemoryAddress

interface Kernel32 {

    fun GetLastError(): Int

    fun CreateToolhelp32Snapshot(
        dwFlags: Int,
        th32ProcessID: Int
    ): MemoryAddress

    fun Process32First(
        hSnapshot: Addressable,
        lppe: Addressable
    ): Boolean

    fun Process32Next(
        hSnapshot: Addressable,
        lppe: Addressable
    ): Boolean

    fun CloseHandle(
        hObject: Addressable
    ): Boolean

    fun OpenProcess(
        dwDesiredAccess: Int,
        bInheritHandle: Boolean,
        dwProcessId: Int
    ): MemoryAddress

    fun ReadProcessMemory(
        hProcess: Addressable,
        lpBaseAddress: Addressable,
        lpBuffer: Addressable,
        nSize: Int,
        lpNumberOfBytesRead: Addressable
    ): Boolean

    fun WriteProcessMemory(
        hProcess: Addressable,
        lpBaseAddress: Addressable,
        lpBuffer: Addressable,
        nSize: Int,
        lpNumberOfBytesWritten: Addressable
    ): Boolean

}
