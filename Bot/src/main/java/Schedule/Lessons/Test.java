package Schedule.Lessons;

public class Test extends Lesson{
    
    public Test(String time, String room, String subj) {
        super(time, room, subj);
    }
    
    @Override
    public String toString() {
        return "[" + super.toString() + " (Зачет)]";
    }
    
}
