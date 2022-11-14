package Schedule.Lessons;

public class Lab extends Lesson{
    
    public Lab(String time, String room, String subj){
        super(time, room, subj);
    }

    @Override
    public String toString() {
        return "[" + super.toString() + " (Лабораторная работа)]";
    }
}
