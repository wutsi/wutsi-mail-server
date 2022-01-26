package com.wutsi.platform.mail.endpoint

import com.wutsi.platform.mail.`delegate`.SendMessageDelegate
import com.wutsi.platform.mail.dto.SendMessageRequest
import com.wutsi.platform.mail.dto.SendMessageResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SendMessageController(
    private val `delegate`: SendMessageDelegate
) {
    @PostMapping("/v1/mail/messages")
    @PreAuthorize(value = "hasAuthority('mail-send')")
    public fun invoke(@Valid @RequestBody request: SendMessageRequest): SendMessageResponse =
        delegate.invoke(request)
}
