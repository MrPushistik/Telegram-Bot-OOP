package com.studentbot.main;

import com.studentbot.chat.MyChat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Bot extends TelegramLongPollingBot{
    
    BotFunctions tools = new BotFunctions();
    
    @Override
    public String getBotToken(){  
        
        File f = new File("..\\..\\Data\\token.txt");
        
        try(Scanner sc = new Scanner(f)){ 
            return sc.nextLine();
        } catch (IOException ex) {
            MyLogger.logger(ex, "Токен не считан");
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public String getBotUsername() {
        return "@MrPushistikBot";
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        
        if (update.hasCallbackQuery()){
               
            Message message = update.getCallbackQuery().getMessage();
            MyChat chat = MyChat.getChat(message.getChatId());  
            
            String callback = update.getCallbackQuery().getData();
            if (callback != null) tools.completeCallBack(callback, this, chat, message);
        }
        
        if(update.hasMessage()){
            
            Message message = update.getMessage();
            MyChat chat = MyChat.getChat(message.getChatId());
            
            if(message.hasText()){    
                
                String ans = chat.getAction();
                if (ans != null) tools.completeReply(ans, this, chat, message);
                chat.setAction(null);

                if (message.hasEntities()){    
                    String entity = message.getEntities().get(0).getText();
                    if (entity != null){
                        if (!entity.contains(getBotUsername())) entity += getBotUsername();
                        tools.completeFunction(entity, this, chat, message);  
                    }
                }  
            }
        }
    }

    public void simpleTextMessage(Message message, String txt, ReplyKeyboard reply){
        try {
            execute(SendMessage
            .builder()
            .text(txt)
            .chatId(message.getChatId().toString())
            .replyMarkup(reply)
            .build());
        } catch (TelegramApiException ex) {
            MyLogger.logger(ex, "Cообщение '" + message.getText() + "' не было отправлено");
        }
    }
    
    public static void main(String[] args) throws TelegramApiException {
        Bot bot = new Bot();
        TelegramBotsApi telegramBotApi =  new TelegramBotsApi(DefaultBotSession.class);
        BotSession registerBot = telegramBotApi.registerBot(bot); 
        
        timeSender(bot);
    }
    
    public static void timeSender(Bot bot){
        
        Calendar dateNow = new GregorianCalendar(TimeZone.getTimeZone("Asia/Amman"));
        Calendar dateReq = new GregorianCalendar(TimeZone.getTimeZone("Asia/Amman"));
        
        File f = new File("..\\..\\Data\\hours.txt");
        int delay;
        
        try(Scanner sc = new Scanner(f)){
            dateReq.set(Calendar.HOUR_OF_DAY, sc.nextInt()-2);
            dateReq.set(Calendar.MINUTE, sc.nextInt());
            dateReq.set(Calendar.SECOND, sc.nextInt());
            delay = sc.nextInt();
        } catch (FileNotFoundException ex) {
            dateReq.set(Calendar.HOUR_OF_DAY, 6-2);
            dateReq.set(Calendar.MINUTE, 30);
            dateReq.set(Calendar.SECOND, 0);
            delay = 86400000;
            MyLogger.logger(ex, "Не удалось считать данные из ..\\..\\Data\\hours.txt");
        }
        
        while (dateReq.compareTo(dateNow) == -1) dateReq.add(Calendar.MILLISECOND, delay); 
//        while (dateReq.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) dateReq.add(Calendar.MILLISECOND, delay); 
        
        Timer t = new Timer();
        t.schedule(new TimerTask(){
            
            @Override
            public void run() {
                File dir = new File("..\\..\\Data\\Chats");
                File [] files = dir.listFiles();
                
                for (File f : files){
                    
                    File subF = new File(f.getAbsolutePath() + "\\push.txt");
                    
                    boolean push = false;
                    
                    try(Scanner sc = new Scanner(subF)){    
                        push = "on".equals(sc.nextLine());
                    } catch (FileNotFoundException ex) {
                        MyLogger.logger(ex, "Не удалось считать данные из "+f.getAbsolutePath()+"\\push.txt");
                    }

                    if (push){  
                        String chatId = f.getName();
                        
                        Update update = new Update();
                        CallbackQuery callback = new CallbackQuery();
                        Message mes = new Message();
                        Chat chat = new Chat();
                        
                        chat.setId(Long.valueOf(chatId));
                        mes.setChat(chat);
                        callback.setMessage(mes);
                        callback.setData("schedule: 0");
                        
                        update.setCallbackQuery(callback);
                        bot.onUpdateReceived(update);
                    }
                }
            }
        }, dateReq.getTime(), delay);
    }
}
