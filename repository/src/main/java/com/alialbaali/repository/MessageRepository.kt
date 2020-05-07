package com.alialbaali.repository

import android.content.SharedPreferences
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import com.alialbaali.local.MessageDao
import com.alialbaali.model.Message
import com.alialbaali.remote.MessageClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MessageRepository(private val messageClient: MessageClient, private val messageDao: MessageDao, private val sharedPreferences: SharedPreferences) {

    suspend fun insertMessageClient(token: String, message: Message) {
        withContext(Dispatchers.IO) {
            messageClient.insertMessage(AUTH_SCHEME.plus(token), message).let {
                messageDao.insertMessage(it)
            }
        }
    }

    suspend fun getMessagesClient() {
        withContext(Dispatchers.IO) {
            val messages = messageClient.getMessages(AUTH_SCHEME.plus(sharedPreferences.getString(TOKEN, null)), messageDao.count())
            messageDao.updateMessages(messages)
        }
    }

    suspend fun getMessagesDao(chatId: Int): LiveData<List<Message>> {
        return withContext(Dispatchers.Main) {
            messageDao.getMessages(chatId)
        }
    }

    suspend fun isMessageDelivered(messageId: Int) {
        withContext(Dispatchers.IO) {
            val value = messageClient.isMessageDelivered(AUTH_SCHEME.plus(sharedPreferences.getString(TOKEN, null)), messageId)
            if (value) {
                messageDao.updateDelivered()
            }
        }
    }

    suspend fun getLastMessage(chatId: Int): String {
        return withContext(Dispatchers.IO) {
            messageDao.getLastMessage(chatId)
        }
    }
}

