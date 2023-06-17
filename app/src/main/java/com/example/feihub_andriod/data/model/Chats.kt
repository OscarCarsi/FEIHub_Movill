package com.example.feihub_andriod.data.model

import java.util.Date

class Chats {
    class Chat {
        var username: String? = null
        var message: String? = null
        var dateOfMessage: Date? = null
        var dateOfMessageString: String? = null
        var dateAPI: String? = null

        override fun toString(): String {
            return "$username: $message"
        }
    }

    var statusCode: Int = 0
    var participants: Array<Participant>? = null
    var messages: Array<Message>? = null
    var chats: Array<Chat>? = null

    class Participant {
        var username: String? = null
        var _id: String? = null
    }

    class Message {
        var username: String? = null
        var message: String? = null
        var dateOfMessage: Date? = null

        override fun toString(): String {
            return "${dateOfMessage.toString()} $username: $message"
        }
    }

    class DateOfMessage {
        var date: Date? = null
    }
}
