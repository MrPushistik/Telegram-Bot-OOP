package Schedule.Lessons;

public class Exam extends Lesson{
    
    public Exam(String time, String room, String subj) {
        super(time, room, subj);
    }
    
     @Override
    public String toString() {
        return "[" + super.toString() + " (Экзамен)]";
    }
}
