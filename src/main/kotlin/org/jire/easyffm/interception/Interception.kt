package org.jire.easyffm.interception

interface Interception {

    fun interception_create_context(): InterceptionContext

    fun interception_send(
        context: InterceptionContext,
        device: Int = 1,
        stroke: InterceptionStroke,
        strokeCount: Int = 1
    ): Int

}
