package org.example.rest

import org.example.dto.BotAppUser
import org.example.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("users")
class UserController(val userService: UserService) {
    @PostMapping("")
    fun addUser(@RequestBody user: BotAppUser) { userService.addUser(user) }
}