package Schedule;

import Schedule.Lessons.Lesson;
import java.util.ArrayList;
import java.util.List;

public class BusyDay extends Day {
    
    private List<Lesson> lessons;
    
    BusyDay(String date, String dateNum, List<Lesson> lessons){
        super(date, dateNum);
        setLessons(lessons);
    }
    
    BusyDay(BusyDay day){
        this(day.date, day.dateNum, day.lessons);
    }
    
    private void setLessons(List<Lesson> lessons){
        this.lessons = new ArrayList<>();
        for (Lesson l : lessons) this.lessons.add(l);
    }
    
    public List<Lesson> getLessons(){
        List<Lesson> copy = new ArrayList<>();
        for (Lesson l : this.lessons) copy.add(l);
        return copy;
    }
    
    @Override
    public String toString(){
        String res = "<<< " + date + " " + dateNum + " >>>\n\n";
        for (Lesson l : lessons) res += l + "\n";
        return res;
    }
    
    @Override
    public BusyDay clone(){
        return new BusyDay(this);
    }
}
