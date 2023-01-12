package org.jire.easyffm.win32

import java.lang.foreign.Addressable

interface NtDll {

    fun NtReadVirtualMemory(
        processHandle: Addressable,
        baseAddress: Addressable,
        buffer: Addressable,
        numberOfBytesToRead: Long,
        numberOfBytesRead: Addressable
    )

    fun NtWriteVirtualMemory(
        processHandle: Addressable,
        baseAddress: Addressable,
        buffer: Addressable,
        numberOfBytesToWrite: Long,
        numberOfBytesWritten: Addressable
    )

}
