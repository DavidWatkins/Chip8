import java.io.Serializable;


/**
 * A State object holding the variables of the CPU
 * Implements seriablizable so that it can written to a file
 * Can only be used by a Chip8 object
 * 
 * @author David Watkins
 * @UNI: djw2146
 */
public class State implements Serializable{
    private static final long serialVersionUID = 1L;
    private boolean[][] window;
    private boolean drawFlag;//Whether or not to redraw drawFlag
    
    //Memory control variables
    private int[] memory;
    private int indexReg;//Has values 0x000 to 0xFFF
    private int programCounter;//Has values 0x000 to 0xFFF
    
    //0x000 to 0x1FF - Chip 8 Interpreter (Contains font set)
    //0x050 to 0x0A0 - Used for the build in 4x5 pixel font set (0-F)
    //0x200 to 0xFFF - Program ROM and work RAM
    
    private int[] stack;//Used to remember the current location before a jump is performed
    private int stackPointer = 0;//Points to a location in the stack
    private int[] key;//Stores the HEX based keypad, current state of key
    private int[] V;//Stores variables to be used by the processor
    
    private int delayTimer, soundTimer;
    
    /**
     * Creates a new State object
     * Sets all values to 0
     */
    public State(){
        stack = new int[16];
        key = new int[16];
        V = new int[16];
        
        window = new boolean[CPU.X_DIM][CPU.Y_DIM];
        for(int x = 0; x < CPU.X_DIM; x++)
            for(int y = 0; y < CPU.Y_DIM; y++)
                window[x][y] = false;
        memory = new int[CPU.MEMORY_SIZE];
    }

    /**
     * Adds i to programCounter
     * @param i Increment value
     */
    public void incProgramCounter(int i) {
        programCounter += i;
    }
    
    /**
     * Sets the initial state of the processor and memory
     * Various values of the processor are defined in the documentation
     */
    public void initialState(){
        programCounter = 0x200;// Program counter starts at 0x200 (Start address program)
        indexReg = 0;// Reset index register
        stackPointer = 0;// Reset stack pointer

        // Clear display
        clearScreen();

        // Clear stack
        for(int i = 0; i < stack.length; i++){
            stack[i] = 0;
            key[i] = 0;
            V[i] = 0;
        }

        // Clear memory
        for(int i = 0; i < 4096; i++)
            memory[i] = 0;
                        
        // Load fontset
        for(int i = 0; i < CPU.CHIP8_FONTSET.length; i++)
            memory[i] = CPU.CHIP8_FONTSET[i];       

        // Reset timers
        delayTimer = 0;
        soundTimer = 0;

        // Clear screen once
        drawFlag = true;
    }
    
    /**
     * Clears the screen by setting all to false
     */
    public void clearScreen(){
        for(int i = 0; i < CPU.X_DIM; ++i)
            for(int j = 0; j < CPU.Y_DIM; j++)
                window[i][j] = false;
    }

    /**
     * @return Drawflag
     */
    public boolean getDrawFlag() {
        return drawFlag;
    }

    /**
     * Returns a formatted 4 byte opcode to be used by the processor
     * @return The opcode
     */
    public int getOpcode(){
        //Takes the first program from memory, shifts it 8 bits, and then appends the next program
        return memory[programCounter] << 8 | memory[programCounter + 1];
    }

    /**
     * @param programCounter New programCounter value
     */
    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }
    
    /**
     * Adds a value to the stack and increments the stack pointer
     * @param code Value to be added to stack
     */
    public void addToStack(int code){
        stack[stackPointer++] = code;
    }
    
    /**
     * Pops a values from the stack and decrements the pointer
     * @return
     */
    public int popFromStack(){
        return stack[--stackPointer];
    }

    /**
     * @return Program Counter
     */
    public int getProgramCounter() {
        return programCounter;
    }

    /**
     * Returns a values of V from pos
     * @param pos Position in V
     * @return Value of V at pos
     */
    public int getV(int pos) {
        return V[pos];
    }

    /**
     * Sets the value of V at pos to val
     * @param pos Position in V
     * @param val New value
     */
    public void setV(int pos, int val) {
        V[pos] = val;
    }

    /**
     * @param indexReg new Index Reg
     */
    public void setIndexReg(int indexReg) {
        this.indexReg = indexReg;
    }

    /**
     * @param drawFlag New draw Flag
     */
    public void setDrawFlag(boolean drawFlag) {
        this.drawFlag = drawFlag;
    }

    /**
     * @return Index Register
     */
    public int getIndexReg() {
        return indexReg;
    }

    /**
     * @param address Address to be read from memory    
     * @return Value of memory at that address
     */
    public int getMemAddr(int address) {
        return memory[address];
    }

    /**
     * @param x X position in window
     * @param y Y position in window
     * @return The value of window at x,y
     */
    public boolean getWindowPos(int x, int y) {
        return window[x][y];
    }

    /**
     * @param x X position in window
     * @param y Y position in window
     * @param val New value of window at x,y
     */
    public void setWindowPos(int x, int y, boolean val) {
        window[x][y] = val;
    }

    /**
     * @param index Index of key value
     * @return New key value
     */
    public int getKey(int index) {
        return key[index];
    }

    /**
     * @return Delay timer
     */
    public int getDelayTimer() {
        return delayTimer;
    }

    /**
     * @param delayTimer New delay Timer
     */
    public void setDelayTimer(int delayTimer) {
        this.delayTimer = delayTimer;
    }

    /**
     * @param sountTimer new sound timer
     */
    public void setSoundTimer(int sountTimer) {
        this.soundTimer = sountTimer;
    }

    /**
     * @param address Memory address to be overwritten
     * @param val New value of memory address
     */
    public void setMemAddr(int address, int val) {
        memory[address] = val;
    }

    /**
     * @return sound timer
     */
    public int getSoundTimer() {
        return soundTimer;
    }

    /**
     * @param loc Location in key array
     * @param state new value of key
     */
    public void setKey(int loc, int state) {
        key[loc] = state;
    }
    
    /** 
     * Returns a string representation of the State object
     * Necessary for debugging purposes
     * @see java.lang.Object#toString()
     */
    public String toString(){
        String output = "";
        output+="window:\n";
        for(int x = 0; x < CPU.X_DIM; x++){
            for(int y = 0; y < CPU.Y_DIM; y++)
                output+="" + window[x][y];
            output+="\n";
        }
        
        output+="drawflag:" + drawFlag + "\n";
        
        output+="Memory:\n";
        for(int i = 0; i < CPU.MEMORY_SIZE; i++)
            output+=" " + memory[i] + " ";
        output+="\n";
        
        output+="indexReg: " + indexReg + "\n";
        output+="programCounter: " + programCounter + "\n";
        
        output+="Stack: \n";
        for(int i = 0; i<16; i++)
            output+= stack[i] + " ";
        output+= "\n";
        
        output+="key: \n";
        for(int i = 0; i<16; i++)
            output+= key[i] + " ";
        output+= "\n";
        
        output+="V: \n";
        for(int i = 0; i<16; i++)
            output+= V[i] + " ";
        output+= "\n";
        
        output+="delayTimer " + delayTimer + "\n";
        output+="soundTimer " + soundTimer + "\n";
        output+="Opcode: " + Integer.toHexString(getOpcode()) + "\n";
        
        return output;
    }
}
