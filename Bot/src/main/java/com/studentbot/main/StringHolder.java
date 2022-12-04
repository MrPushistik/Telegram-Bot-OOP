/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.studentbot.main;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MrPushistik
 */
public class StringHolder {
    
    private static StringHolder main = new StringHolder();
    private List<String> msgs = new ArrayList<>();
    
    {
        msgs.add(" просит не отмечать");
        
        File f = new File("..\\..\\Data\\messages.txt");
        try(Scanner sc = new Scanner(f)){
            while(sc.hasNextLine()) msgs.add(" " + sc.nextLine());
        }catch (IOException ex) {
            MyLogger.logger(ex, "Не удалось считать данные из ..\\Data\\messages.txt");
        }
    }
    
    public static StringHolder getStringHolder(){
        return main;
    }
    
    public String getString(){
        return msgs.get((int) (Math.random()*msgs.size()));
    } 
}
