package com.wutsi.platform.mail.dto

public data class SendMessageRequest(
    public val recipient: Party = Party(),
    public val content: Message = Message()
)
