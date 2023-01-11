package org.jire.easyffm.win32

interface User32 {

    fun GetKeyState(nVirtKey: Int): Short

    fun MapVirtualKeyA(uCode: Int, uMapType: Int): Int

    enum class MapVirtualKeyType(val value: Int) {
        VK_TO_VSC(0),
        VSC_TO_VK(1),
        VK_TO_CHAR(2),
        VSC_TO_VK_EX(3),
        VK_TO_VSC_EX(4)
    }

    fun MapVirtualKeyA(uCode: Int, uMapType: MapVirtualKeyType = MapVirtualKeyType.VK_TO_VSC) =
        MapVirtualKeyA(uCode, uMapType.value)

}
