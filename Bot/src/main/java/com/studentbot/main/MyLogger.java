package com.studentbot.main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

public class MyLogger {
    
    private MyLogger(){}
    
    public static void logger(Exception ex, String reason){
        try(FileWriter f = new FileWriter("..\\..\\Data\\log.txt", true)){ 
           f.write(reason + ":\n" + ex.toString() + "\n");
        } catch (IOException ex1) {
            java.util.logging.Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex1);
        } 
    }
}
