package com.studentbot.main;

import com.studentbot.chat.MyChat;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface BotExecutor {
    void execute(Bot bot, MyChat chat, Message mes);
}
