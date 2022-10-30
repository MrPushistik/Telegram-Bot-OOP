package Schedule;

public class Lesson implements Cloneable {
    final String time;
    final String room;
    final String subj;
    final String type;

    public Lesson(String time, String room, String subj, String type) {
        this.time = time;
        this.room = room;
        this.subj = subj;
        this.type = type;
    }
    
    public Lesson(Lesson l){
        this.time = l.time;
        this.room = l.room;
        this.subj = l.subj;
        this.type = l.type;
    }
    
    @Override 
    public String toString(){

        if (type.contains("лекц"))
            return "[" + time + " " + room + " " + subj + " " + type + "]";
        else if (type.contains("прак")) 
            return "[" + time + " " + room + " " + subj + " " + type + "]";
        else 
            return "[" + time + " " + room + " " + subj + " " + type + "]";
    }
}
