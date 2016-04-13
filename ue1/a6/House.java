import lejos.nxt.Motor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.nxt.Sound;

public class House {
    public static void main(String args[]) {
        DifferentialPilot pilot = new DifferentialPilot(DifferentialPilot.WHEEL_SIZE_NXT1, 11.3, Motor.C, Motor.A);
        pilot.setTravelSpeed(25);
        pilot.setRotateSpeed(90);
        pilot.travel(50);                   // Move forward
        pilot.rotate(-135);                 // Turn to diagonal
        pilot.travel(50 * Math.sqrt(2));    // Drive diagonal
        pilot.rotate(135);                  // Turn to side
        pilot.travel(50);                   // Drive side
        pilot.rotate(45);                   // Turn to roof
        pilot.travel(50 / Math.sqrt(2));    // Drive roof
        pilot.rotate(90);                   // Turn at roof
        pilot.travel(50 / Math.sqrt(2));    // Drive roof
        pilot.rotate(135);                  // Turn to side
        pilot.travel(50);                   // Drive side
        pilot.rotate(-135);                 // Turn to diagonal
        pilot.travel(50 * Math.sqrt(2));    // Drive diagonal
        pilot.rotate(135);                  // Turn to side
        pilot.travel(50);                   // Drive side
        pilot.rotate(180);                  // Turn around
        pilot.travel(50);                   // Drive side
        pilot.rotate(-90);                  // Turn to start
        Sound.beep();                       // Beep
    }
}
