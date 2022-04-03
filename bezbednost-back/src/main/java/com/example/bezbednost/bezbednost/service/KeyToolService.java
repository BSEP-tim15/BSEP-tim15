package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IKeyToolService;
import org.springframework.stereotype.Service;

@Service
public class KeyToolService implements IKeyToolService {

    public void executeCommand(String command) {
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
