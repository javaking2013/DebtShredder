package com.jk.util;

public class ValidUtility {

    public static boolean isValidPrice(String input){
        if(input.isEmpty() || input.equals("")){ return false; }

        if(input.substring(input.length() - 3,input.length() - 2).contains(".")){
            if(!input.contains("$")){
                try{
                    Double balance = Double.parseDouble(input);
                    return true;
                } catch(Exception e){ return false; }
            }
        }
        return false;
    }

    public static boolean isValidDate(String input){
        if(input.isEmpty() || input.equals("")){ return false; }

        if(input.split("/")[0].length() == 1){ input = "0" + input; }

        if(input.substring(2,3).equals("/") && input.substring(5,6).equals("/")){
            String[] parts = input.split("/");
            try {
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                int year = Integer.parseInt(parts[2]);
                if( (month <= 12) && (day <= 31) && (parts[2].length() == 4) ){
                    return true;
                }
            }catch(Exception e){ return false; }
        }
        return false;
    }
}
