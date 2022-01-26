package com.wutsi.platform.mail.`delegate`

import com.wutsi.platform.mail.dto.Party
import com.wutsi.platform.mail.dto.SendMessageRequest
import com.wutsi.platform.mail.dto.SendMessageResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.util.UUID
import javax.mail.Message
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

@Service
public class SendMessageDelegate(
    @Autowired private val sender: JavaMailSender,

    @Value("\${spring.mail.properties.mail.smtp.from}") private val from: String
) {
    public fun invoke(request: SendMessageRequest): SendMessageResponse {
        val message = createMessage(request)
        sender.send(message)
        return SendMessageResponse(
            id = UUID.randomUUID().toString()
        )
    }

    private fun createMessage(request: SendMessageRequest): MimeMessage {
        val fromAddress = InternetAddress(from)
        val message = sender.createMimeMessage()
        message.addRecipients(Message.RecipientType.TO, arrayOf(toAddress(request.recipient)))
        message.setFrom(fromAddress)
        message.sender = fromAddress
        message.subject = request.content.subject
        message.contentLanguage = request.content.language?.let { arrayOf(it) }
        message.setContent(request.content.body, request.content.mimeType)

//        val unsubscribeUrl = unsubscribeUrl(request, site)
//        val unsubscribeEmail = unsubscribeEmail(site)
//        if (!unsubscribeEmail.isNullOrEmpty() && !unsubscribeUrl.isNullOrEmpty()) {
//            message.addHeader("List-Unsubscribe", "<mailto:$unsubscribeEmail>,<$unsubscribeUrl>")
//            message.addHeader("List-Unsubscribe-Post", "List-Unsubscribe=One-Click")
//        }
        return message
    }

    private fun toAddress(party: Party?): InternetAddress? =
        if (party == null)
            null
        else if (party.displayName == null)
            InternetAddress(party.email)
        else
            InternetAddress(party.email, party.displayName)
}
