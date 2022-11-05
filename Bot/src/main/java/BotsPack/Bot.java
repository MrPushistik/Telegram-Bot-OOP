package BotsPack;

import Schedule.ArrayDay;
import java.io.FileReader;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Bot extends TelegramLongPollingBot{
    
    @Override
    public String getBotToken(){
        
        String token = null;
        
        try(FileReader f = new FileReader("..\\..\\Data\\token.txt")){ 
            Scanner sc = new Scanner(f);
            token = sc.nextLine();
        } catch (IOException ex) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);  
        }
        return token;
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

                try {
                     execute(SendMessage
                        .builder()
                        .text(chat.getDaySchedule(idx))
                        .chatId(message.getChatId().toString())
                        .build());
                } catch (TelegramApiException ex) {
                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                }
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
                        try {
                            execute(SendMessage
                                    .builder()
                                    .text("Группа успешно установлена")
                                    .chatId(message.getChatId().toString())
                                    .build());
                        } catch (TelegramApiException ex) {
                            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                    } catch (IOException ex) {
                        try {
                            execute(SendMessage
                                    .builder()
                                    .text("Группа не была установлена")
                                    .chatId(message.getChatId().toString())
                                    .build());
                        } catch (TelegramApiException ex1) {
                            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex1);
                        }
                    }
                    chat.setAction("/noaction");
                }
                
 //=================================================================================================
                if (message.hasEntities()){
                    
                    //CALL /SCHEDULE

                    if(chat.check(message.getEntities().get(0).getText(), getBotUsername(),"/schedule")){
                          
                        try {
                            chat.fillSchedule(new ArrayDay(Jsoup.connect(chat.getGroup()).get()));
                        } catch (IOException ex) {
                            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            return;
                        }
                        
                        List<List<InlineKeyboardButton>> tmp = chat.getScheduleButtons();

                        if(tmp!= null){
                            try {
                                execute(SendMessage
                                        .builder()
                                        .text("Выберите день")
                                        .chatId(message.getChatId().toString())
                                        .replyMarkup(InlineKeyboardMarkup.builder().keyboard(tmp).build())
                                        .build());
                            } catch (TelegramApiException ex) {
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        else{
                            try {
                                execute(SendMessage
                                        .builder()
                                        .text("К сожалению, расписание отсутствует")
                                        .chatId(message.getChatId().toString())
                                        .build());
                            } catch (TelegramApiException ex) {
                                Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        chat.setAction("/schedule");
                    }

                    //CALL /GROUP 

                    else if(chat.check(message.getEntities().get(0).getText(), getBotUsername(),"/group")){
                        try {
                            execute(SendMessage
                                    .builder()
                                    .text("Введите группу")
                                    .chatId(message.getChatId().toString())
                                    .build());
                        } catch (TelegramApiException ex) {
                            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        chat.setAction("/group");
                    }
                }
            }
        }
        
        
    }
    
    public static void main(String[] args) throws TelegramApiException {
        Bot bot = new Bot();
        TelegramBotsApi telegramBotApi =  new TelegramBotsApi(DefaultBotSession.class);
        BotSession registerBot = telegramBotApi.registerBot(bot);
        
    }
}
