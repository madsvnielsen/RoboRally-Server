package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

/**
 * Conveyor field
 *<p>
 * An special type of movement field which moves the player in conveyors direction on interaction.
 * Moves one or two spaces depending on the color
 *
 */

public class Conveyor extends MovementField {

    public Paint getCOLOR() {
        return COLOR;
    }



    private final Color COLOR;


    @Expose
    public final boolean isDouble;

    public Conveyor(Color COLOR, Heading heading) {
        super(heading);
        this.COLOR = COLOR;
        this.isDouble = COLOR.equals(Color.BLUE);

    }


}
