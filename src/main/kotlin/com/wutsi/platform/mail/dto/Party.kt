package com.wutsi.platform.mail.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class Party(
    @get:NotBlank
    public val email: String = "",
    public val displayName: String? = null
)
