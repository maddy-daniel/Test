package org.fdu;

import java.util.Scanner;

public class UI {

    public UI(){}

    public void writeString(String msg){
        System.out.println(msg);
    }

    public String getLine(){
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }
}
