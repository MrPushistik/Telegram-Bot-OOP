package com.studentbot.chat;

import com.studentbot.schedule.Day;
import com.studentbot.schedule.ArrayDay;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class Chat {
    
    final long id;
    private String action = null;
    
    static private HashMap<Long,Chat> chats = new HashMap<>();
    
    public static Chat getChat(long id){
        if (chats.containsKey(id)) {
            return chats.get(id);
        }
        else {
            Chat tmp = new Chat(id);
            chats.put(id, tmp);
            return tmp;
        }
    }
    
    private Chat(long id){
        this.id = id;
        addChat();
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    public String getAction(){
        return this.action;
    }
    
    private void addChat(){
        
        File dir = new File("..\\..\\Data\\Chats\\"+this.id);
        if (dir.exists()) return;
        
        dir.mkdirs(); 
        
        File f = new File("..\\..\\Data\\Chats\\"+this.id+"\\group.txt");
        
        try (FileWriter wf = new FileWriter(f)){
            f.createNewFile();
            wf.write("https://rasp.sstu.ru/rasp/group/135");
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean setGroup(String group) throws IOException {
        
        Document document = Jsoup.connect("https://rasp.sstu.ru/").get();
        Elements groups = document.select(".card > .collapse > .card-body > .groups > .col > .no-gutters > .group > a");
        
        for (Element e : groups){
            if (e.text().equals(group)){
                
                String res = "https://rasp.sstu.ru" + e.attr("href");
                
                try (FileWriter f = new FileWriter("..\\..\\Data\\Chats\\"+this.id+"\\group.txt")) {
                    f.write(res);
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    public String getGroup(){
        
        String res = null;
        
        File f = new File("..\\..\\Data\\Chats\\"+this.id+"\\group.txt");
        
        try (Scanner sc = new Scanner(f);) {
            res = sc.nextLine();
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return res;
    }
    
    public void fillSchedule(ArrayDay arr){
        
        List<Day> days = arr.getDays();
        
        File dir = new File("..\\..\\Data\\Chats\\"+this.id+"\\Schedule");
        
        if (dir.exists()) {
            File[] contents = dir.listFiles();
            if (contents != null) for (File f : contents) f.delete();
            dir.delete();
        }
        
        dir.mkdirs();
     
        for (int i = 0; i <= 5; i++){
            
            Day d = days.get(arr.getCurrDay() + i);
            
            File f = new File("..\\..\\Data\\Chats\\"+this.id+"\\Schedule\\"+i+".txt");
            
            try (FileWriter wf = new FileWriter(f)) {
                f.createNewFile();
                wf.write(d.toString());  
            } catch (IOException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public String getDaySchedule(int i){

        String res = "";
        
        File f = new File("..\\..\\Data\\Chats\\"+this.id+"\\Schedule\\"+i+".txt");
        
        try (Scanner sc = new Scanner(f);){
            while(sc.hasNextLine())res += sc.nextLine() + "\n";
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    public List<List<InlineKeyboardButton>> getScheduleButtons(){
        
        List<List<InlineKeyboardButton>> vars = new ArrayList<>();
        
        File dir = new File("..\\..\\Data\\Chats\\"+this.id+"\\Schedule");
        File[] files;
        
        if (dir.exists()) files = dir.listFiles();
        else return null;
        
        for (File file : files) {
            
            String name = "День";
            
            try (Scanner sc = new Scanner(file);){
                name = sc.nextLine();  
                name = name.substring(4, name.length()-4);
            } catch (IOException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            }

            vars.add(new ArrayList<>());
            
            vars.get(vars.size()-1).add(InlineKeyboardButton
                            .builder()
                            .text(name)
                            .callbackData("schedule: " + file.getName().substring(0, 1))
                            .build());
        }
       
        return vars;
    }
    
    public void addToNList(User user){
        
        File dir = new File("..\\..\\Data\\Chats\\"+this.id+"\\UsersQuery");
        
        if (!dir.exists()) dir.mkdirs();
        
        File file = new File("..\\..\\Data\\Chats\\"+this.id+"\\UsersQuery\\"+user.getId()+".txt");
            
        try {
            file.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
              
        try (FileWriter wr = new FileWriter(file)){   
            wr.write(user.getFirstName());
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getNList(){
        
        File usersDir = new File("..\\..\\Data\\Chats\\"+this.id+"\\UsersQuery");
        if (!usersDir.exists()) return "Список пуст";
        
        File[] userDir = usersDir.listFiles();

        String res = "Просили не отмечать:\n";
        int i = 0;
        
        for (File file : userDir){
            
            File [] userName = 
                file.listFiles((File dir, String name) -> "firstname.txt".equals(name));
            
            try(Scanner sc = new Scanner(userName[0])){
                res += ++i + ") "+ sc.nextLine() + "\n";
            } catch (IOException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        
        return res;
    }
    
    public boolean check (Message message, String botName, String expected){
        
        String happened = message.getEntities().get(0).getText();
        
        if (happened != null)
            return happened.equals(expected) || happened.equals(expected + botName);
        
        return false;
    }
}
        



