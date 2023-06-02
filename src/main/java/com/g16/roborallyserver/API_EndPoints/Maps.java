package com.g16.roborallyserver.API_EndPoints;

import com.g16.roborallyserver.sessionUtils.Connection;
import com.g16.roborallyserver.sessionUtils.ConnectionManager;
import com.g16.roborallyserver.sessionUtils.GameSession;
import com.g16.roborallyserver.sessionUtils.GameSessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RequestMapping(value = "/map", produces="application/json")
@RestController
public class Maps {

    @Autowired
    private HttpServletRequest request;

    @GetMapping(value = "/")
    public ResponseEntity<?> getMapList() {
        File mapDirectory = new File("src/main/java/dk/dtu/compute/se/pisd/roborally/Maps");
        File[] maps =  mapDirectory.listFiles();
        List<String> mapNames = new ArrayList<String>();
        if(maps ==  null){
            return new ResponseEntity<>("No maps", HttpStatus.OK);
        }
        for(File f : maps){
            mapNames.add(f.getName());
        }
        return new ResponseEntity<>(mapNames, HttpStatus.OK);

    }






}
