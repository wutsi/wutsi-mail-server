package com.wutsi.platform.mail.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class Message(
    @get:NotBlank
    public val subject: String = "",
    public val body: String = "",
    public val language: String? = null,
    public val mimeType: String? = null
)
