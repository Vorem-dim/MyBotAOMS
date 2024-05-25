package org.example.telegram

import org.example.service.UserService
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.pow
import kotlin.time.toKotlinDuration
import org.slf4j.*

@Component
class Bot(
    telegramBotsApi: TelegramBotsApi,
    private val userService: UserService,
    private val properties: BotProperties
): TelegramLongPollingBot() {
    private var latitude: Double = .0
    private var longitude: Double = .0
    private var timeOfPreviousPosition: LocalDateTime? = null
    private val log: Logger = LoggerFactory.getLogger(Bot::class.java)

    enum class BotAnswer(val message: String) {
        START("Welcome to AOMS bot!"),
        VOICE("Your voice message duration: "),
        CURRENT_LOCATION("Your current position "),
        CHANGE_LOCATION("Your position changed on "),
        DEFAULT("Unknown command")
    }

    init { telegramBotsApi.registerBot(this) }

    override fun getBotUsername(): String = properties.username

    override fun getBotToken(): String = properties.token

    override fun onUpdateReceived(update: Update) {
        if (!update.hasMessage()) return

        update.message.apply {
            val sendMessage = SendMessage().also { it.setChatId(chatId) }

            log.info("Message from ${from.id} ${from.userName}")

            val user = userService.getUser(from.id)
            if (user == null) {
                sendMessage.also {
                    it.text = "not authorized user"
                    execute(it)
                }
                return
            }

            if (hasText()) {
                sendMessage.also {
                    it.text = when (text) {
                        "/start" -> BotAnswer.START.message
                        else -> BotAnswer.DEFAULT.message
                    }
                    execute(it)
                }
            } else if (hasVoice()) {
                sendMessage.also {
                    it.text = BotAnswer.VOICE.message + voice.duration
                    execute(it)
                }
            } else if (hasLocation()) {
                timeOfPreviousPosition?.let {
                    sendMessage.also {
                        it.text = with(location) {
                            BotAnswer.CHANGE_LOCATION.message +
                                    getDistance(latitude, longitude) +
                                    " for " + getTime()
                        }
                        execute(it)
                    }
                }

                latitude = location.latitude
                longitude = location.longitude
                timeOfPreviousPosition = LocalDateTime.now()

                sendMessage.also {
                    it.text = BotAnswer.CURRENT_LOCATION.message + getCoordinates()
                    execute(it)
                }
            }
        }
    }

    private fun getCoordinates(x: Double = latitude, y: Double = longitude): String = "($x; $y)"

    private fun getDistance(x: Double, y: Double): Double = ((latitude - x).pow(2) + (longitude - y).pow(2)).pow(.5)

    private fun getTime(): String = Duration
        .between(timeOfPreviousPosition, LocalDateTime.now())
        .toKotlinDuration()
        .toComponents { hours, minutes, seconds, _ -> "${hours}h:${minutes}m:${seconds}s" }
}