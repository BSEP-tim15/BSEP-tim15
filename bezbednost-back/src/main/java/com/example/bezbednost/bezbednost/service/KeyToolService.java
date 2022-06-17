package com.example.bezbednost.bezbednost.service;

import com.example.bezbednost.bezbednost.iservice.IKeyToolService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class KeyToolService implements IKeyToolService {
    private final Logger logger = LoggerFactory.getLogger("logerror");

    public void executeCommand(String command) {
        try {
            printCommand(command);
        }
        catch (Exception e) {
            logger.error("location=KeyToolService timestamp=" + LocalDateTime.now() + " status=failure message=" + e.getMessage());
        }
    }

    private static void printCommand(String command) {
        System.out.println(command);
    }

}
