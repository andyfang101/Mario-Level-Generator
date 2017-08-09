package dk.itu.mario.engine;
import java.awt.GraphicsConfiguration;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

	public class LevelSceneTest extends LevelScene{

			ArrayList<Double> switchPoints;
			private double thresshold; //how large the distance from point to mario should be before switching
			private int point = -1;
			private int []checkPoints;
			private boolean isCustom;


			public LevelSceneTest(long seed, int levelDifficulty, int type,boolean isCustom){
				super(seed,levelDifficulty,type);
				this.isCustom = isCustom;
			}

			public void init() {
				//Load in behaviors
		        if(level==null){
		        	LabeledLevel labeledLevel = new LabeledLevel("831");
	        		currentLevel = labeledLevel.getLevel();
			        try {
						 level = currentLevel.clone();
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
		        }
		        paused = false;
		        Sprite.spriteContext = this;
		        sprites.clear();
		        layer = new LevelRenderer(level, 320, 240);
		        mario = new Mario(this, currAgent);
		        sprites.add(mario);
		        startTime = 1;

		        timeLeft = 200*15;
		        tick = 0;
		        
		        /*
		         * SETS UP ALL OF THE CHECKPOINTS TO CHECK FOR SWITCHING
		         */
		        switchPoints = new ArrayList<Double>();

		        //first pick a random starting waypoint from among ten positions
		    	int squareSize = 16; //size of one square in pixels
		        int sections = 10;

		    	double startX = 32; //mario start position
		    	double endX = level.getxExit()*squareSize; //position of the end on the level
		    	
		        gameStarted = false;
			}



			public void tick(){
				super.tick();

			}

			public void deathActions(){
				if(this.mario.lives <=0){//has no more lives
					//TODO; put in a signal here
				}
				else{ // mario still has lives to play :)--> have a new beginning
					reset();
					mario.myAgent.resetAgent();
				}
			}

			public void bump(int x, int y, boolean canBreakBricks)
		    {
		        byte block = level.getBlock(x, y);

		        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BUMPABLE) > 0)
		        {
		            bumpInto(x, y - 1);
		            level.setBlock(x, y, (byte) 4);

		            if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_SPECIAL) > 0)
		            {
		                if (!this.mario.large)
		                {
		                    addSprite(new Mushroom(this, x * 16 + 8, y * 16 + 8));
		                }
		                else
		                {
		                    addSprite(new FireFlower(this, x * 16 + 8, y * 16 + 8));
		                }

		            }
		            else
		            {
		            	this.mario.getCoin();
		                addSprite(new CoinAnim(x, y));
		            }
		        }

		        if ((Level.TILE_BEHAVIORS[block & 0xff] & Level.BIT_BREAKABLE) > 0)
		        {
		            bumpInto(x, y - 1);
		            if (canBreakBricks)
		            {
		                level.setBlock(x, y, (byte) 0);
		                for (int xx = 0; xx < 2; xx++)
		                    for (int yy = 0; yy < 2; yy++)
		                        addSprite(new Particle(x * 16 + xx * 8 + 4, y * 16 + yy * 8 + 4, (xx * 2 - 1) * 4, (yy * 2 - 1) * 4 - 8));
		            }

		        }
		    }

			 public void bumpInto(int x, int y)
			    {
			        byte block = level.getBlock(x, y);
			        if (((Level.TILE_BEHAVIORS[block & 0xff]) & Level.BIT_PICKUPABLE) > 0)
			        {
			        	this.mario.getCoin();
			            level.setBlock(x, y, (byte) 0);
			            addSprite(new CoinAnim(x, y + 1));
			        }

			        for (Sprite sprite : sprites)
			        {
			            sprite.bumpCheck(x, y);
			        }
			    }

			private int randomNumber(int low, int high){
				return new Random(new Random().nextLong()).nextInt(high-low)+low;
			}

			private int toBlock(float n){
				return (int)(n/16);
			}

			private int toBlock(double n){
				return (int)(n/16);
			}

			private float toReal(int b){
				return b*16;
			}



}
