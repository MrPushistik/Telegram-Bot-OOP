package BotsPack;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class Schedule {
    
    public static void fillSchedule(ArrayDay arr){
        
        List<Day> days = arr.getDays();
        
        File dir = new File("..\\..\\Data\\Schedule");
        if (dir.exists()) {
            File[] contents = dir.listFiles();
            if (contents != null) for (File f : contents) f.delete();
            dir.delete();
        }
        dir.mkdirs();
     
        for (int i = 0; i <= 5; i++){
            
            Day d = days.get(arr.currDay + i);
            
            File f = new File("..\\..\\Data\\Schedule\\"+i+".txt");
            
            try (FileWriter wf = new FileWriter(f)) {
                f.createNewFile();
                wf.write(d.toString());  
            } catch (IOException ex) {
                Logger.getLogger(Schedule.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static ArrayDay setSchedule(Document document){
        
        List<Day> arrDays = new ArrayList<>(); int curr = 0;
        Elements daysNode = document.select(".day:not(.day-color-red):not(.day-color-blue)");
            
        for (int i = 0; i < daysNode.size(); i++){
          
            Element dayNode = daysNode.get(i);
            if (dayNode.hasClass("day-current")) curr = i;
            
            List<Lesson> arrLessons = new ArrayList<>();
            Elements lessonsNode = dayNode.select(".day-lesson > div");
                
            for (Element lessonNode : lessonsNode){
                    
                String time = lessonNode.select(".lesson-hour").text();
                String room = lessonNode.select(".lesson-room").text();
                String name = lessonNode.select(".lesson-name").text();
                String type = lessonNode.select(".lesson-type").text();
                    
                arrLessons.add(new Lesson(time, room, name, type));
            } 
            
            String tmp = dayNode.selectFirst(".day-header > div").text();
            arrDays.add(new Day(arrLessons,tmp.substring(0, tmp.length()-5),tmp.substring(tmp.length()-5, tmp.length())));
        }
        
        ArrayDay res = new ArrayDay(arrDays, curr);

        return res;
    }
    
    public static List<List<InlineKeyboardButton>> getScheduleButtons(){
        
        List<List<InlineKeyboardButton>> vars = new ArrayList<>();
        
        File dir = new File("..\\..\\Data\\Schedule");
        File[] files = null;
        if (dir.exists())  files = dir.listFiles();
        else return null;
        
        for (File file : files) {
            
            String name = "День";
            
            try (FileReader rf = new FileReader(file)){
                Scanner sc = new Scanner(rf);
                name = sc.nextLine();  
                name = name.substring(4, name.length()-4);
            } catch (IOException ex) {
                Logger.getLogger(Schedule.class.getName()).log(Level.SEVERE, null, ex);
            }

            vars.add(new ArrayList<>());
            
            vars.get(vars.size()-1).add(
                    InlineKeyboardButton
                            .builder()
                            .text(name)
                            .callbackData("schedule: " + file.getName().substring(0, 1))
                            .build());
        }
       
        return vars;
    }
    
    public static String getDaySchedule(int i){

        String res = "";
        
        try (FileReader daySchedule = new FileReader("..\\..\\Data\\Schedule\\"+i+".txt")){

            Scanner sc = new Scanner(daySchedule);

            while(true){
                if(sc.hasNextLine()) res += sc.nextLine() + "\n";
                else break;  
            }
             
        } catch (IOException ex) {
            Logger.getLogger(Schedule.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return res;
    }
    
    public static boolean check(String command, String bot){
        return command.equals("/schedule") || command.equals("/schedule" + bot);
    }
    
    public static void action() {
        try(FileWriter f = new FileWriter("..\\..\\Data\\action.txt")){
            f.write("/schedule");
        } catch (IOException ex) {
            Logger.getLogger(Schedule.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
