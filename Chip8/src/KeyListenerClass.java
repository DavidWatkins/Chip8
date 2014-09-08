/**
 * This class is responsible for the actions taken after a key is pressed.
 * @author Nai Chen Chang
 */

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyListenerClass implements KeyListener
{
	private CPU cpu;
	
	public KeyListenerClass(CPU c)
	{
		cpu = c;
	}
	//when key is pressed
	public void keyPressed(KeyEvent e) 
	{
		char key = e.getKeyChar(); //get key character
		//below is a switch statement that addresses each
		//key that is relevant to the game. Then it calls
		//set key in cpu class and set at a particular location
		//to 1 - true.
		switch(key)
		{
			case '1':    //if equal to 1
				cpu.setKey(0x1,1);
				break;
			case '2':
				cpu.setKey(0x2,1);
				break;
			case '3':
				cpu.setKey(0x3,1);
				break;
			case '4':
				cpu.setKey(0xC,1);
				break;
			case 'q':
				cpu.setKey(0x4,1);
				break;
			case 'w':
				cpu.setKey(0x5,1);
				break;
			case 'e':
				cpu.setKey(0x6,1);
				break;
			case 'r':
				cpu.setKey(0xD,1);
				break;
			case 'a':
				cpu.setKey(0x7,1);
				break;
			case 's':
				cpu.setKey(0x8,1);
				break;
			case 'd':
				cpu.setKey(0x9,1);
				break;
			case 'f':
				cpu.setKey(0xE,1);
				break;
			case 'z':
				cpu.setKey(0xA,1);
				break;
			case 'x':
				cpu.setKey(0x0,1);
				break;
			case 'c':
				cpu.setKey(0xB,1);
				break;
			case 'v':
				cpu.setKey(0xF,1);
				break;
		}
	}

	//when key is released
	public void keyReleased(KeyEvent e) 
	{
		char key = e.getKeyChar();  //get key character
		//below is a switch statement that addresses each
		//key that is relevant to the game. Then it calls
		//set key in cpu class and set at a particular location
		//to 0 - false.
		switch(key)
		{
			case '1':
				cpu.setKey(0x1,0);
				break;
			case '2':
				cpu.setKey(0x2,0);
				break;
			case '3':
				cpu.setKey(0x3,0);
				break;
			case '4':
				cpu.setKey(0xC,0);
				break;
			case 'q':
				cpu.setKey(0x4,0);
				break;
			case 'w':
				cpu.setKey(0x5,0);
				break;
			case 'e':
				cpu.setKey(0x6,0);
				break;
			case 'r':
				cpu.setKey(0xD,0);
				break;
			case 'a':
				cpu.setKey(0x7,0);
				break;
			case 's':
				cpu.setKey(0x8,0);
				break;
			case 'd':
				cpu.setKey(0x9,0);
				break;
			case 'f':
				cpu.setKey(0xE,0);
				break;
			case 'z':
				cpu.setKey(0xA,0);
				break;
			case 'x':
				cpu.setKey(0x0,0);
				break;
			case 'c':
				cpu.setKey(0xB,0);
				break;
			case 'v':
				cpu.setKey(0xF,0);
				break;
		}
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}


