package com.studentbot.schedule.lessons;

public abstract class Lesson {
    
    final String time;
    final String room;
    final String subj;

    public Lesson(String time, String room, String subj) {
        this.time = time;
        this.room = room;
        this.subj = subj;
    }
    
    @Override 
    public String toString(){
        return time + " " + room + " " + subj;
    }
}
