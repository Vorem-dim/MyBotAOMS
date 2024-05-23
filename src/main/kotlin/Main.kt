package org.example

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.time.LocalDateTime
import kotlin.math.pow
import java.time.Duration
import kotlin.time.*

class Bot: TelegramLongPollingBot() {
    private var latitude: Double = .0
    private var longitude: Double = .0
    private var timeOfPreviousPosition: LocalDateTime? = null

    companion object {
        const val BOT_USERNAME: String = "AOMSwithBot"
        const val BOT_TOKEN: String = "7160098247:AAGQJhrrbzCQW7oqUXlB6lFosDZvNE1GpQ0"
    }

    enum class BotAnswer(val message: String) {
        START("Welcome to AOMS bot!"),
        VOICE("Your voice message duration: "),
        CURRENT_LOCATION("Your current position "),
        CHANGE_LOCATION("Your position changed on "),
        DEFAULT("Unknown command")
    }

    override fun getBotUsername(): String = BOT_USERNAME

    override fun getBotToken(): String = BOT_TOKEN

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage()) {
            update.message.apply {
                val sendMessage = SendMessage().also { it.setChatId(chatId) }

                if (hasText()) {
                    sendMessage.also {
                        it.text = when(text) {
                            "/start" -> BotAnswer.START.message
                            else -> BotAnswer.DEFAULT.message
                        }
                        execute(it)
                    }
                }
                else if (hasVoice()) {
                    sendMessage.also {
                        it.text = BotAnswer.VOICE.message + voice.duration
                        execute(it)
                    }
                }
                else if (hasLocation()) {
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
    }

    private fun getCoordinates(x: Double = latitude, y: Double = longitude): String = "($x; $y)"

    private fun getDistance(x: Double, y: Double): Double = ((latitude - x).pow(2) + (longitude - y).pow(2)).pow(.5)

    private fun getTime(): String = Duration
        .between(timeOfPreviousPosition, LocalDateTime.now())
        .toKotlinDuration()
        .toComponents { hours, minutes, seconds, _ -> "${hours}h:${minutes}m:${seconds}s" }
}

fun main() {
    val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
    telegramBotsApi.registerBot(Bot())
}