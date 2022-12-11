package com.studentbot.chat;

import com.studentbot.main.MyLogger;
import com.studentbot.schedule.Day;
import com.studentbot.schedule.ArrayDay;
import java.io.File;
import java.io.FileWriter;
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
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class MyChat {
    
    public final long id;
    private String action = null;
    
    static private HashMap<Long,MyChat> chats = new HashMap<>();
    
    public static MyChat getChat(long id){
        
        addChat(id);
        
        if (chats.containsKey(id)) {
            return chats.get(id);
        }
        else {
            MyChat tmp = new MyChat(id);
            chats.put(id, tmp);
            return tmp;
        }
    }
    
    private static void addChat(long id){
        
        File dir = new File("..\\..\\Data\\Chats\\"+id);
        if (dir.exists()) return;
        
        dir.mkdirs(); 
        
        File f = new File("..\\..\\Data\\Chats\\"+id+"\\group.txt");
        
        try (FileWriter wf = new FileWriter(f)){
            f.createNewFile();
            wf.write("https://rasp.sstu.ru/rasp/group/135");
        } catch (IOException ex) {
            MyLogger.logger(ex, "Не удалось создать файл/записать в файл ..\\Data\\Chats\\"+id+"group.txt");
        }
        
        File f2 = new File("..\\..\\Data\\Chats\\"+id+"\\push.txt");
        
        try (FileWriter wf2 = new FileWriter(f2)){
            f2.createNewFile();
            wf2.write("off");
        } catch (IOException ex) {
            MyLogger.logger(ex, "Не удалось создать файл/записать в файл ..\\Data\\Chats\\"+id+"push.txt");
        }
    }
    
    private MyChat(long id){
        this.id = id;
    }

    public void setAction(String action) {
        this.action = action;
    }
    
    public String getAction(){
        return this.action;
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
            MyLogger.logger(ex, "Не удалось считать данные из ..\\Data\\Chats\\"+this.id+"\\group.txt");
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
                MyLogger.logger(ex, "Не удалось создать файл/записать в файл ..\\Data\\Chats\\"+this.id+"\\Schedule"+i+".txt");
            }
        }
    }
    
    public String getDaySchedule(int i){

        String res = "";
        
        File f = new File("..\\..\\Data\\Chats\\"+this.id+"\\Schedule\\"+i+".txt");
        
        try (Scanner sc = new Scanner(f);){
            while(sc.hasNextLine())res += sc.nextLine() + "\n";
        } catch (IOException ex) {
            MyLogger.logger(ex, "Не удалось считать данные из ..\\Data\\Chats\\"+this.id+"\\Schedule\\"+i+".txt");
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
                MyLogger.logger(ex, "Не удалось считать данные из ..\\Data\\Chats\\"+this.id+"\\Schedule\\"+file.getName()+".txt");
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
    
    public List<List<InlineKeyboardButton>> getClearNListButton(){
        
        List<List<InlineKeyboardButton>> vars = new ArrayList<>();
        
        vars.add(new ArrayList<>());
            
        vars.get(vars.size()-1).add(InlineKeyboardButton
                            .builder()
                            .text("Очистить список")
                            .callbackData("clear NList")
                            .build());
        
        return vars;
    }
    
    public List<List<InlineKeyboardButton>> getGroupButton(){
        
        List<List<InlineKeyboardButton>> vars = new ArrayList<>();
        
        vars.add(new ArrayList<>());
            
        vars.get(vars.size()-1).add(InlineKeyboardButton
                            .builder()
                            .text("Попробовать снова")
                            .callbackData("try /group")
                            .build());
        
        return vars;
    }
    
    public List<List<InlineKeyboardButton>> getRandomButton(){
        
        List<List<InlineKeyboardButton>> vars = new ArrayList<>();
        
        vars.add(new ArrayList<>());
            
        vars.get(vars.size()-1).add(InlineKeyboardButton
                            .builder()
                            .text("Попробовать снова")
                            .callbackData("try /random")
                            .build());
        
        return vars;
    }
    
    public void addToNList(User user){
        
        File dir = new File("..\\..\\Data\\Chats\\"+this.id+"\\UsersQuery");
        
        if (!dir.exists()) dir.mkdirs();
        
        File file = new File("..\\..\\Data\\Chats\\"+this.id+"\\UsersQuery\\"+user.getId()+".txt");
            
        try (FileWriter wr = new FileWriter(file)){
            file.createNewFile();
            wr.write(user.getFirstName());
        } catch (IOException ex) {
            MyLogger.logger(ex, "Не удалось создать файл/записать в файл ..\\Data\\Chats\\"+this.id+"\\UsersQuery" + user.getId() + ".txt");
        }
    }
    
    public String getNList(){
        
        File userDir = new File("..\\..\\Data\\Chats\\"+this.id+"\\UsersQuery");
        if (!userDir.exists()) return null;
        
        File[] users = userDir.listFiles();

        String res = "Просили не отмечать:\n";
        int i = 0;
        
        for (File file : users){
            
            try(Scanner sc = new Scanner(file)){
                res += ++i + ") "+ sc.nextLine() + "\n";
            } catch (IOException ex) {
                MyLogger.logger(ex, "Не удалось считать данные из ..\\Data\\Chats\\"+this.id+"\\UsersQuery\\"+file.getName()+".txt");
            } 
        }
        
        return res;
    }
    
    public void addToСList(String c){
        
        File dir = new File("..\\..\\Data\\Chats\\"+this.id+"\\C");
        
        if (!dir.exists()) dir.mkdirs();
        
        int i = dir.listFiles().length;
        
        File file = new File("..\\..\\Data\\Chats\\"+this.id+"\\C\\"+i+".txt");
            
        try (FileWriter wr = new FileWriter(file)){
            file.createNewFile();
            wr.write(c);
        } catch (IOException ex) {
            MyLogger.logger(ex, "Не удалось создать файл/записать в файл ..\\Data\\Chats\\"+this.id+"\\C\\" + i + ".txt");
        }
    }
    
    public String getC(){
        
        File userDir = new File("..\\..\\Data\\Chats\\"+this.id+"\\C");
        if (!userDir.exists()) return "Cписок цитат пуст";
        
        File[] users = userDir.listFiles();

        int i = (int) (Math.random()*users.length);
            
        try(Scanner sc = new Scanner(users[i])){
            return sc.nextLine();
        } catch (IOException ex) {
            MyLogger.logger(ex, "Не удалось считать данные из ..\\Data\\Chats\\"+this.id+"\\C\\"+i+".txt");
            return "Не удалось загрузить цитату";
        } 
    }
    
    public void clearCList(){
        
        File userDir = new File("..\\..\\Data\\Chats\\"+this.id+"\\C");
        if (!userDir.exists()) return;
        
        File[] users = userDir.listFiles();
        for (File file : users)file.delete();

        userDir.delete();
    }
    
    public void push(String state){
        File f = new File("..\\..\\Data\\Chats\\"+this.id+"\\push.txt");
        
        try(FileWriter wf = new FileWriter(f)){
            if (!f.exists()) f.createNewFile();
            wf.write(state);
        } catch (IOException ex) {
            MyLogger.logger(ex, "Не удалось внести данные в ..\\..\\Data\\Chats\\"+this.id+"\\push.txt");
        }
    }
}
        



