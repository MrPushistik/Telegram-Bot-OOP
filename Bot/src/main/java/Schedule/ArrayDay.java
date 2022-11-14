package Schedule;

import Schedule.Lessons.*;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ArrayDay {
    
    private int currDay = 0;
    private List<Day> days = new ArrayList<>();

    public ArrayDay(Document document){
        
        Elements daysNode = document.select(".day:not(.day-color-red):not(.day-color-blue)");
            
        for (int i = 0; i < daysNode.size(); i++){

            Element dayNode = daysNode.get(i);
            if (dayNode.hasClass("day-current")) this.currDay = i;

            List<Lesson> arrLessons = new ArrayList<>();
            Elements lessonsNode = dayNode.select(".day-lesson > div");
  
            String tmp = dayNode.selectFirst(".day-header > div").text();
            
            if (lessonsNode.isEmpty()){
                this.days.add(new Day(tmp.substring(0, tmp.length()-5),tmp.substring(tmp.length()-5, tmp.length())));
            }
            else {
                for (Element lessonNode : lessonsNode){

                    String time = lessonNode.select(".lesson-hour").text();
                    String room = lessonNode.select(".lesson-room").text();
                    String name = lessonNode.select(".lesson-name").text();


                    String type = lessonNode.select(".lesson-type").text();

                    switch(type){
                        case "(лекц)": arrLessons.add(new Lection(time, room, name)); break;
                        case "(прак)": arrLessons.add(new Practice(time, room, name)); break;
                        case "(зач)": arrLessons.add(new Test(time, room, name)); break;
                        case "(экз)": arrLessons.add(new Exam(time, room, name)); break;
                        case "(лаб)": arrLessons.add(new Lab(time, room, name)); break;
                    } 


                } 

                this.days.add(new BusyDay(tmp.substring(0, tmp.length()-5),tmp.substring(tmp.length()-5, tmp.length()),arrLessons));
            }  
        }
    }

    public List<Day> getDays(){
        List<Day> copy = new ArrayList<>();
        for (Day d : this.days) copy.add((Day)d.clone());
        return copy;
    }
    
    public int getCurrDay(){
        return this.currDay;
    }
    
    @Override
    public String toString(){
        String res = "";
        for (Day d : days) res += d + "\n";
        return res;
    }
}
