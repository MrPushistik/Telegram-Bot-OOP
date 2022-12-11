package com.studentbot.main;

import com.studentbot.chat.MyChat;
import com.studentbot.schedule.ArrayDay;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
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
        
        System.out.println(update);
        
        if (update.hasCallbackQuery()){
               
            Message message = update.getCallbackQuery().getMessage();
            MyChat chat = MyChat.getChat(message.getChatId());  
            String callBack = update.getCallbackQuery().getData();
            
            if (callBack.contains("schedule:")){
                int idx = Integer.parseInt(callBack.substring(10, callBack.length()));
                simpleTextMeaasge(message, chat.getDaySchedule(idx) +"/n точное время отпраки: " + new GregorianCalendar().getTime(), null);
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
            else if (callBack.contains("try /group")){
                simpleTextMeaasge(message, "Введите группу:", null);
                chat.setAction("REPLY_GROUP");
            }
            else if (callBack.contains("try /random")){
                simpleTextMeaasge(message, "Введите кол-во вариантов", null);
                chat.setAction("REPLY_RANDOM");
            }
            
        }
        
        if(update.hasMessage()){
            
            Message message = update.getMessage();
            MyChat chat = MyChat.getChat(message.getChatId());
            
            
            if(message.hasText()){    
                
                if ("REPLY_GROUP".equals(chat.getAction())){
                    
                    String group = message.getText();
                    
                    try {
                        if (chat.setGroup(group))
                            simpleTextMeaasge(message, "Группа " + group + " успешно установлена", null);
                        else
                            simpleTextMeaasge(message, "Группа не была установлена. Убедитесь в существовании группы " + group, InlineKeyboardMarkup.builder().keyboard(chat.getGroupButton()).build());
                    } catch (IOException ex) {
                        simpleTextMeaasge(message, "К сожалению, операция невозможна на данный момент", null);
                        MyLogger.logger(ex, "Некорретная работа setGroup");
                    }
                }
                else if ("REPLY_RANDOM".equals(chat.getAction())){

                    String numberStr = message.getText();
                    
                    try{
                        int i = Integer.parseInt(numberStr);
                        if (i <= 1) throw new IllegalArgumentException();
                        int res = (int) (Math.random()*i) + 1;
                        simpleTextMeaasge(message, ("Результат: " + res), null);
                    }
                    catch(NumberFormatException ex){
                        simpleTextMeaasge(message, "Введено не число", InlineKeyboardMarkup.builder().keyboard(chat.getRandomButton()).build());
                    }
                    catch(IllegalArgumentException ex){
                        simpleTextMeaasge(message, "Кол-во вариантов должно быть не менее 2", InlineKeyboardMarkup.builder().keyboard(chat.getRandomButton()).build());
                    }
                }
                else if ("REPLY_SPAM".equals(chat.getAction())){
                    for (int i = 0; i < 4; i++){
                       simpleTextMeaasge(message, message.getText(), null); 
                    }
                }
                else if ("REPLY_С".equals(chat.getAction())){
                    chat.addToСList(message.getText()); 
                    simpleTextMeaasge(message, "Цитата установлена", null); 
                }
                
                chat.setAction(null);
                
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
                    else if (check(message, getBotUsername(),"/getnlist")){
                        String tmp = chat.getNList();
                        if (tmp == null)
                            simpleTextMeaasge(message, "Список пуст", null);
                        else
                            simpleTextMeaasge(message, chat.getNList(), InlineKeyboardMarkup.builder().keyboard(chat.getClearNListButton()).build()); 
                    }
                    else if (check(message, getBotUsername(),"/pushon")){
                        chat.push("on");
                        simpleTextMeaasge(message, "Уведомления включены", null);
                    }
                    else if (check(message, getBotUsername(),"/pushoff")){
                        chat.push("off");
                        simpleTextMeaasge(message, "Уведомления отключены", null);
                    }
                    else if (check(message, getBotUsername(),"/spam")){
                        simpleTextMeaasge(message, "Введите сообщение", null);
                        chat.setAction("REPLY_SPAM");
                    }
                    else if (check(message, getBotUsername(),"/random")){
                        simpleTextMeaasge(message, "Введите кол-во вариантов", null);
                        chat.setAction("REPLY_RANDOM");
                    }
                    else if (check(message, getBotUsername(),"/c")){
                        simpleTextMeaasge(message, "Введите цитату", null);
                        chat.setAction("REPLY_С");
                    }
                    else if (check(message, getBotUsername(),"/getc")){
                        simpleTextMeaasge(message, chat.getC(), null);
                    }
                    else if (check(message, getBotUsername(),"/clearc")){
                        chat.clearCList();
                        simpleTextMeaasge(message, "Циатник очищен", null);
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
