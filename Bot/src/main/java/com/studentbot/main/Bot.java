package com.studentbot.main;

import com.studentbot.chat.Chat;
import com.studentbot.schedule.ArrayDay;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        
        try(FileReader f = new FileReader("..\\..\\Data\\token.txt")){ 
            Scanner sc = new Scanner(f);
            return sc.nextLine();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    @Override
    public String getBotUsername() {
        return "@MrPushistikBot";
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        
        //ANSWER ON /SCHEDULE
      
        if (update.hasCallbackQuery()){
                   
            Chat chat = new Chat(update.getCallbackQuery().getMessage().getChatId());  
            
            String callBack = update.getCallbackQuery().getData();
            Message message = update.getCallbackQuery().getMessage();

            //CALL /SCHEDULE BUTTONS
            
            if (callBack.contains("schedule:")){
                int idx = Integer.parseInt(callBack.substring(10, callBack.length()));
                simpleTextMeaasge(message, chat.getDaySchedule(idx), null);
            }
            
            chat.setAction("/noaction");
        }
        
        if(update.hasMessage()){
            
            Chat chat = new Chat(update.getMessage().getChatId());  
            
            Message message = update.getMessage();
            
            if(message.hasText()){
                      
                //ANSWER ON /GROUP
                if ("/group".equals(chat.getAction())){
                    
                    try {
                        chat.setGroup(message.getText());
                        simpleTextMeaasge(message, "Группа успешно установлена", null);
                    } catch (IOException ex) {
                        simpleTextMeaasge(message, "Группа не была установлена", null);
                    }
                    chat.setAction("/noaction");
                }
                
                if (message.hasEntities()){
                    
                    //CALL /SCHEDULE
                    
                    if(chat.check(message.getEntities().get(0).getText(), getBotUsername(),"/schedule")){
                          
                        try {
                            chat.fillSchedule(new ArrayDay(Jsoup.connect(chat.getGroup()).get()));
                        } catch (IOException ex) {
                            logger(ex);
                            return;
                        }
                        
                        List<List<InlineKeyboardButton>> tmp = chat.getScheduleButtons();

                        if(tmp!= null)
                            simpleTextMeaasge(message, "Выберите день", InlineKeyboardMarkup.builder().keyboard(tmp).build()); 
                        else
                            simpleTextMeaasge(message,"К сожалению, расписание отсутствует", null);

                        chat.setAction("/schedule");
                    }

                    //CALL /GROUP 

                    else if(chat.check(message.getEntities().get(0).getText(), getBotUsername(),"/group")){
                        simpleTextMeaasge(message,"Введите группу", null);
                        chat.setAction("/group");
                    }
                }
            }
        }
    }
    
    //EXTRA FUNCTIONS
    
    public void logger(Exception ex){
        try(FileWriter f = new FileWriter("..\\..\\Data\\log.txt", true)){ 
           f.write(ex.toString());
        } catch (IOException ex1) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex1);
        } 
    }
    
    public void simpleTextMeaasge(Message message, String txt, ReplyKeyboard reply){
        try {
            execute(SendMessage
            .builder()
            .text(txt)
            .chatId(message.getChatId().toString())
            .replyMarkup(reply)
            .build());
        } catch (TelegramApiException ex) {
            logger(ex);
        }
    }
    
    public static void main(String[] args) throws TelegramApiException {
        Bot bot = new Bot();
        TelegramBotsApi telegramBotApi =  new TelegramBotsApi(DefaultBotSession.class);
        BotSession registerBot = telegramBotApi.registerBot(bot); 
    }
}
