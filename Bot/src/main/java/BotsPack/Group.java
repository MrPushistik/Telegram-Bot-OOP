
package BotsPack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Group {
    
    public static boolean check(String command, String bot){
        return command.equals("/group") || command.equals("/group" + bot);
    }
    
    public static void setGroup(long id, String group){
        File f = new File("..\\..\\Data\\Chats\\"+id+".txt");
        
        try {
            FileWriter w = new FileWriter(f);
            w.write(group);
        } catch (IOException ex) {
            Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String getGroup(long id, String group){
        
        File f = new File("..\\..\\Data\\Chats\\"+id+"\\group.txt");
        try {
            Scanner sc = new Scanner(f);
            return sc.nextLine();
        } catch (IOException ex) {
            Logger.getLogger(Group.class.getName()).log(Level.SEVERE, null, ex);
            return "б1-ИФСТ-24";
        }

    }
    
    public static void action() {
        try(FileWriter f = new FileWriter("..\\..\\Data\\action.txt")){
            f.write("/group");
        } catch (IOException ex) {
            Logger.getLogger(Schedule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
