/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BotsPack;

import static BotsPack.Bot.url;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.IIOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Chat {
    
    final long id;
    
    public Chat(long id){
        this.id = id;
        addChat();
    }
    
    private void addChat(){
        File dir = new File("..\\..\\Data\\Chats\\"+id);
        if (dir.exists()) return;
        
        dir.mkdirs(); 
        File f = new File("..\\..\\Data\\Chats\\"+id+"\\group.txt");
        try {
            f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void addGroup(String group) throws IOException{
        
        Document document = Jsoup.connect("https://rasp.sstu.ru/").get();
        Elements groups = document.select(".card > .collapse > .card-body > .groups > .col > .no-gutters > .group > a");
        
        for (Element e : groups){
            if (e.text().equals(group)){
                String res = "https://rasp.sstu.ru" + e.attr("href");
                System.out.println(res);
                
                FileWriter f = new FileWriter("..\\..\\Data\\Chats\\"+id+"\\group.txt");
                f.write(res);
                f.close();
                return;
            }
        }
        
        throw new IOException();
    }
    
    public String getGroup(){
        
        String res = null;
        
        try (FileReader f = new FileReader("..\\..\\Data\\Chats\\"+id+"\\group.txt")) {
            Scanner sc = new Scanner(f);
            res = sc.nextLine();
        } catch (IOException ex) {
            Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
        } 
        
        return res;
    }
}
        



