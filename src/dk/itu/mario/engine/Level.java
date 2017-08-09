package dk.itu.mario.engine;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;


public class Level implements LevelInterface
{
    protected static final byte BLOCK_EMPTY	= (byte) (0 + 1 * 16);
    protected static final byte BLOCK_POWERUP	= (byte) (4 + 2 + 1 * 16);
    protected static final byte BLOCK_COIN	= (byte) (4 + 1 + 1 * 16);
    protected static final byte GROUND		= (byte) (1 + 9 * 16);
    protected static final byte ROCK			= (byte) (9 + 0 * 16);
    protected static final byte WOOD			= (byte) (12 + 0 * 16);
    protected static final byte STEEL			= (byte) (12 + 1 * 16);
    protected static final byte LOG1			= (byte) (9 + 1 * 16);
    protected static final byte LOG2			= (byte) (9 + 2 * 16);
    protected static final byte LOG3			= (byte) (9 + 3 * 16);
    protected static final byte COIN			= (byte) (2 + 2 * 16);

    protected static final byte TINYPIPE1			= (byte) (8 + 1 * 16);
    protected static final byte TINYPIPE2			= (byte) (8 + 2 * 16);
    protected static final byte TINYPIPE3			= (byte) (8 + 3 * 16);

    protected static final byte LEFT_GRASS_EDGE = (byte) (0+9*16);
    protected static final byte RIGHT_GRASS_EDGE = (byte) (2+9*16);
    protected static final byte RIGHT_UP_GRASS_EDGE = (byte) (2+8*16);
    protected static final byte LEFT_UP_GRASS_EDGE = (byte) (0+8*16);
    protected static final byte LEFT_POCKET_GRASS = (byte) (3+9*16);
    protected static final byte RIGHT_POCKET_GRASS = (byte) (3+8*16);

    protected static final byte HILL_FILL = (byte) (5 + 9 * 16);
    protected static final byte HILL_LEFT = (byte) (4 + 9 * 16);
    protected static final byte HILL_RIGHT = (byte) (6 + 9 * 16);
    protected static final byte HILL_TOP = (byte) (5 + 8 * 16);
    protected static final byte HILL_TOP_LEFT = (byte) (4 + 8 * 16);
    protected static final byte HILL_TOP_RIGHT = (byte) (6 + 8 * 16);

    protected static final byte HILL_TOP_LEFT_IN = (byte) (4 + 11 * 16);
    protected static final byte HILL_TOP_RIGHT_IN = (byte) (6 + 11 * 16);

    protected static final byte TUBE_TOP_LEFT = (byte) (10 + 0 * 16);
    protected static final byte TUBE_TOP_RIGHT = (byte) (11 + 0 * 16);

    protected static final byte TUBE_SIDE_LEFT = (byte) (10 + 1 * 16);
    protected static final byte TUBE_SIDE_RIGHT = (byte) (11 + 1 * 16);

    //The level's width and height
    protected int width;
    protected int height;

    //This map of WIDTH * HEIGHT that contains the level's design
    private byte[][] map;

    //This is a map of WIDTH * HEIGHT that contains the placement and type enemies
    private SpriteTemplate[][] spriteTemplates;

    //These are the place of the end of the level
    protected int xExit;
    protected int yExit;
    
    //public ArrayList<Point>listaInicioFimTelas = new ArrayList<Point>();
    
    public byte[][] data;		 //Estava sendo utilizada na outra arquitetura
    public byte[][] observation; //Estava sendo utilizada na outra arquitetura
    
    private static final int FILE_HEADER = 0x271c4178;
    
    public static final String[] BIT_DESCRIPTIONS = {//
        "BLOCK UPPER", //
                "BLOCK ALL", //
                "BLOCK LOWER", //
                "SPECIAL", //
                "BUMPABLE", //
                "BREAKABLE", //
                "PICKUPABLE", //
                "ANIMATED",//
        };

    public Level(){

    }

    public Level(int width, int height)
    {
        this.width = width;
        this.height = height;

        xExit = 10;
        yExit = 10;
        map = new byte[width][height];
        spriteTemplates = new SpriteTemplate[width][height];
    }
    

    /**
    *Clone the level data so that we can load it when Mario dies
    */
    public Level clone() throws CloneNotSupportedException {

        Level clone=new Level(width, height);

        clone.map = new byte[width][height];
    	clone.spriteTemplates = new SpriteTemplate[width][height];
    	clone.xExit = xExit;
    	clone.yExit = yExit;

    	for (int i = 0; i < map.length; i++)
    		for (int j = 0; j < map[i].length; j++) {
    			clone.map[i][j]= map[i][j];
    			if ( spriteTemplates[i][j]!=null){
    				clone.spriteTemplates[i][j] = (SpriteTemplate) spriteTemplates[i][j].clone();
    			}
    			else{
    				clone.spriteTemplates[i][j] =spriteTemplates[i][j];
    			}
    	}

        return clone;

      }

    public void tick(){}
    

    public byte getBlockCapped(int x, int y)
    {
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x >= width) x = width - 1;
        if (y >= height) y = height - 1;
        return map[x][y];
    }

    public byte getBlock(int x, int y)
    {
        if (x < 0) x = 0;
        if (y < 0) return 0;
        if (x >= width) x = width - 1;
        if (y >= height) y = height - 1;
        return map[x][y];
    }

    public void setBlock(int x, int y, byte b)
    {
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        map[x][y] = b;
    }

    public boolean isBlocking(int x, int y, float xa, float ya)
    {
        byte block = getBlock(x, y);

        boolean blocking = ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_ALL) > 0;
        blocking |= (ya > 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_UPPER) > 0;
        blocking |= (ya < 0) && ((TILE_BEHAVIORS[block & 0xff]) & BIT_BLOCK_LOWER) > 0;

        return blocking;
    }

    public SpriteTemplate getSpriteTemplate(int x, int y)
    {
        if (x < 0) return null;
        if (y < 0) return null;
        if (x >= width) return null;
        if (y >= height) return null;
        return spriteTemplates[x][y];
    }

    public void setSpriteTemplate(int x, int y, SpriteTemplate spriteTemplate)
    {
        if (x < 0) return;
        if (y < 0) return;
        if (x >= width) return;
        if (y >= height) return;
        spriteTemplates[x][y] = spriteTemplate;
    }
    
    public void SetSpritesNotSpawned(){
    	for(int x = 0; x<width; x++){
    		for(int y=0; y<height; y++){
    			if(spriteTemplates[x][y]!=null){
    				spriteTemplates[x][y].hasSpawned = false;
    			}
    		}
    	}
    }

    public SpriteTemplate[][] getSpriteTemplate(){
    	return this.spriteTemplates;
    }

    public void resetSpriteTemplate(){
    	for (int i = 0; i < spriteTemplates.length; i++) {
			for (int j = 0; j < spriteTemplates[i].length; j++) {

				SpriteTemplate st = spriteTemplates[i][j];
				if(st != null)
					st.isDead = false;
			}
		}
    }


    public void print(byte[][] array){
    	for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < array[i].length; j++) {
				System.out.print(array[i][j]);
			}
			System.out.println();
		}
    }
    
    public void printMap(){
    	print(map);
    }
    
    public static Level load(DataInputStream dis) throws IOException
    {
        long header = dis.readLong();
        if (header != Level.FILE_HEADER) throw new IOException("Bad level header");
        @SuppressWarnings("unused")
		int version = dis.read() & 0xff;

        int width = dis.readShort() & 0xffff;
        int height = dis.readShort() & 0xffff;      
        
//        System.out.println("width = " + width);
//        System.out.println("height = " + height);
        
        Level level = new Level(width, height);

        level.xExit = dis.readInt();
        level.yExit = dis.readInt();
                
        level.map = new byte[width][height];
        level.data = new byte[width][height];
        
        for (int i = 0; i < width; i++)
        {
            dis.readFully(level.map[i]);
            dis.readFully(level.data[i]);
        }
        
//        for (int i = 0; i < width; i++)
//        {        	
//        	System.out.print("map " + i + " : ");
//        	for (int j = 0; j < height; j++)
//        	{
//        		System.out.print(level.map[i][j] + " ");
//        	}        	
//        	System.out.println();
        	
//        	System.out.print("data " + i + " : ");
//        	for (int j = 0; j < height; j++)
//        	{
//        		System.out.print(level.data[i][j] + " ");
//        	}
//        	System.out.println();
//        }
        int tam = dis.readInt();
                
        for (int i = 0; i < tam; i++)
        {
        	int x = dis.readInt();
        	int y = dis.readInt();
            int type = dis.readInt();
            int winged = dis.readInt();
            
            level.getSpriteTemplate()[x][y] = new SpriteTemplate(type, winged == 1 ? true : false);
            //System.out.println("tam: " + x + ", " + y + ", " + type + ", " + winged);
        }

        return level;
    }
    
	public byte[][] getMap() {
		return map;
	}
	public SpriteTemplate[][] getSpriteTemplates() {
		return spriteTemplates;
	}
	public int getxExit() {
		return xExit;
	}
	public int getyExit() {
		return yExit;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public String getName() {
		return "";
	}
	
	public int GetNumEnemies(){
		int numEnemies = 0;
		
		for (int i = 0; i < spriteTemplates.length; i++) {
			for (int j = 0; j < spriteTemplates[i].length; j++) {

				SpriteTemplate st = spriteTemplates[i][j];
				if(st != null &&st.type>=0 && st.type<7){
					numEnemies +=1;
				}
			}
		}
		
		return numEnemies;
	}
	
	public int GetNumGaps(){
		int numGaps = 0;
		for(int x = 0; x<getxExit(); x++){
			if(getBlock(x,getHeight()-1)==0 && getBlock(x-1,getHeight()-1)!=0){
				numGaps+=1;
			}
		}
		
		return numGaps;
	}
	
	public double GetAverageGapLength(){
		double avgGap = 0.0;
		int numGaps = GetNumGaps();
		
		int prevGapX = -1;
		for(int x = 0; x<getxExit(); x++){
			if(getBlock(x,getHeight()-1)==0 && getBlock(x-1,getHeight()-1)!=0){
				prevGapX = x;
			}
			else if(getBlock(x,getHeight()-1)!=0 && getBlock(x-1,getHeight()-1)==0){
				avgGap+=(x-prevGapX);
			}
		}
		
		if(numGaps>0){
			avgGap = avgGap/((double)numGaps);
		}
		return avgGap;
	}
	
	public int GetPowerups(){
		int powerups =0;
	
		for(int x = 0; x<getxExit(); x++){
			for(int y = 0; y<getHeight(); y++){
				if(getBlock(x,y)==BLOCK_POWERUP){
					powerups+=1;
				}
			}
		}
		
		return powerups;
	}
	
	public int GetBlocks(){
		int blocks =0;
	
		for(int x = 0; x<getxExit(); x++){
			for(int y = 0; y<getHeight(); y++){
				if(getBlock(x,y)==BLOCK_EMPTY){
					blocks+=1;
				}
			}
		}
		
		return blocks;
	}
	
	public int GetCoins(){
		int coins =0;
	
		for(int x = 0; x<getxExit(); x++){
			for(int y = 0; y<getHeight(); y++){
				if(getBlock(x,y)==BLOCK_COIN || getBlock(x,y)==COIN){
					coins+=1;
				}
			}
		}
		
		return coins;
	}
	
	public int GetCannons(){
		int cannons =0;
	
		for(int x = 0; x<getxExit(); x++){
			for(int y = 0; y<getHeight(); y++){
				if(getBlock(x,y)==(14 + 0 * 16)){
					cannons+=1;
				}
			}
		}
		
		return cannons;
	}
	
	public static boolean isSolid(byte b){
		Integer[] solidBlocks = new Integer[29];
		solidBlocks[0] = new Integer(BLOCK_EMPTY);
		solidBlocks[1] = new Integer(BLOCK_POWERUP);
		solidBlocks[2] = new Integer(BLOCK_COIN);
		solidBlocks[3] = new Integer(GROUND);
		solidBlocks[4] = new Integer(ROCK);
		solidBlocks[21] = new Integer(WOOD);
		solidBlocks[22] = new Integer(STEEL);
		
		solidBlocks[23] = new Integer(LOG1);
		solidBlocks[24] = new Integer(LOG2);
		solidBlocks[25] = new Integer(LOG3);
		
		solidBlocks[26] = new Integer(TINYPIPE1);
		solidBlocks[27] = new Integer(TINYPIPE2);
		solidBlocks[28] = new Integer(TINYPIPE3);
		
		solidBlocks[5] = new Integer(HILL_TOP);
		solidBlocks[6] = new Integer(HILL_TOP_LEFT);
		solidBlocks[7] = new Integer(HILL_TOP_RIGHT);
		solidBlocks[8] = new Integer(HILL_TOP_LEFT_IN);
		solidBlocks[9] = new Integer(HILL_TOP_RIGHT_IN);
		
		solidBlocks[10] = new Integer(TUBE_TOP_LEFT);
		solidBlocks[11] = new Integer(TUBE_TOP_RIGHT);
		solidBlocks[12] = new Integer(TUBE_SIDE_LEFT);
		solidBlocks[13] = new Integer(TUBE_SIDE_RIGHT);
		
		//solidBlocks[14] = -127;
		solidBlocks[14] = new Integer(LEFT_GRASS_EDGE);
		solidBlocks[15] = new Integer(RIGHT_GRASS_EDGE);
		solidBlocks[16] = new Integer(LEFT_UP_GRASS_EDGE);
		solidBlocks[17] = new Integer(RIGHT_UP_GRASS_EDGE);
		solidBlocks[18] = new Integer(LEFT_POCKET_GRASS);
		solidBlocks[19] = new Integer(RIGHT_POCKET_GRASS);
		solidBlocks[20] = new Integer(-127);
		
		
		Integer bInt = new Integer((int)b);
		
		return Arrays.asList(solidBlocks).contains(bInt);//IntStream.of(solidBlocks).anyMatch(x -> x == (int)b);
	}
	
	public static boolean isPipeBottom(byte b){
		return TUBE_SIDE_LEFT==(int)b || TUBE_SIDE_RIGHT==(int)b;
	}
	
	public static boolean isPipeTop(byte b){
		return TUBE_TOP_LEFT==(int)b || TUBE_TOP_RIGHT==(int)b;
	}
	
	public static boolean isCoin(byte b){
		
		return COIN==(int)b;
	}
	
	public static boolean isPowerup(byte b){
		
		return BLOCK_POWERUP==(int) b;
	}
	
	public static boolean isBreakable(byte b){
		
		return BLOCK_EMPTY==(int) b || BLOCK_COIN==(int) b;
	}
	
	public static boolean isCannon(byte b){
		int bInt = (int) b;
		return (14 + 0 * 16)==bInt || (14 + 1 * 16)==bInt || (14 + 2 * 16)==bInt;
	}			
}
