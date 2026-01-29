package com.technokratos.agona.handler;

import org.springframework.stereotype.Component;

@Component
public class StartHandler  {

    public String startCommandReceived(String name) {
        return  "Здарова бродяга, " + name +
                ". Как жизнь? Как двигаешься? Чем дышишь?";
    }

}
