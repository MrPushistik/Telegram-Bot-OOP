package BotsPack;

import java.util.ArrayList;
import java.util.List;

public class ArrayDay {
    
    final int currDay;
    private List<Day> days;

    public ArrayDay(){
        this(0);
        setDays(new ArrayList<>());
    }
    
    public ArrayDay(List<Day> days, int currDay){
        this(currDay);
        setDays(days);
    }
    
    private ArrayDay(int currDay){
        this.currDay = currDay;
    }
        
    public final void setDays(List<Day> days){
        this.days = new ArrayList<>();
        for (Day d : days) this.days.add(new Day(d));
    }
    
    public List<Day> getDays(){
        List<Day> copy = new ArrayList<>();
        for (Day d : this.days) copy.add(new Day(d));
        return copy;
    }
    
    @Override
    public String toString(){
        String res = "";
        for (Day d : days) res += d + "\n";
        return res;
    }
}
