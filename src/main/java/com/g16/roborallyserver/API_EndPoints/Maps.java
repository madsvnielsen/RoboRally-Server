package com.g16.roborallyserver.API_EndPoints;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**Maps
 * A controller that manages all endpoints related to getting information about maps
 */

@RequestMapping(value = "/map", produces="application/json")
@RestController
public class Maps {



    private final String mapLocation ="src/main/java/dk/dtu/compute/se/pisd/roborally/Maps";

    /** Get map list
     * @return list of maps represtened as strings.
     * The maps are listed from the local directory pointed to by the mapLocation variable.
     * A map is represented by its file name.
     */
    @GetMapping(value = "/")
    public ResponseEntity<?> getMapList() {
        File mapDirectory = new File(mapLocation);
        File[] maps =  mapDirectory.listFiles();
        List<String> mapNames = new ArrayList<>();
        if(maps ==  null){
            return new ResponseEntity<>("No maps", HttpStatus.OK);
        }
        for(File f : maps){
            mapNames.add(f.getName());
        }
        return new ResponseEntity<>(mapNames, HttpStatus.OK);

    }






}
