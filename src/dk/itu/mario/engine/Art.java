package dk.itu.mario.engine;

import java.awt.Image;


public class Art
{

    public static Image[][] mario;
    public static Image[][] smallMario;
    public static Image[][] fireMario;
    public static Image[][] enemies;
    public static Image[][] items;
    public static Image[][] level;
    public static Image[][] particles;

    private static final String PREFIX="res";


    public static void init()
    {
    	mario = cutImage(PREFIX+"/mariosheet.png", 32, 32);
    	smallMario = cutImage(PREFIX+"/smallmariosheet.png", 16, 16);
    	fireMario = cutImage(PREFIX+"/firemariosheet.png", 32, 32);
    	enemies = cutImage(PREFIX+"/enemysheet.png", 16, 32);
    	items = cutImage(PREFIX+ "/itemsheet.png", 16, 16);
    	level = cutImage(PREFIX+"/mapsheet.png", 16, 16);
    	particles = cutImage(PREFIX+"/particlesheet.png", 8, 8);
           
       
    }

    private static Image[][] cutImage(String imageName, int xSize, int ySize)
    {
    	int width = 0;
    	int height = 0;
    	if(imageName.contains("/mariosheet")){
    		width = 480;
    		height = 32;
    	}
    	else if (imageName.contains("/smallmariosheet")){
    		width = 176;
    		height = 16;
    	}
    	else if (imageName.contains("/firemariosheet")){
    		width = 480;
    		height = 32;
    	}
    	else if (imageName.contains("/enemysheet")){
    		width = 256;
    		height = 256;
    	}
    	else if (imageName.contains("/itemsheet")){
    		width = 256;
    		height = 256;
    	}
    	else if (imageName.contains("/mapsheet")){
    		width = 256;
    		height = 256;
    	}
    	else if (imageName.contains("/particlesheet")){
    		width = 64;
    		height = 64;
    	}
        Image[][] images = new Image[width / xSize][height / ySize];
        
        return images;
    }

}
