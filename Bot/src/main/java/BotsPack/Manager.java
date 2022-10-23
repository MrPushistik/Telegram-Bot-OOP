package BotsPack;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Manager {
    
    public static void addChatId(long id){
        try {
            File f = new File("..\\..\\Data\\Chats\\"+id+".txt");
            if (!f.exists()) f.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(Manager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

}
