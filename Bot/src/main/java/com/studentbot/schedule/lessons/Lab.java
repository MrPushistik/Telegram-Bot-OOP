package com.studentbot.schedule.lessons;

public class Lab extends Lesson{
    
    public Lab(String time, String room, String subj){
        super(time, room, subj);
    }


    @Override
    public String toString() {
        String res = super.toString();
        return  res.substring(0, res.length()) + " (Лабораторная работа)]";
    }
}
