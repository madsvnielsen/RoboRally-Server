package com.g16.roborallyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
@Controller
public class RoboRallyServerApplication {

    @RequestMapping("/")
    @ResponseBody
    String helloWorld(){
        return "Amazing <span style=\"color:red\">robo</span> server!";
    }
    public static void main(String[] args) {

        SpringApplication.run(RoboRallyServerApplication.class, args);
    }
}