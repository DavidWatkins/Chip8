/**
 * Contains methods for the CPU of the emulator
 * Defines various constants to be used for other classes
 * Also reduces some of the clutter from the implementation
 * 
 * @author David Watkins
 * @UNI djw2146
 */
public interface CPU{
    //Define constants
    public final int X_DIM = 64;
    public final int Y_DIM = 32;
    public final int MEMORY_SIZE = 4096;
    public final int ROM_OFFSET = 512;
    public final int ROM_SIZE = MEMORY_SIZE - ROM_OFFSET;
    
    //Chip8 has a fontset that must be predefined for graphic output
    public final int[] CHIP8_FONTSET =
        { 
            0xF0, 0x90, 0x90, 0x90, 0xF0, //0
            0x20, 0x60, 0x20, 0x20, 0x70, //1
            0xF0, 0x10, 0xF0, 0x80, 0xF0, //2
            0xF0, 0x10, 0xF0, 0x10, 0xF0, //3
            0x90, 0x90, 0xF0, 0x10, 0x10, //4
            0xF0, 0x80, 0xF0, 0x10, 0xF0, //5
            0xF0, 0x80, 0xF0, 0x90, 0xF0, //6
            0xF0, 0x10, 0x20, 0x40, 0x40, //7
            0xF0, 0x90, 0xF0, 0x90, 0xF0, //8
            0xF0, 0x90, 0xF0, 0x10, 0xF0, //9
            0xF0, 0x90, 0xF0, 0x90, 0x90, //A
            0xE0, 0x90, 0xE0, 0x90, 0xE0, //B
            0xF0, 0x80, 0x80, 0x80, 0xF0, //C
            0xE0, 0x90, 0x90, 0x90, 0xE0, //D
            0xF0, 0x80, 0xF0, 0x80, 0xF0, //E
            0xF0, 0x80, 0xF0, 0x80, 0x80  //F
        };
    
    /**
     * Initialize the state of the CPU
     */
    public void initState();
    
    /**
     * Returns the draw flag
     * 
     * @return Draw Flag
     */
    public boolean getDrawFlag();
    
    /**
     * Runs an emulation cycle once
     */
    public void emulateCycle();
    
    /**
     * Returns a 2d array graphics window
     * 
     * @return 2d boolean array window
     */
    public boolean[][] getWindow();
    
    /**
     * Sets the key input value at index loc
     * 
     * @param loc The index in the key array
     * @param val 1 or 0 if the key is pressed
     */
    public void setKey(int loc, int val);
    
    
    /**
     * Saves the state of the cpu with the given fileName
     * 
     * @param fileName Name of the file to be written
     */
    public void saveState(String fileName);
    
    /**
     * Loads the state from a file
     * 
     * @param fileName Name of the file to be read
     */
    public void loadState(String fileName);
    
    /**
     * Sets the draw flag to true or false
     * 
     * @param drawFlag New drawflag
     */
    public void setDrawFlag(boolean drawFlag);
    
    /**
     * Loads ROM information from a file fileName
     * 
     * @param fileName The location of the ROM file
     */
    public void loadROM(String fileName);
}