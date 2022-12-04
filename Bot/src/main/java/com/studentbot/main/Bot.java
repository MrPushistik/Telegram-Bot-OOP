package com.studentbot.main;

import com.studentbot.chat.Chat;
import com.studentbot.schedule.ArrayDay;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import org.jsoup.Jsoup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Bot extends TelegramLongPollingBot{
    
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
            Chat chat = Chat.getChat(message.getChatId());  
            String callBack = update.getCallbackQuery().getData();
            
            if (callBack.contains("schedule:")){
                int idx = Integer.parseInt(callBack.substring(10, callBack.length()));
                simpleTextMeaasge(message, chat.getDaySchedule(idx), null);
            }
            else if (callBack.contains("clear NList")){
                
                File dir = new File("..\\..\\Data\\Chats\\" + chat.id + "\\UsersQuery");
                
                if (dir.exists()){
                    File [] files = dir.listFiles();
                    if (files != null) for (File file : files) file.delete();
                    dir.delete();
                } 
                
                simpleTextMeaasge(message, "Список очищен", null);
            }
        }
        
        if(update.hasMessage()){
            
            Message message = update.getMessage();
            Chat chat = Chat.getChat(message.getChatId());
            
            if(message.hasText()){    
                
                if ("REPLY_GROUP".equals(chat.getAction())){
                    
                    chat.setAction(null);
                    String group = message.getText();
                    
                    try {
                        if (chat.setGroup(group))
                            simpleTextMeaasge(message, "Группа " + group + " успешно установлена", null);
                        else
                            simpleTextMeaasge(message, "Группа не была установлена. Убедитесь в существовании группы " + group, null);
                    } catch (IOException ex) {
                        simpleTextMeaasge(message, "К сожалению, операция невозможна на данный момент", null);
                        MyLogger.logger(ex, "Некорретная работа setGroup");
                    }
                }
                
                if (message.hasEntities()){
                    
                    if(check(message, getBotUsername(),"/schedule")){
                          
                        try {
                            chat.fillSchedule(new ArrayDay(Jsoup.connect(chat.getGroup()).get()));
                        } catch (IOException ex) {
                            MyLogger.logger(ex, "Не удолость получить страницу распиания");
                            return;
                        }
                        
                        List<List<InlineKeyboardButton>> tmp = chat.getScheduleButtons();
                        
                        if(tmp!= null)
                            simpleTextMeaasge(message, "Выберите день", InlineKeyboardMarkup.builder().keyboard(tmp).build()); 
                        else
                            simpleTextMeaasge(message,"К сожалению, расписание отсутствует", null);
                    }
                    else if (check(message, getBotUsername(),"/group")){
                        
                        simpleTextMeaasge(message, "Введите группу:", null);
                        chat.setAction("REPLY_GROUP");
                    }
                    else if (check(message, getBotUsername(),"/n")){
                        StringHolder h = StringHolder.getStringHolder();
                        simpleTextMeaasge(message, message.getFrom().getFirstName() + h.getString(), null);
                        chat.addToNList(message.getFrom());
                    }
                    else if (check(message, getBotUsername(),"/getn")){
                        String tmp = chat.getNList();
                        if (tmp == null)
                            simpleTextMeaasge(message, "Список пуст", null);
                        else
                            simpleTextMeaasge(message, chat.getNList(), InlineKeyboardMarkup.builder().keyboard(chat.getClearNListButton()).build()); 
                    }
                }
            }
        }
    }
    
    //EXTRA FUNCTIONS
    
    public void simpleTextMeaasge(Message message, String txt, ReplyKeyboard reply){
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
    
    public boolean check (Message message, String botName, String expected){
        
        String happened = message.getEntities().get(0).getText();
        
        if (happened != null)
            return happened.equals(expected) || happened.equals(expected + botName);
        
        return false;
    }
    
    public static void main(String[] args) throws TelegramApiException {
        Bot bot = new Bot();
        TelegramBotsApi telegramBotApi =  new TelegramBotsApi(DefaultBotSession.class);
        BotSession registerBot = telegramBotApi.registerBot(bot); 
    }
}
