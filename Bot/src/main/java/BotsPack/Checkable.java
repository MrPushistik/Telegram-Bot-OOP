/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package BotsPack;

/**
 *
 * @author sunfl
 */
public interface Checkable {
    
    static boolean check(String command, String bot){
        return command.equals("/schedule") || command.equals("/schedule" + bot);
    }
    
}
