package com.g16.roborallyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import dk.dtu.compute.se.pisd.roborally.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@Controller
public class RoboRallyServerApplication {

    ExecutorService executor = Executors.newFixedThreadPool(5);

    @RequestMapping("/start")
    @ResponseBody
    String start(){

        executor.execute(() -> StartRoboRally.main(null));
        return "Game stated";
    }

    @RequestMapping("/players")
    @ResponseBody
    String players(){
        return "Players: 1, 2, 3";
    }

    @RequestMapping("/")
    @ResponseBody
    String helloWorld(){
        return "Sick <span style=\"color:red\">robo</span> server!";
    }
    public static void main(String[] args) {

        SpringApplication.run(RoboRallyServerApplication.class, args);
    }



}

