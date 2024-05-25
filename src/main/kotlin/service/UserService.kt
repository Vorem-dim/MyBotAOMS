package org.example.service

import org.example.dto.BotAppUser
import org.springframework.stereotype.Component

@Component
class UserService {
    private val users: ArrayList<BotAppUser> = ArrayList()

    fun getUser(userId: Long): BotAppUser? = users.lastOrNull { user -> user.id == userId }

    fun addUser(user: BotAppUser) { users.add(user) }
}