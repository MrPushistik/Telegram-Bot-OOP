/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentbot.main;

import com.studentbot.chat.MyChat;
import com.studentbot.schedule.ArrayDay;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.Jsoup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class BotFunctions {
      
    private Map<String, BotExecutor> functions = new HashMap<>();
    private Map<String,BotExecutor> replyes = new HashMap<>();
    private Map<String,BotExecutor> callbacks = new HashMap<>();
   
    {
        BotExecutor func1;
        func1 = (bot, chat, message) -> {
            try {
                chat.fillSchedule(new ArrayDay(Jsoup.connect(chat.getGroup()).get()));
            } catch (IOException ex) {
                MyLogger.logger(ex, "Не удолость получить страницу распиания");
                return;
            }

            List<List<InlineKeyboardButton>> tmp = chat.getScheduleButtons();

            if(tmp!= null)
                bot.simpleTextMessage(message, "Выберите день", InlineKeyboardMarkup.builder().keyboard(tmp).build()); 
            else
                bot.simpleTextMessage(message,"К сожалению, расписание отсутствует", null);
        };

        BotExecutor func2 = (bot, chat, message) -> {
             bot.simpleTextMessage(message, "Введите группу:", null);
             chat.setAction("REPLY_GROUP");
        };
        
        BotExecutor func3 = (bot, chat, message) -> {
            StringHolder h = StringHolder.getStringHolder();
            bot.simpleTextMessage(message, message.getFrom().getFirstName() + h.getString(), null);
            chat.addToNList(message.getFrom());
        };
        
        BotExecutor func4 = (bot, chat, message) -> {
            String tmp = chat.getNList();
            if (tmp == null)
                bot.simpleTextMessage(message, "Список пуст", null);
            else
                bot.simpleTextMessage(message, chat.getNList(), InlineKeyboardMarkup.builder().keyboard(chat.getClearNListButton()).build()); 
        };
        
        BotExecutor func5 = (bot, chat, message) -> {
            chat.push("on");
            bot.simpleTextMessage(message, "Уведомления включены", null);
        };
        
        BotExecutor func6 = (bot, chat, message) -> {
            chat.push("off");
            bot.simpleTextMessage(message, "Уведомления отключены", null);
        };
        
        BotExecutor func7 = (bot, chat, message) -> {
            bot.simpleTextMessage(message, "Введите сообщение", null);
            chat.setAction("REPLY_SPAM");
        };
        
        BotExecutor func8 = (bot, chat, message) -> {
            bot.simpleTextMessage(message, "Введите кол-во вариантов", null);
            chat.setAction("REPLY_RANDOM");
        };
        
        BotExecutor func9 = (bot, chat, message) -> {
            bot.simpleTextMessage(message, "Введите цитату", null);
            chat.setAction("REPLY_С");
        };
        
        BotExecutor func10 = (bot, chat, message) -> {
            bot.simpleTextMessage(message, chat.getC(), null);
        };
        
        BotExecutor func11 = (bot, chat, message) -> {
            chat.clearCList();
            bot.simpleTextMessage(message, "Циатник очищен", null);
        };
        
        BotExecutor func12 = (bot, chat, message) -> {
            chat.removeLastC();
            bot.simpleTextMessage(message, "Последняя цитата удалена", null);
        };
        
        BotExecutor repl1 = (bot,chat,message)->{
            String group = message.getText();

            try {
                if (chat.setGroup(group))
                bot.simpleTextMessage(message, "Группа " + group + " успешно установлена", null);
            else
                bot.simpleTextMessage(message, "Группа не была установлена. Убедитесь в существовании группы " + group, InlineKeyboardMarkup.builder().keyboard(chat.getGroupButton()).build());
            } catch (IOException ex) {
                bot.simpleTextMessage(message, "К сожалению, операция невозможна на данный момент", null);
                MyLogger.logger(ex, "Некорретная работа setGroup");
            }
        };
        
        BotExecutor repl2 = (bot,chat,message)->{
            String numberStr = message.getText();

            try{
                int i = Integer.parseInt(numberStr);
                if (i <= 1) throw new IllegalArgumentException();
                int res = (int) (Math.random()*i) + 1;
                bot.simpleTextMessage(message, ("Результат: " + res), null);
            }
            catch(NumberFormatException ex){
                bot.simpleTextMessage(message, "Введено не число", InlineKeyboardMarkup.builder().keyboard(chat.getRandomButton()).build());
            }
            catch(IllegalArgumentException ex){
                bot.simpleTextMessage(message, "Кол-во вариантов должно быть не менее 2", InlineKeyboardMarkup.builder().keyboard(chat.getRandomButton()).build());
            }
        };
        
        BotExecutor repl3 = (bot,chat,message)->{
            for (int i = 0; i < 4; i++) bot.simpleTextMessage(message, message.getText(), null); 
        };
        
        BotExecutor repl4 = (bot,chat,message)->{
            chat.addToСList(message.getText()); 
            bot.simpleTextMessage(message, "Цитата установлена", null); 
        };
        
        BotExecutor ex1 = (bot,chat,message)-> {bot.simpleTextMessage(message, chat.getDaySchedule(0), null);};
        BotExecutor ex2 = (bot,chat,message)-> {bot.simpleTextMessage(message, chat.getDaySchedule(1), null);};
        BotExecutor ex3 = (bot,chat,message)-> {bot.simpleTextMessage(message, chat.getDaySchedule(2), null);};
        BotExecutor ex4 = (bot,chat,message)-> {bot.simpleTextMessage(message, chat.getDaySchedule(3), null);};
        BotExecutor ex5 = (bot,chat,message)-> {bot.simpleTextMessage(message, chat.getDaySchedule(4), null);};
        BotExecutor ex6 = (bot,chat,message)-> {bot.simpleTextMessage(message, chat.getDaySchedule(5), null);};
        BotExecutor ex7 = (bot,chat,message)-> {
            chat.clearNList();
            bot.simpleTextMessage(message, "Список очищен", null);
        };

        functions.put("/schedule@MrPushistikBot", func1);
        functions.put("/group@MrPushistikBot", func2);
        functions.put("/n@MrPushistikBot", func3);
        functions.put("/getnlist@MrPushistikBot", func4);
        functions.put("/pushon@MrPushistikBot", func5);
        functions.put("/pushoff@MrPushistikBot", func6);
        functions.put("/spam@MrPushistikBot", func7);
        functions.put("/random@MrPushistikBot", func8);
        functions.put("/c@MrPushistikBot", func9);
        functions.put("/getc@MrPushistikBot", func10);
        functions.put("/clearc@MrPushistikBot", func11);
        functions.put("/removelastc@MrPushistikBot", func12);
  
        replyes.put("REPLY_GROUP", repl1);
        replyes.put("REPLY_RANDOM", repl2);
        replyes.put("REPLY_SPAM", repl3);
        replyes.put("REPLY_С", repl4);
        
        callbacks.put("schedule: 0", ex1);
        callbacks.put("schedule: 1", ex2);
        callbacks.put("schedule: 2", ex3);
        callbacks.put("schedule: 3", ex4);
        callbacks.put("schedule: 4", ex5);
        callbacks.put("schedule: 5", ex6);
        callbacks.put("clear NList", ex7);
        callbacks.put("try /group", func2);
        callbacks.put("try /random", func8);
    }
    
    public void completeFunction(String function, Bot bot, MyChat chat, Message mes){
        functions.get(function).execute(bot, chat, mes);
    }
    
    public void completeReply(String function, Bot bot, MyChat chat, Message mes){
        replyes.get(function).execute(bot, chat, mes);
    }
    
    public void completeCallBack(String function, Bot bot, MyChat chat, Message mes){
        callbacks.get(function).execute(bot, chat, mes);
    }
}
