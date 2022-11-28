package com.studentbot.chat;

import java.util.ArrayList;
import java.util.List;

public class StringHolder {
    
    private List<String> lst = new ArrayList<>();
    
    {
        lst.add(" умоляет о пощаде");
        lst.add(" когда-нибудь принесет шоколадку");
    }

    public String getText() {
        int indx = (int) (Math.random()*lst.size());
        return lst.get(indx);
    }
    
}
