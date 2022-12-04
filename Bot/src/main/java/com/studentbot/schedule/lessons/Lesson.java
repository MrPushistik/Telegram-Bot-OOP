package com.studentbot.schedule.lessons;

public class Lesson {
    
    final String time;
    final String room;
    final String subj;

    public Lesson(String time, String room, String subj) {
        this.time = time;
        this.room = room;
        this.subj = subj;
    }
    
    public static Lesson getLesson(String type, String time, String room, String name){
        
        switch(type){
            case "(лекц)": return new Lection(time, room, name);
            case "(прак)": return new Practice(time, room, name); 
            case "(зач)": return new Test(time, room, name); 
            case "(экз)": return new Exam(time, room, name); 
            case "(лаб)": return new Lab(time, room, name); 
            default: return new Lesson(time, room, name);
        }
    }
    
    @Override 
    public String toString(){
        return "[" + time + " " + room + " " + subj + "]";
    }
}
