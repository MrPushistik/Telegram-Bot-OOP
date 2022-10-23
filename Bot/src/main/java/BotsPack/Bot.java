package BotsPack;

import java.io.IOException;
import java.util.List;
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

    static String url = "https://rasp.sstu.ru/rasp/group/135";
    
    @Override
    public String getBotToken(){
        return "5781517112:AAEBHPVQ-zY8I2wXCyk9w8CiyNev80fIiP4";
    }
    
    @Override
    public String getBotUsername() {
        return "@MrPushistikBot";
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        

     
        ArrayDay schedule = null;
        try {
            schedule = Schedule.setSchedule(Jsoup.connect(url).get());
        } catch (IOException ex) {
            Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
        }
  
        Schedule.fillSchedule(schedule);
        
        
        if(update.hasMessage()){
            
            Manager.addChatId(update.getMessage().getChatId());
                
            Message message = update.getMessage();
            if(message.hasText() && message.hasEntities()){
 
                
                //CALL /SCHEDULE =================================================================
                
                if(Schedule.check(message.getEntities().get(0).getText(), getBotUsername())){
                    
                    List<List<InlineKeyboardButton>> tmp = Schedule.getScheduleButtons();
                    
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
                }
                
                //CALL /GROUP =================================================================
                
                else if(Group.check(message.getEntities().get(0).getText(), getBotUsername())){
                    try {
                        execute(SendMessage
                                .builder()
                                .text("Введите группу")
                                .chatId(message.getChatId().toString())
                                .build());
                    } catch (TelegramApiException ex) {
                        Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            }
        }
        
       
        if (update.hasCallbackQuery()){
            String callBack = update.getCallbackQuery().getData();
            Message message = update.getCallbackQuery().getMessage();
                
            //CALL /SCHEDULE BUTTON =================================================================
            
            if (callBack.contains("schedule:")){
                    
                int idx = Integer.parseInt(callBack.substring(10, callBack.length()));
                    
                try {
                    execute(SendMessage
                            .builder()
                            .text(Schedule.getDaySchedule(idx))
                            .chatId(message.getChatId().toString())
                            .build());
                } catch (TelegramApiException ex) {
                    Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
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
