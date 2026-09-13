package com.illiasapa.url_shortener.Service;

import org.springframework.stereotype.Service;

@Service
public class Base62Service {

    public String encode(long number){
        if (number == 0){
            return "a";
        }

        StringBuilder sb = new StringBuilder();
        String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        while(number > 0){
            sb.append(chars.charAt((int) (number % 62)));
            number = number / 62;
        }

        return sb.reverse().toString();
    }

}
