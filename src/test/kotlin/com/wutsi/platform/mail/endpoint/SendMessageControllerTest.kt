package com.wutsi.platform.mail.endpoint

import com.amazonaws.util.IOUtils
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import com.wutsi.platform.mail.dto.Message
import com.wutsi.platform.mail.dto.Party
import com.wutsi.platform.mail.dto.SendMessageRequest
import com.wutsi.platform.mail.dto.SendMessageResponse
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus
import javax.mail.internet.InternetAddress

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SendMessageControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var url: String

    private var smtp: GreenMail = GreenMail(ServerSetup(2525, null, "smtp"))

    @Value("\${spring.mail.properties.mail.smtp.from}")
    private lateinit var from: String

    @Value("\${spring.mail.username}")
    private lateinit var username: String

    @Value("\${spring.mail.password}")
    private lateinit var password: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        smtp.setUser(username, password)
        smtp.start()

        url = "http://127.0.0.1:$port/v1/mail/messages"
    }

    @AfterEach
    fun tearDown() {
        smtp.stop()
    }

    @Test
    public fun invoke() {
        // GIVEN
        val request = createRequest()

        // WHEN
        val response = rest.postForEntity(url, request, SendMessageResponse::class.java)

        // THEN
        assertEquals(HttpStatus.OK, response.statusCode)

        val message = smtp.receivedMessages[0]
        val body = IOUtils.toString(message.inputStream)
        assertEquals(InternetAddress(from), message.sender)
        assertEquals(request.content.subject, message.subject)
        assertEquals(InternetAddress(request.recipient.email, request.recipient.displayName), message.allRecipients[0])
        assertTrue(message.contentType.contains(request.content.mimeType!!))
        assertTrue(body.contains(request.content.body))
    }

    private fun createRequest() = SendMessageRequest(
        recipient = Party("ray.sponsible@gmail.com", "Ray Sponsible"),
        content = Message(
            subject = "Hello world",
            language = "en",
            mimeType = "text/plain",
            body = "Yo man"
        )
    )
}
