package org.example

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Location
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

class Bot: TelegramLongPollingBot() {
    companion object {
        const val BOT_USERNAME: String = "AOMSwithBot"
        const val BOT_TOKEN: String = "7160098247:AAGQJhrrbzCQW7oqUXlB6lFosDZvNE1GpQ0"
    }

    enum class BotAnswer(val message: String) {
        START("Welcome to AOMS bot!"),
        VOICE("Your voice message duration: "),
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
                    sendMessage.also {
                        it.text = getCurrentLocation(location)
                        execute(it)
                    }
                }
            }
        }
    }

    private fun getCurrentLocation(location: Location): String = location.run {
        "Your current position ($latitude; $longitude)"
    }
}

fun main() {
    val telegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)
    telegramBotsApi.registerBot(Bot())
}