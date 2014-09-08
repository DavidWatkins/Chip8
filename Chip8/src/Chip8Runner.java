
/**
 * Main runner method for the Chip8 emulator
 * Initializes Chip8 and Window and then begins emulation
 * User must choose new file to load from options in menu
 * 
 * @author David Watkins, Nadine Chang, Dhruv Purushottam
 * @UNI: djw2146, nc2539, dp2631
 */
public class Chip8Runner {
    public static void main(String[] args) throws InterruptedException{
        CPU cpu = new Chip8();
        cpu.initState();
        Window w = new Window(cpu);
        
        while(true){
            cpu.emulateCycle();
            w.display();
            //Thread.sleep((long) 0.1);
        }
    }
}
