import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * The way this works is that you send a file as a parameter to a
 * ApplicationLoader object, after which you can access the array of hex values
 * using the getMemory() method
 * 
 * @author dhruv purushottam
 * @UNI: dp2631
 */

public class ApplicationLoader{
    /**
	 * Checks to see if the file exists
	 * 
	 * @param rom The file to be checked
	 * @return True if the file exists, else false
	 */
	private static boolean openFile(File rom) {
		if (rom.equals(null)) {
			System.out.println("Invalid file. Choose another file");
			return false;
		} else
			return true;
	}

	/**
	 * Imports ROM data from a rom file
	 * If the file is not proper, will return nothing
	 * 
	 * @param fileName The string name of the file
	 * @return A formatted integer array of hexadecimal values
	 */
	public static int[] getROMFromFile(String fileName) {
	    //Initialize variables
	    File rom = new File(fileName);
	    long fileSize = rom.length();
	    int[] romInfo = new int[CPU.ROM_SIZE];//Must be the size of the rom allotment
	    FileInputStream fis = null;
	    
	    try{
	        try{
	            //If file is valid, add information to romInfo
        	    if (openFile(rom) && fileSize < CPU.ROM_SIZE) {
        	        int count = 0;
                    fis = new FileInputStream(rom);
        	        while(fis.available() > 0){//Must be read byte by byte
        	            romInfo[count++] = fis.read();
        	        }
                }
        	    else
        	        System.out.println("File not formatted correctly");
	        }
	        finally{
	            fis.close();
	        }
	    }
	    catch(IOException e){
	        //File not read
	        e.printStackTrace();
	    }
		return romInfo;
	}

	/**
	 * Writes an object State to a file fileName
	 * Appends the extension ".sav" to the fileName
	 * Saves the .sav file to the local directory
	 * 
	 * @param currentState
	 * @param fileName
	 */
	public static void writeState(State currentState, String fileName){
	    try{
	        //Append .sav to fileName
	        OutputStream file = new FileOutputStream(fileName + ".sav");
	        OutputStream buffer = new BufferedOutputStream( file );
	        ObjectOutput output = new ObjectOutputStream( buffer );
	        try{
	          //Write out state object
	          output.writeObject(currentState);
	        }
	        finally{
	            //Make sure stream is closed
	          output.close();
	        }
	      }  
	      catch(IOException ex){
	          //File was not outputted properly
	          System.out.println("Cannot output file");
	      }
	}
	
	/**
	 * Reads in a state from a .sav file
	 * If the file is not formatted properly, will return an error
	 * 
	 * @param fileName Path name of the file location
	 * @return The State from the file
	 */
	public static State readState(String fileName){
	    try{
	        //use buffering
	        InputStream file = new FileInputStream(fileName);
	        InputStream buffer = new BufferedInputStream( file );
	        ObjectInput input = new ObjectInputStream ( buffer );
	        try{
	          //deserialize the state
	          State recoveredState = (State)input.readObject();
	          return recoveredState;
	        }
	        finally{
	          input.close();
	        }
	      }
	      catch(ClassNotFoundException ex){
	        System.out.println("Cannot perform input. Class not found.");
	      }
	      catch(IOException ex){
	          System.out.println("Cannot perform input.");
	      }

	    return null;
	}
	
	/**
	 * Debugging method
	 * Will print out the state and append it to a file
	 * 
	 * @param currentState The state to append to fle
	 * @param logName Name of the log file
	 */
	public static void logStatus(State currentState, String logName){
	    try {
	        //Append currentState info to file
	        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logName, true)));
	        out.println(currentState.toString());
	        out.close();
	    } catch (IOException e) {
	        //File not appended
	    }
	}
}
