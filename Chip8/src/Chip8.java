import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * A Chip8 processor emulation that emulates graphics, CPU, and memory
 * Utilizes a State object to maintain the variables of the given processor
 * Follows the Chip8 processor instructions at http://en.wikipedia.org/wiki/CHIP-8
 * Also plays a sound when SoundTimer > 0, "button-10.wav" must be in the project directory
 * Also utilizes the ApplicationLoader class to load and save states/roms
 * 
 * @author David Watkins
 * @UNI: djw2146
 */
public class Chip8 implements CPU{
    //State variable of Chip8
    private State currentState;
    //List of opcodes used, for debugging purposes
    private ArrayList<String> opcodes;
    
    /**
     * Creates a new State object and initializes opcodes
     */
    public Chip8(){
        currentState = new State();
        opcodes = new ArrayList<String>();
    }
    
    /** 
     * Initializes the State variable
     * @see CPU#initState()
     */
    public void initState(){
        currentState.initialState();
    }
    
    /**
     * Loads a rom from file using ApplicationLoader
     * The String passed must be the location of the Rom file
     * Will return an print error if file is not formatted properly
     * @see CPU#loadROM(java.lang.String)
     */
    public void loadROM(String fileName){
        currentState.initialState();
        int[] rom = ApplicationLoader.getROMFromFile(fileName);
        if(rom.length <= ROM_SIZE)//If valid, read rom into memory
            for(int i = 0; i < rom.length; i++){
                //System.out.println(Integer.toHexString(rom[i]));
                currentState.setMemAddr(i + ROM_OFFSET, rom[i]);
                //System.out.println(Integer.toHexString(currentState.getMemAddr(i+ROM_OFFSET)));
            }
        else
            System.out.println("error");
    }
    
    /** 
     * Loads a state from a file
     * Uses ApplicationLoader to load the file
     * @see CPU#loadState(java.lang.String)
     */
    public void loadState(String fileName){
        this.currentState = ApplicationLoader.readState(fileName);
        if(currentState == null)
            System.out.println("File not read");
    }
    
    /* (non-Javadoc)
     * @see CPU#getDrawFlag()
     */
    public boolean getDrawFlag(){
        return currentState.getDrawFlag();
    }
    
    /**
     * Emulates one cycle of the Chip8 processor
     * Follows opcode formatting using bitwise operations
     * First fetches the opcode, then processes it, then updates the timers
     * @see CPU#emulateCycle()
     */
    public void emulateCycle()
    {
        // Fetch opcode
        int opcode = currentState.getOpcode();
        //ApplicationLoader.printStatus(currentState);
        opcodes.add(Integer.toHexString(opcode));
        
        // Process opcode
        switch(opcode & 0xF000)
        {       
            case 0x0000:
                switch(opcode & 0x000F)
                {
                    case 0x0000: // 0x00E0: Clears the screen
                        currentState.clearScreen();
                        currentState.setDrawFlag(true);
                        currentState.incProgramCounter(2);
                    break;

                    case 0x000E: // 0x00EE: Returns from subroutine
                        // Put the stored return address from the stack back into the program counter 
                        currentState.setProgramCounter(currentState.popFromStack());           
                        currentState.incProgramCounter(2);
                    break;

                    default:
                        printError(opcode);          
                }
            break;

            case 0x1000: // 0x1NNN: Jumps to address NNN
                currentState.setProgramCounter(opcode & 0x0FFF);
            break;

            case 0x2000: // 0x2NNN: Calls subroutine at NNN.
                currentState.addToStack(currentState.getProgramCounter());// Store current address in stack
                currentState.setProgramCounter(opcode & 0x0FFF);// Set the program counter to the address at NNN
            break;
            
            case 0x3000: // 0x3XNN: Skips the next instruction if VX equals NN
                if(currentState.getV((opcode & 0x0F00) >> 8) == (opcode & 0x00FF))
                    currentState.incProgramCounter(4);
                else
                    currentState.incProgramCounter(2);
            break;
            
            case 0x4000: // 0x4XNN: Skips the next instruction if VX doesn't equal NN
                if(currentState.getV((opcode & 0x0F00) >> 8) != (opcode & 0x00FF))
                    currentState.incProgramCounter(4);
                else
                    currentState.incProgramCounter(2);
            break;
            
            case 0x5000: // 0x5XY0: Skips the next instruction if VX equals VY.
                if(currentState.getV((opcode & 0x0F00) >> 8) == currentState.getV((opcode & 0x00F0) >> 4))
                    currentState.incProgramCounter(4);
                else
                    currentState.incProgramCounter(2);
            break;
            
            case 0x6000: // 0x6XNN: Sets VX to NN.
                currentState.setV((opcode & 0x0F00) >> 8, (opcode & 0x00FF));
                currentState.incProgramCounter(2);
            break;
            
            case 0x7000: // 0x7XNN: Adds NN to VX.
                int case7 = currentState.getV((opcode & 0x0F00) >> 8) + opcode & 0x00FF;
                currentState.setV((opcode & 0x0F00) >> 8, case7);
                currentState.incProgramCounter(2);
            break;
            
            case 0x8000:
                switch(opcode & 0x000F)
                {
                    case 0x0000: // 0x8XY0: Sets VX to the value of VY
                        int case0 = currentState.getV((opcode & 0x00F0) >> 4);
                        currentState.setV((opcode & 0x0F00) >> 8, case0);
                        currentState.incProgramCounter(2);
                    break;

                    case 0x0001: // 0x8XY1: Sets VX to "VX OR VY"
                        int case1 = currentState.getV((opcode & 0x0F00) >> 8) | currentState.getV((opcode & 0x00F0) >> 4);
                        currentState.setV((opcode & 0x0F00) >> 8, case1);
                        currentState.incProgramCounter(2);
                    break;

                    case 0x0002: // 0x8XY2: Sets VX to "VX AND VY"
                        int case2 = currentState.getV((opcode & 0x0F00) >> 8) & currentState.getV((opcode & 0x00F0) >> 4);
                        currentState.setV((opcode & 0x0F00) >> 8, case2);
                        currentState.incProgramCounter(2);
                    break;

                    case 0x0003: // 0x8XY3: Sets VX to "VX XOR VY"
                        int case3 = currentState.getV((opcode & 0x0F00) >> 8) ^ currentState.getV((opcode & 0x00F0) >> 4);
                        currentState.setV((opcode & 0x0F00) >> 8, case3);
                        currentState.incProgramCounter(2);
                    break;

                    case 0x0004: // 0x8XY4: Adds VY to VX. VF is set to 1 when there's a carry, and to 0 when there isn't                   
                        if(currentState.getV((opcode & 0x00F0) >> 4) > (0xFF - currentState.getV((opcode & 0x0F00) >> 8))) 
                            currentState.setV(0xF, 1); //carry
                        else 
                            currentState.setV(0xF, 0);                 
                        currentState.setV((opcode & 0x0F00) >> 8, currentState.getV((opcode & 0x0F00) >> 8) + currentState.getV((opcode & 0x00F0) >> 4));
                        currentState.incProgramCounter(2);                   
                    break;

                    case 0x0005: // 0x8XY5: VY is subtracted from VX. VF is set to 0 when there's a borrow, and 1 when there isn't
                        if(currentState.getV((opcode & 0x00F0) >> 4) > currentState.getV((opcode & 0x0F00) >> 8)) 
                            currentState.setV(0xF, 0); // there is a borrow
                        else 
                            currentState.setV(0xF, 1);                 
                        currentState.setV((opcode & 0x0F00) >> 8, currentState.getV((opcode & 0x0F00) >> 8) - currentState.getV((opcode & 0x00F0) >> 4));
                        currentState.incProgramCounter(2);  
                    break;

                    case 0x0006: // 0x8XY6: Shifts VX right by one. VF is set to the value of the least significant bit of VX before the shift
                        currentState.setV(0xF, (currentState.getV((opcode & 0x0F00) >> 8) & 0x1));
                        currentState.setV((opcode & 0x0F00) >> 8, currentState.getV((opcode & 0x0F00) >> 8) >> 1);
                        currentState.incProgramCounter(2);  
                    break;

                    case 0x0007: // 0x8XY7: Sets VX to VY minus VX. VF is set to 0 when there's a borrow, and 1 when there isn't
                        if(currentState.getV((opcode & 0x0F00) >> 8) > currentState.getV((opcode & 0x00F0) >> 4))   // VY-VX
                            currentState.setV(0xF, 0); // there is a borrow
                        else
                            currentState.setV(0xF, 1);
                        currentState.setV((opcode & 0x0F00) >> 8, currentState.getV((opcode & 0x00F0) >> 4) - currentState.getV((opcode & 0x0F00) >> 8));              
                        currentState.incProgramCounter(2);  
                    break;

                    case 0x000E: // 0x8XYE: Shifts VX left by one. VF is set to the value of the most significant bit of VX before the shift
                        currentState.setV(0xF, currentState.getV((opcode & 0x0F00) >> 8) >> 7);
                        currentState.setV((opcode & 0x0F00) >> 8, currentState.getV((opcode & 0x0F00) >> 8) << 1);
                        currentState.incProgramCounter(2);  
                    break;

                    default:
                        printError(opcode);
                }
            break;
            
            case 0x9000: // 0x9XY0: Skips the next instruction if VX doesn't equal VY
                if(currentState.getV((opcode & 0x0F00) >> 8) != currentState.getV((opcode & 0x00F0) >> 4))
                    currentState.incProgramCounter(4);
                else
                    currentState.incProgramCounter(2);
            break;

            case 0xA000: // ANNN: Sets I to the address NNN
                currentState.setIndexReg(opcode & 0x0FFF);
                currentState.incProgramCounter(2);
            break;
            
            case 0xB000: // BNNN: Jumps to the address NNN plus V0
                currentState.setProgramCounter((opcode & 0x0FFF) + currentState.getV(0));
            break;
            
            case 0xC000: // CXNN: Sets VX to a random number and NN
                int casec = (((new Random()).nextInt() % 0xFF) & (opcode & 0x00FF));
                currentState.setV((opcode & 0x0F00) >> 8, casec);
                currentState.incProgramCounter(2);
            break;
        
            case 0xD000: 
             // DXYN: Draws a sprite at coordinate (VX, VY) that has a width of 8 pixels and a height of N pixels. 
                // Each row of 8 pixels is read as bit-coded starting from memory location I; 
                // I value doesn't change after the execution of this instruction. 
                // VF is set to 1 if any screen pixels are flipped from set to unset when the sprite is drawn, 
                // and to 0 if that doesn't happen
                int x = currentState.getV((opcode & 0x0F00) >> 8);
                int y = currentState.getV((opcode & 0x00F0) >> 4);
                int height = (opcode & 0x000F);
                int pixel;

                currentState.setV(0xF, 0);
                for (int yline = 0; yline < height; yline++)
                {
                    pixel = currentState.getMemAddr(currentState.getIndexReg() + yline);
                    for(int xline = 0; xline < 8; xline++)
                    {
                        if((pixel & (0x80 >> xline)) != 0 && (x+xline) < X_DIM && (y+yline) < Y_DIM)
                        {
                            if(currentState.getWindowPos(x + xline, y + yline) == true)
                            {
                                currentState.setV(0xF, 1);                                    
                            }
                            currentState.setWindowPos(x + xline, y + yline, currentState.getWindowPos(x + xline, y + yline) ^ true);
                        }
                    }
                }
                            
                currentState.setDrawFlag(true);            
                currentState.incProgramCounter(2);
            break;
                
            case 0xE000:
                switch(opcode & 0x00FF)
                {
                    case 0x009E: // EX9E: Skips the next instruction if the key stored in VX is pressed
                        if(currentState.getKey(currentState.getV((opcode & 0x0F00) >> 8)) != 0)
                            currentState.incProgramCounter(4);
                        else
                            currentState.incProgramCounter(2);
                    break;
                    
                    case 0x00A1: // EXA1: Skips the next instruction if the key stored in VX isn't pressed
                        if(currentState.getKey(currentState.getV((opcode & 0x0F00) >> 8)) == 0)
                            currentState.incProgramCounter(4);
                        else
                            currentState.incProgramCounter(2);
                    break;

                    default:
                        printError(opcode);
                }
            break;
            
            case 0xF000:
                switch(opcode & 0x00FF)
                {
                    case 0x0007: // FX07: Sets VX to the value of the delay timer
                        currentState.setV((opcode & 0x0F00) >> 8, currentState.getDelayTimer());
                        currentState.incProgramCounter(2);
                    break;
                                    
                    case 0x000A: // FX0A: A key press is awaited, and then stored in VX     
                    {
                        boolean keyPress = false;

                        for(int i = 0; i < 16; ++i)
                        {
                            if(currentState.getKey(i) != 0)
                            {
                                currentState.setV((opcode & 0x0F00) >> 8, i);
                                keyPress = true;
                            }
                        }

                        // If we didn't received a keypress, skip this cycle and try again.
                        if(!keyPress)                       
                            return;

                        currentState.incProgramCounter(2);                    
                    }
                    break;
                    
                    case 0x0015: // FX15: Sets the delay timer to VX
                        currentState.setDelayTimer(currentState.getV((opcode & 0x0F00) >> 8));
                        currentState.incProgramCounter(2); 
                    break;

                    case 0x0018: // FX18: Sets the sound timer to VX
                        currentState.setSoundTimer(currentState.getV((opcode & 0x0F00) >> 8));
                        currentState.incProgramCounter(2); 
                    break;

                    case 0x001E: // FX1E: Adds VX to I
                        if(currentState.getIndexReg() + currentState.getV((opcode & 0x0F00) >> 8) > 0xFFF)// VF is set to 1 when range overflow (I+VX>0xFFF), and 0 when there isn't.
                            currentState.setV(0xF, 1);
                        else
                            currentState.setV(0xF, 0);
                        currentState.setIndexReg(currentState.getIndexReg() + currentState.getV(opcode & 0x0F00 >> 8));
                        currentState.incProgramCounter(2);
                    break;

                    case 0x0029: // FX29: Sets I to the location of the sprite for the character in VX. Characters 0-F (in hexadecimal) are represented by a 4x5 font
                        currentState.setIndexReg(currentState.getV((opcode & 0x0F00) >> 8) * 0x5);
                        currentState.incProgramCounter(2);
                    break;

                    case 0x0033: // FX33: Stores the Binary-coded decimal representation of VX at the addresses I, I plus 1, and I plus 2
                        currentState.setMemAddr(currentState.getIndexReg(), currentState.getV((opcode & 0x0F00) >> 8) / 100);
                        currentState.setMemAddr(currentState.getIndexReg() + 1, ((currentState.getV((opcode & 0x0F00) >> 8) / 10) % 10));
                        currentState.setMemAddr(currentState.getIndexReg() + 2, ((currentState.getV((opcode & 0x0F00) >> 8) % 100) % 10));                 
                        currentState.incProgramCounter(2);
                    break;

                    case 0x0055: // FX55: Stores V0 to VX in memory starting at address I                   
                        for (int i = 0; i <= ((opcode & 0x0F00) >> 8); ++i)
                            currentState.setMemAddr(currentState.getIndexReg() + 1, currentState.getV(i));  

                        // On the original interpreter, when the operation is done, I = I + X + 1.
                        currentState.setIndexReg(currentState.getIndexReg() + ((opcode & 0x0F00) >> 8) + 1);
                        currentState.incProgramCounter(2);
                    break;

                    case 0x0065: // FX65: Fills V0 to VX with values from memory starting at address I                  
                        for (int i = 0; i <= ((opcode & 0x0F00) >> 8); ++i)
                            currentState.setV(i, currentState.getMemAddr(currentState.getIndexReg() + i));         

                        // On the original interpreter, when the operation is done, I = I + X + 1.
                        currentState.setIndexReg(currentState.getIndexReg() + ((opcode & 0x0F00) >> 8) + 1);
                        currentState.incProgramCounter(2);
                    break;

                    default:
                        printError(opcode);
                }
            break;

            default:
                printError(opcode);
        }   

        updateTimers();
    }
   
    /**
     * Updates the DelayTimer and Sound Timer
     * Will also play a sound from beep() if soundtimer is greater than 0
     */
    private void updateTimers(){
        if(currentState.getDelayTimer() > 0)
            currentState.setDelayTimer(currentState.getDelayTimer() - 1);

        if(currentState.getSoundTimer() > 0)
        {
            if(currentState.getSoundTimer() == 1){//"Sound"{
                beep();
            }
            currentState.setSoundTimer(currentState.getSoundTimer() - 1);
        }
    }
    
    /**
     * Returns a new copy of window from State
     * @see CPU#getWindow()
     */
    public boolean[][] getWindow(){
        boolean[][] tempWindow = new boolean[X_DIM][Y_DIM];
        for(int x = 0; x < X_DIM; x++)
            for(int y = 0; y < Y_DIM; y++)
                tempWindow[x][y] = currentState.getWindowPos(x, y);
        return tempWindow;
    }
    
    /**
     * Sets the key value to state
     * @see CPU#setKey(int, int)
     */
    public void setKey(int loc, int state){
        currentState.setKey(loc, state);
    }
    
    /**
     * Saves state using ApplicationLoader with the given fileName
     * @see CPU#saveState(java.lang.String)
     */
    public void saveState(String fileName){
        ApplicationLoader.writeState(currentState, fileName);
    }

    /**
     * Sets the drawFlag in state
     * @see CPU#setDrawFlag(boolean)
     */
    @Override
    public void setDrawFlag(boolean drawFlag) {
        currentState.setDrawFlag(drawFlag);
    }
    
    /**
     * Prints an error to the console with the given opcodes and all opcodes leading up to it
     * For debugging purposes
     * 
     * @param opcode The opcode when the error was caused
     */
    private void printError(int opcode){
        //Print opcode and hex version
        System.out.println(currentState.toString() + "\nOpcode: " + Integer.toHexString(opcode));
        //Print all prior opcodes
        for(String i : opcodes)
            System.out.print(i + " ");
        System.out.println();
    }

    /**
     * Plays a beep sound from "button-10.wav"
     * Returns an error if there is a problem playing sound
     */
    private void beep(){
        try {
            //Load sound and play clip
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File("button-10.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        }
        
        catch(UnsupportedAudioFileException uae) {
            uae.printStackTrace();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
        catch(LineUnavailableException lua) {
            lua.printStackTrace();
        }
    }
}

