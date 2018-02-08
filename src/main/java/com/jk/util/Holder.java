package com.jk.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class Holder {
    public static String daysTransaction = "10";
    public static String database = "";

    public static String appLocation = "C:/Users/" + System.getProperty("user.name") + "/AppData/Local/DebtShredder";
    public static String databaseName = "debtshredder.db";
    //public static String database = appLocation + "/" + databaseName;
    public static String propertyFile = appLocation + "/" + "debtShredder.props";

    public static Double salary = 1.00;
    public static Double escrow = 1.00;

    public static String getProperty(String input){
        try{
            //String configPath = new java.io.File(".").getCanonicalPath();
            BufferedReader br = new BufferedReader(new FileReader(propertyFile));

            String line = br.readLine();
            while(line != null){
                if(line.contains(input)){
                    return line.split("=")[1];
                }
                line = br.readLine();
            }
            br.close();
        }catch(Exception e){ e.printStackTrace(); }
        return "";
    }

    public static void saveProperty(String key, String value){
        try{
            Path configPath = Paths.get(propertyFile);
            List<String> fileContent = new ArrayList<>(Files.readAllLines(configPath, StandardCharsets.UTF_8));
            boolean exists = false;
            for(int i = 0; i < fileContent.size(); i++){
                if(fileContent.get(i).contains(key)){
                    fileContent.set(i, key + "=" + value);
                    exists = true;
                    break;
                }
            }

            Files.write(configPath, fileContent, StandardCharsets.UTF_8);

            if(!exists){
                Files.write(configPath, (key + "=" + value).getBytes(), StandardOpenOption.APPEND);
            }
        }catch(Exception e){ e.printStackTrace(); }
    }
}
