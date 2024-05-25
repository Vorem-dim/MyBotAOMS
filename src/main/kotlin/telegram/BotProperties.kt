package org.example.telegram

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.stereotype.Component

@Component
@Configuration
@PropertySource("classpath:bot.properties")
@ConfigurationProperties(prefix = "bot")
class BotProperties() {
    var username: String = ""
    var token: String = ""
}
