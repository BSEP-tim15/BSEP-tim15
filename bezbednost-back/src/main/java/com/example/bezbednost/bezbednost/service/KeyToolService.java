package com.example.bezbednost.bezbednost.service;

public class KeyToolService {

    public static void executeCommand(String command) {
        try {
            printCommand(command);
            sun.security.tools.keytool.Main.main(parse(command));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String[] parse(String command) {
        return command.trim().split("\\s+");
    }

    private static void printCommand(String command) {
        System.out.println(command);
    }

}
