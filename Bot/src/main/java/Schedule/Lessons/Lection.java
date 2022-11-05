package Schedule.Lessons;

public class Lection extends Lesson{
    
    public Lection(String time, String room, String subj){
        super(time, room, subj);
    }

    @Override
    public String toString() {
        return "[" + super.toString() + " (Лекция)]";
    }
}
