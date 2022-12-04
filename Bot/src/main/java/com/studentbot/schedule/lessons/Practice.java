package com.studentbot.schedule.lessons;

public class Practice extends Lesson{
    
    public Practice(String time, String room, String subj) {
        super(time, room, subj);
    }
    
    @Override
    public String toString() {
        String res = super.toString();
        return  res.substring(0, res.length()) + " (Практика)]";
    }  
}
