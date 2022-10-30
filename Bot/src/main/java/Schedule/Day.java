package Schedule;

import java.util.List;
import java.util.ArrayList;

public class Day{
    
    final String date;
    final String dateNum;
    private List<Lesson> lessons;
    
    public Day(String date, String dateNum){
        this.date = date;
        this.dateNum = dateNum;
    }
    
    public Day(List<Lesson> lessons, String date, String dateNum){
        this(date, dateNum);
        setLessons(lessons);
    }
    
    public Day(Day d){
        this(d.date, d.dateNum);
        setLessons(d.lessons);
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
        if(lessons.isEmpty()) res += "[В этот день нет пар]";
        for (Lesson l : lessons) res += l + "\n";
        return res;
    }
}
