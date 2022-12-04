package com.studentbot.schedule.lessons;

public class Exam extends Lesson{
    
    public Exam(String time, String room, String subj) {
        super(time, room, subj);
    }
    
    @Override
    public String toString() {
        String res = super.toString();
        return  res.substring(0, res.length()) + " (Экзамен)]";
    }
}
