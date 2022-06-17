package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IKeyToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KeyToolService implements IKeyToolService {
    private final Logger LOGGER = LoggerFactory.getLogger("logerror");

    public void executeCommand(String command) {
        try {
            printCommand(command);
            //sun.security.tools.keytool.Main.main(parse(command));
        }
        catch (Exception e) {
            LOGGER.error("location=KeyToolService timestamp=" + LocalDateTime.now() + " status=failure message=" + e.getMessage());
        }
    }

    private static String[] parse(String command) {
        return command.trim().split("\\s+");
    }

    private static void printCommand(String command) {
        System.out.println(command);
    }

}
