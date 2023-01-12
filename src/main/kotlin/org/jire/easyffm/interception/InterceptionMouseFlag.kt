package org.jire.easyffm.interception

enum class InterceptionMouseFlag(
    val bitFlag: Int
) {

    MOVE_RELATIVE(0x000),
    MOVE_ABSOLUTE(0x001),
    VIRTUAL_DESKTOP(0x002),
    ATTRIBUTES_CHANGED(0x004),
    MOVE_NOCOALESCE(0x008),
    TERMSRV_SRC_SHADOW(0x100),
    CUSTOM(0x200);

}
