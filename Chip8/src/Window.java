/**
 * This class handles all aspects of the GUI. It fetches
 * information from the CPU class to form the images. It also
 * loads files. 
 * 
 * @author Nai Chen Chang (nc2539)
 */

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class Window extends JPanel implements ActionListener
{
	//define variables for the window
    private JFrame frame = new JFrame();
	private JMenu menu = new JMenu("File");
	private JMenuItem open, save, load;
	private JMenuBar menuBar = new JMenuBar();
	private int displayWidth;
	private int displayLength;
	private JFileChooser fileChooser;
	private File file;
	private int[][][] screenData;
	private CPU cpu;
	private BufferedImage I;
	private BufferedImage tempI;
	
    private static final long serialVersionUID = 1L;
	private final int ENLARGE = 15;
	private final int X_OFFSET = 15, Y_OFFSET = 63;
	
	//constructor
	public Window(CPU current)
	{
		//set variables
	    displayWidth = ENLARGE*CPU.X_DIM;
	    displayLength = ENLARGE*CPU.Y_DIM;
	    screenData = new int[CPU.X_DIM][CPU.Y_DIM][3];
	    I = new BufferedImage(displayWidth, displayLength, BufferedImage.TYPE_INT_RGB);
	    tempI = new BufferedImage(displayWidth, displayLength, BufferedImage.TYPE_INT_RGB);
	    fileChooser = new JFileChooser();
	    
	    cpu = current;
		setup(); //sets up intial screen which is black
		frame.setTitle("Chip 8 Emulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		open = new JMenuItem("New Game");
		save = new JMenuItem("Save");
		load = new JMenuItem("Load");

		//add all items to menu 
		menu.add(open);
		menu.add(load);
		menu.add(save);
		
		menuBar.add(menu); //add menu to menu bar
		frame.setJMenuBar(menuBar);  //set menu bar
		frame.setContentPane(this);  //set the content pane to this since extends JPanel
		
		//add listeners for open,load,save buttons
		OpenFileListener openListener = new OpenFileListener("open");
		open.addActionListener(openListener);
		
		OpenFileListener loadListener = new OpenFileListener("load");
		load.addActionListener(loadListener);
		
		save.addActionListener(this);
		
		//if drawflag is true
		if(cpu.getDrawFlag())
		{		
			convert();  //convert 2D screenData to image
			enlarge();  //enlarge image
		}
			
		//add keylistener to frame so that keys can be pressed and recognized
		frame.setFocusable(true);  
		frame.addKeyListener(new KeyListenerClass(cpu));
		
		//set frame size, the offset is added because the menu bar
		//add extra width and length
		frame.setSize(new Dimension(displayWidth+X_OFFSET,displayLength+Y_OFFSET));
		frame.setVisible(true);  //set visible
	}
	
	/**
	 * This method sets up the initial screen which is black
	 */
	public void setup()
	{
		//nested loop that goes through screenData and set all to black
		for(int x = 0; x < CPU.X_DIM; x++)		
			for(int y = 0; y < CPU.Y_DIM; y++)
				screenData[x][y][0] = screenData[x][y][1] = screenData[x][y][2] = 0;
	}
	
	/**
	 * This method converts data from screenData to image tempI
	 */
	public void convert()
	{
		//nested loop through all screenData
		for(int x = 0; x < CPU.X_DIM; x++)		
		{
			for(int y = 0; y < CPU.Y_DIM; y++)
			{
				int rgb;  //initialize rgb value
				//if = to 255 then set rgb value to white
				//else black
				if(screenData[x][y][0] == 255) 
					rgb = 0xFFFFFF;  //white
				else
					rgb = 0x000000;  //black
				
				tempI.setRGB(x, y, rgb);  //set rgb in image tempI
			}
		}
	}
	
	/**
	 * This method enlarges the picture.
	 */
	public void enlarge() 
	{
        int w = displayWidth;  //enlarged width
        int l = displayLength;  //enlarged 
        
        //enlarged image
        BufferedImage enlargedImage = new BufferedImage(w, l, tempI.getType());
        
        //go through enlarged image and set rgb value
        //it gets the rgb from the temporary image tempI. I divide the
        //coordinates by enlarge so that I can fill each pixel in enlarged image
        for (int x=0; x < w; x++)
            for (int y=0; y < l; y++)
                enlargedImage.setRGB(x, y, tempI.getRGB(x/ENLARGE, y/ENLARGE));
        
       I = enlargedImage; //set image I to enlarged image
	}
	
	/**
	 * This method updates screenData for the appropriate colors in each pixel
	 * @param cpu CPU class
	 */
	private void update(CPU cpu)
	{
		boolean[][] window = cpu.getWindow();  //get 2D array of info from windows
		
		//nested for loop that goes through window and screenData
		for(int y = 0; y < CPU.Y_DIM; ++y)
		{
			for(int x = 0; x < CPU.X_DIM; ++x)
			{
				if(window[x][y] == false)  //if equals to false then it's black
					screenData[x][y][0] = screenData[x][y][1] = screenData[x][y][2] = 0; //black
				else   //other wise it's white
					screenData[x][y][0] = screenData[x][y][1] = screenData[x][y][2] = 255;  //white				
			}	
		}
	}

	/**
	 * This method redisplays the screen when called.
	 */
    public void display()
    {
    	if(cpu.getDrawFlag())  //if drawflag true then update screen
    	{
    		update(cpu);   //update 2D array screendata to correct rgb values
    		convert();    //convert array data to image
    		enlarge();    //enlarge image
    		cpu.setDrawFlag(false);   //set drawflag to false
    	}
    }
	
    /**
     * This method repaints the images in the jpanel and avoids
     * repainting the entire frame and covering the menu bar.
     */
    public void paintComponent(Graphics g)
    { 	
    	g.drawImage(I,0,0, this);
    	repaint();
    }
    
    /**
     * This class handles opening files, and then passes the file
     * to cpu to load. 
     */
    private class OpenFileListener implements ActionListener 
	{
	    private String type;
	    
	    public OpenFileListener(String type){
	        this.type = type;
	    }
	    
	    //if open or load clicked
		public void actionPerformed(ActionEvent arg0) 
		{
			//pop open a file chooser box to ask user for file
			if (JFileChooser.APPROVE_OPTION == fileChooser.showOpenDialog(frame)) 
			{
				//get path
				String file = fileChooser.getSelectedFile().getAbsolutePath();
				if(type.equals("load"))  
				    cpu.loadState(file);  //load
				else if(type.equals("open"))
				    cpu.loadROM(file);    // open rom
			}	
		}
	}

    /**
     * This method handles when save button is pressed.
     */
	public void actionPerformed(ActionEvent arg0) 
	{
		if(arg0.getSource().equals(save)) //if save button
		{
			//ask for save name
		    String fileName = JOptionPane.showInputDialog("Please enter save name:");
			cpu.saveState(fileName);  //call save method
			JOptionPane.showMessageDialog(null, "Your file has been saved!", "Order", 2);  //box pops open
		}
	}
	
    //accessor method for file
    public File getFile()
	{
		return file;
	}
}
