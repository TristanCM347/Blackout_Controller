package blackout.entity.satellite;

import blackout.entity.Entity;
import blackout.functionality.Movement;
import utils.Angle;


public abstract class Satellite extends Entity implements Movement {
    private int direction;

    public Satellite(String id, double height, Angle position, int direction, int range) {
        super(id, height, position, range);
        this.direction = direction;
    }

    public int getDirection() {
        return this.direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
