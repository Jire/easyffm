package org.jire.easyffm.interception

interface InterceptionMouseStroke : InterceptionStroke {

    var state: Short
    var flags: Short
    var rolling: Short

    var x: Int
    var y: Int

    var information: Short

    fun flags(vararg flags: InterceptionMouseFlag) {
        var bitFlags = 0
        for (flag in flags) {
            bitFlags += flag.bitFlag
        }
        information = bitFlags.toShort()
    }

}
