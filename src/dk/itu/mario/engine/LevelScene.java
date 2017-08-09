package dk.itu.mario.engine;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.*;



public class LevelScene extends Scene implements SpriteContext
{
    protected List<Sprite> sprites = new ArrayList<Sprite>();
    protected List<Sprite> spritesToAdd = new ArrayList<Sprite>();
    protected List<Sprite> spritesToRemove = new ArrayList<Sprite>();

    public Level level;
//    public Level levelTemp;
    public Mario mario;
    public float xCam, yCam, xCamO, yCamO;
    public static Image tmpImage;
    protected int tick;

    public LevelRenderer layer;
    protected Level currentLevel;

    public boolean paused = false;
    public int startTime = 0;
    public int timeLeft;

    //    private Recorder recorder = new Recorder();
    //    private Replayer replayer = null;

    protected long levelSeed;
    protected int levelType;
    protected int levelDifficulty;
    
    public boolean gameStarted;

    public static boolean bothPlayed = false;

    private int []xPositionsArrow;
    private int []yPositionsArrow;
    private int widthArrow,heightArrow,tipWidthArrow;
    private int xArrow,yArrow;
    
    protected Agent currAgent;
    

    public LevelScene(long seed, int levelDifficulty, int type)
    {
        this.levelSeed = seed;
        this.levelDifficulty = levelDifficulty;
        this.levelType = type;

        widthArrow = 25;
    	tipWidthArrow = 10;
    	heightArrow = 20;

    	xArrow = 160;
    	yArrow = 40;

    	xPositionsArrow = new int[]{xArrow+-widthArrow/2,xArrow+widthArrow/2-tipWidthArrow,xArrow+widthArrow/2-tipWidthArrow,xArrow+widthArrow/2,xArrow+widthArrow/2-tipWidthArrow,xArrow+widthArrow/2-tipWidthArrow,xArrow+-widthArrow/2};
    	yPositionsArrow = new int[]{yArrow+-heightArrow/4,yArrow+-heightArrow/4,yArrow+-heightArrow/2,yArrow+0,yArrow+heightArrow/2,yArrow+heightArrow/4,yArrow+heightArrow/4};
    }

    public void init()
    {

    }

    public int fireballsOnScreen = 0;

    List<Shell> shellsToCheck = new ArrayList<Shell>();

    public void checkShellCollide(Shell shell)
    {
        shellsToCheck.add(shell);
    }

    List<Fireball> fireballsToCheck = new ArrayList<Fireball>();

    public void checkFireballCollide(Fireball fireball)
    {
        fireballsToCheck.add(fireball);
    }

    public void tick(){
        timeLeft--;
        if( widthArrow < 0){
        	widthArrow*=-1;
        	tipWidthArrow*=-1;

        	xPositionsArrow = new int[]{xArrow+-widthArrow/2,xArrow+widthArrow/2-tipWidthArrow,xArrow+widthArrow/2-tipWidthArrow,xArrow+widthArrow/2,xArrow+widthArrow/2-tipWidthArrow,xArrow+widthArrow/2-tipWidthArrow,xArrow+-widthArrow/2};
        	yPositionsArrow = new int[]{yArrow+-heightArrow/4,yArrow+-heightArrow/4,yArrow+-heightArrow/2,yArrow+0,yArrow+heightArrow/2,yArrow+heightArrow/4,yArrow+heightArrow/4};

        }

        if (timeLeft==0)
        {
            mario.dieTime();
        }

        xCamO = xCam;
        yCamO = yCam;

        if (startTime > 0)
        {
            startTime++;
        }

        float targetXCam = mario.x - 160;

        xCam = targetXCam;

        if (xCam < 0) xCam = 0;
        if (xCam > level.getWidth() * 16 - 320) xCam = level.getWidth() * 16 - 320;

        /*      if (recorder != null)
         {
         recorder.addTick(mario.getKeyMask());
         }

         if (replayer!=null)
         {
         mario.setKeys(replayer.nextTick());
         }*/

        fireballsOnScreen = 0;

        for (Sprite sprite : sprites)
        {
            if (sprite != mario)
            {
                float xd = sprite.x - xCam;
                float yd = sprite.y - yCam;
                if (xd < -64 || xd > 320 + 64 || yd < -64 || yd > 240 + 64)
                {
                    removeSprite(sprite);
                }
                else
                {
                    if (sprite instanceof Fireball)
                    {
                        fireballsOnScreen++;
                    }
                }
            }
        }

        if (paused)
        {
            for (Sprite sprite : sprites)
            {
                if (sprite == mario)
                {
                    sprite.tick();
                }
                else
                {
                    sprite.tickNoMove();
                }
            }
        }
        else
        {

            tick++;
            level.tick();

            boolean hasShotCannon = false;
            int xCannon = 0;

            for (int x = (int) xCam / 16 - 1; x <= (int) (xCam + layer.width) / 16 + 1; x++)
                for (int y = (int) yCam / 16 - 1; y <= (int) (yCam + layer.height) / 16 + 1; y++)
                {
                    int dir = 0;

                    if (x * 16 + 8 > mario.x + 16) dir = -1;
                    if (x * 16 + 8 < mario.x - 16) dir = 1;

                    SpriteTemplate st = level.getSpriteTemplate(x, y);

                    if (st != null && !st.hasSpawned)
                    {
                        if (st.lastVisibleTick != tick - 1)
                        {
                            if (st.sprite == null || !sprites.contains(st.sprite))
                            {
                                st.spawn(this, x, y, dir);

							}
                        }

                        st.lastVisibleTick = tick;
                    }

                    if (dir != 0)
                    {
                    	
                        byte b = level.getBlock(x, y);
                        if (((Level.TILE_BEHAVIORS[b & 0xff]) & Level.BIT_ANIMATED) > 0)
                        {
                            if ((b % 16) / 4 == 3 && b / 16 == 0)
                            {
                                if ((tick - x * 2) % 100 == 0)
                                {
                                	
                                    xCannon = x;
                                    for (int i = 0; i < 8; i++)
                                    {
                                        addSprite(new Sparkle(x * 16 + 8, y * 16 + (int) (Math.random() * 16), (float) Math.random() * dir, 0, 0, 1, 5));
                                    }
                                    addSprite(new BulletBill(this, x * 16 + 8 + dir * 8, y * 16 + 15, dir));
                                    hasShotCannon = true;
                                }
                            }
                        }
                    }
                }

           

            for (Sprite sprite : sprites)
            {
                sprite.tick();
            }

            for (Sprite sprite : sprites)
            {
                sprite.collideCheck();
            }

            for (Shell shell : shellsToCheck)
            {
                for (Sprite sprite : sprites)
                {
                    if (sprite != shell && !shell.dead)
                    {
                        if (sprite.shellCollideCheck(shell))
                        {
                            if (mario.carried == shell && !shell.dead)
                            {
                                mario.carried = null;
                                shell.die();
                            }
                        }
                    }
                }
            }
            shellsToCheck.clear();

            for (Fireball fireball : fireballsToCheck)
            {
                for (Sprite sprite : sprites)
                {
                    if (sprite != fireball && !fireball.dead)
                    {
                        if (sprite.fireballCollideCheck(fireball))
                        {
                            fireball.die();
                        }
                    }
                }
            }
            fireballsToCheck.clear();
        }

        sprites.addAll(0, spritesToAdd);
        sprites.removeAll(spritesToRemove);
        spritesToAdd.clear();
        spritesToRemove.clear();

    }
    
    private DecimalFormat df = new DecimalFormat("00");
    private DecimalFormat df2 = new DecimalFormat("000");

    public void render(Graphics g, float alpha)
    {
        int xCam = (int) (mario.xOld + (mario.x - mario.xOld) * alpha) - 160;
        int yCam = (int) (mario.yOld + (mario.y - mario.yOld) * alpha) - 120;
        //int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
        //        int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
        if (xCam < 0) xCam = 0;
        if (yCam < 0) yCam = 0;
        if (xCam > level.getWidth() * 16 - 320) xCam = level.getWidth() * 16 - 320;
        if (yCam > level.getHeight() * 16 - 240) yCam = level.getHeight() * 16 - 240;

        g.translate(-xCam, -yCam);
        for (Sprite sprite : sprites)
        {
            if (sprite.layer == 0) sprite.render(g, alpha);
        }
        g.translate(xCam, yCam);

        ////////////THIS RENDERS THE LEVEL
        layer.setCam(xCam, yCam);
        layer.render(g, tick, paused?0:alpha);
        layer.renderExit0(g, tick, paused?0:alpha, mario.winTime==0);
        ////////////END OF LEVEL RENDER


        ////////////RENDERS SPRITES
        g.translate(-xCam, -yCam);
        for (Sprite sprite : sprites)
        {
            if (sprite.layer == 1) sprite.render(g, alpha);
        }
        g.translate(xCam, yCam);
        g.setColor(Color.BLACK);
        layer.renderExit1(g, tick, paused?0:alpha);

        renderDirectionArrow(g);


        if (startTime > 0)
        {
            float t = startTime + alpha - 2;
            t = t * t * 0.6f;
            renderBlackout(g, 160, 120, (int) (t));
        }
//        mario.x>level.xExit*16
        if (mario.winTime > 0)
        {
        	
            float t = mario.winTime + alpha;
            t = t * t * 0.2f;
            
            if (t > 900)
            {

                	winActions();
                	return;
            }

            renderBlackout(g, (int) (mario.xDeathPos - xCam), (int) (mario.yDeathPos - yCam), (int) (320 - t));
        }

        if (mario.deathTime > 0)
        {
        	g.setColor(Color.BLACK);
            float t = mario.deathTime + alpha;
            t = t * t * 0.4f;

            if (t > 1800)
            {
            	this.mario.lives--;
            	deathActions();
           }

            renderBlackout(g, (int) (mario.xDeathPos - xCam), (int) (mario.yDeathPos - yCam), (int) (320 - t));
        }
    }

    public void winActions(){

    }

    public void deathActions(){

    }

    protected void reset(){
		paused = false;
        Sprite.spriteContext = this;
        sprites.clear();

        try {
			level = currentLevel.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        level.resetSpriteTemplate();
        /**
        level.setBlock(26, 12, (byte) 0);
        level.setBlock(27, 12, (byte) 0);
        level.setBlock(26, 11, (byte) 0);
        level.setBlock(27, 11, (byte) 0);
        level.setBlock(26, 10, (byte) 0);
        level.setBlock(27, 10, (byte) 0);
		*/
        layer = new LevelRenderer(level, 320, 240);

        double oldX = 0;

        if(mario!=null)
        	oldX = mario.x;

        mario = new Mario(this, currAgent);
        
        sprites.add(mario);
        startTime = 1;

        timeLeft = 200*15;
        tick = 0;
//        recorder = new DataRecorder(this,level,keys,gametype);
   //     recorder.detailedLog = "";
        gameStarted = false;
	}

    private void renderDirectionArrow(Graphics g){
    	if(widthArrow<0)
    		g.setColor(new Color(0,0,255,150));
    	else
    		g.setColor(new Color(255,0,0,150));

    	g.fillPolygon(xPositionsArrow,yPositionsArrow,Math.min(xPositionsArrow.length,yPositionsArrow.length));
    	g.setColor(new Color(0,0,0,255));
    	g.drawPolygon(xPositionsArrow,yPositionsArrow,Math.min(xPositionsArrow.length,yPositionsArrow.length));
    }

    float decrease = (float)0.03;
    float factor = 0;
    boolean in = true;
    String flipText = "FLIP! MOVE THE OTHER WAY!";

//    protected void renderFlip(Graphics g){
//
//        if(level.startFlipping){
//            if(in){
//            	factor += decrease;
//            	if(factor>=1){
//            		in = false;
//            		level.canFlip = true;
//            	}
//            }
//            else{
//
//            	factor -= decrease;
//
//            	if(factor<=0){
//            		in = true;
//            		level.startFlipping = false;
//            	}
//            }
//
//            int width = 320;
//            int height = 240;
//            int overlap = 20;
//
//        	g.setColor(Color.BLACK);
//        	g.fillRect(0,0,(int)((width/2)*factor) + overlap, height);
//
//        	g.setColor(Color.BLACK);
//        	g.fillRect(width - overlap -(int)((width/2)*factor),0,(int)((width/2)*factor) + overlap,height);
//
//        	//draw a box behind the string so you can see it
//        	int padding = 3;
//
//        	g.setColor(new Color(0,0,0,100));
//        	g.fillRect(width/2-flipText.length()*8/2-padding,height/2-padding,flipText.length()*8 + 2*padding,10 + 2*padding);
//        	drawString(g,flipText, width/2-flipText.length()*8/2+2, height/2+2, 0);
//        	drawString(g,flipText,width/2-flipText.length()*8/2,height/2,2);
//
//        }
//    }

    private void renderBlackout(Graphics g, int x, int y, int radius)
    {
        if (radius > 320) return;

        int[] xp = new int[20];
        int[] yp = new int[20];
        for (int i = 0; i < 16; i++)
        {
            xp[i] = x + (int) (Math.cos(i * Math.PI / 15) * radius);
            yp[i] = y + (int) (Math.sin(i * Math.PI / 15) * radius);
        }
        xp[16] = 320;
        yp[16] = y;
        xp[17] = 320;
        yp[17] = 240;
        xp[18] = 0;
        yp[18] = 240;
        xp[19] = 0;
        yp[19] = y;
        g.fillPolygon(xp, yp, xp.length);

        for (int i = 0; i < 16; i++)
        {
            xp[i] = x - (int) (Math.cos(i * Math.PI / 15) * radius);
            yp[i] = y - (int) (Math.sin(i * Math.PI / 15) * radius);
        }
        xp[16] = 320;
        yp[16] = y;
        xp[17] = 320;
        yp[17] = 0;
        xp[18] = 0;
        yp[18] = 0;
        xp[19] = 0;
        yp[19] = y;

        g.fillPolygon(xp, yp, xp.length);
    }


    public void addSprite(Sprite sprite)
    {
    	sprite.spriteContext = this;
        spritesToAdd.add(sprite);
        sprite.tick();
    }

    public void removeSprite(Sprite sprite)
    {
        spritesToRemove.add(sprite);
    }

    public float getX(float alpha)
    {
        int xCam = (int) (mario.xOld + (mario.x - mario.xOld) * alpha) - 160;
        //        int yCam = (int) (mario.yOld + (mario.y - mario.yOld) * alpha) - 120;
        //int xCam = (int) (xCamO + (this.xCam - xCamO) * alpha);
        //        int yCam = (int) (yCamO + (this.yCam - yCamO) * alpha);
        if (xCam < 0) xCam = 0;
        //        if (yCam < 0) yCam = 0;
        //        if (yCam > 0) yCam = 0;
        return xCam + 160;
    }

    public float getY(float alpha)
    {
        return 0;
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
    public void setLevel(Level level){
    	this.level = level;
    }

	@Override
	public void mouseClicked(MouseEvent me) {
		// TODO Auto-generated method stub

	}
	
	public List<Sprite> GetSprites(){
		return sprites;
	}
	
	public void SetTick(int _tick){
		this.tick = _tick;
	}
	
	public int GetTick(){
		return tick;
	}
	
	@Override protected Object clone() throws CloneNotSupportedException {
    	LevelScene c = new LevelScene(this.levelSeed,this.levelDifficulty,this.levelType);
    	if(this.mario!=null){
    		c.mario = (Mario) this.mario.clone();
    		c.mario.world = c;
    	}
    	c.level = (Level) this.level.clone();
    	
    	c.layer = layer;
    	c.SetTick(this.tick);
    	
    	List<Sprite> clone = new ArrayList<Sprite>(this.sprites.size());
        for(Sprite item: this.sprites) 
        {
        	if (item == mario)
        	{
        		clone.add(c.mario);
        	}
        	else
        	{
        		Sprite s = (Sprite) item.clone();
        		if ( (s instanceof Shell) && ((Shell) s).carried && c.mario.carried != null)
        			c.mario.carried = s;
        		if ((item instanceof Enemy) )
        			s = (Sprite)(((Enemy) item).clone());
        		if ((item instanceof FlowerEnemy))
        			s = (Sprite)(((FlowerEnemy) item.clone()));
        		if (( item instanceof BulletBill))
        			s = (Sprite)(((BulletBill) item.clone()));
        		s.world = c;
        		clone.add(s);
        	}
        }
        c.sprites = clone;
    	return c;
    }
	
	public LevelScene GetClone() throws CloneNotSupportedException{
		return (LevelScene) this.clone();
	}
	
	public String GetScreenStringRepresentationGranular(){
		String str = "";
		int marioX = 8;
		int marioY = -1;
		if(mario!=null){
			marioX = (int) mario.x/16;
			marioY = (int) mario.y/16;
		}
		int minX = 0;
		if (marioX-8>minX){
			minX = marioX-8;
		}
		
		int levelHeight = level.getHeight()+1;
		int levelWidth = level.getxExit()+1;
		for(int j = 0; j<levelHeight; j++){
			for(int i = 0; i<levelWidth; i++){
				Sprite enemyHere = null;
				if(marioX==i && marioY==j){
					str+="M";
				}
				else if(i<level.getWidth()){
					boolean enemyThere = false;
					for(int a = 0; a<sprites.size(); a++){
						if(mario!=sprites.get(a) && (SpriteTemplate.IsEnemy(sprites.get(a)) ||  sprites.get(a) instanceof BulletBill) ){
							int spriteX = (int)sprites.get(a).x/16;
							int spriteY = (int)sprites.get(a).y/16;
							if(spriteX==i && spriteY ==j){
								enemyThere = true;
								enemyHere = sprites.get(a);
								break;
							}
						}
					}
					
					if(enemyThere){
						
						SpriteTemplate template = enemyHere.spriteTemplate;
						
						//ENEMY TYPES
						if(enemyHere instanceof BulletBill){
							str+="U";
						}
						else if(template!=null){
							boolean winged = template.getWinged();
							switch(template.getType()){
	        				case(Enemy.ENEMY_RED_KOOPA):
	        					if(winged){
	        						//mapToUse[x][y] = Enemy.RED_KOOPA_WINGED;
	        						str+="R";
	        					}
	        					else{
	        						//mapToUse[x][y] = Enemy.RED_KOOPA_WINGLESS;
	        						str+="r";
	        					}
	        					break;
	        				case(Enemy.ENEMY_GREEN_KOOPA):
	        					if(winged){
	        						//mapToUse[x][y] = Enemy.GREEN_KOOPA_WINGED;
	        						str+="K";
	        					}
	        					else{
	        						//mapToUse[x][y] = Enemy.GREEN_KOOPA_WINGLESS;
	        						str+="k";
	        					}
	        					break;
	        				case(Enemy.ENEMY_GOOMBA):
	        					if(winged){
	        						//mapToUse[x][y] = Enemy.GOOMBA_WINGED;
	        						str+="G";
	        					}
	        					else{
	        						//mapToUse[x][y] = Enemy.GOOMBA_WINGLESS;
	        						str+="g";
	        					}
	        					break;
	        				case(Enemy.ENEMY_SPIKY):
	        					if(winged){
	        						//mapToUse[x][y] = Enemy.SPIKY_WINGED;
	        						str+="S";
	        					}
	        					else{
	        						//mapToUse[x][y] = Enemy.SPIKY_WINGLESS;
	        						str+="s";
	        					}
	        					break;
	        				case(Enemy.ENEMY_FLOWER):
	        					if(winged){
	        						//mapToUse[x][y] = Enemy.FLOWER_WINGED;
	        						str+="F";
	        					}
	        					else{
	        						//mapToUse[x][y] = Enemy.FLOWER_WINGLESS;
	        						str+="f";
	        					}
	        					break;
	        				default:
								str+="E";
								break;
	        				}
							
						}
						else{
							str+="E";
						}
					}
					else{
						if(level.getBlock(i, j)==0){
							if(i==level.getxExit()){
								str +="G";
							}
							else{
								str+="-";
							}
						}
						else{
							//check if solid
							if(Level.isCannon(level.getBlock(i, j))){
								str+="N";
							}
							else if(Level.isCoin(level.getBlock(i, j))){
								str+="c";
							}
							else if(Level.BLOCK_COIN==(int)level.getBlock(i, j)){
								str+="C";
							}
							else if(Level.isPowerup(level.getBlock(i, j))){
								str+="P";
							}
							else if(Level.isBreakable(level.getBlock(i, j))){
								str+="B";
							}
							else if(Level.isPipeBottom(level.getBlock(i, j))){
								str+="T";
							}
							else if(Level.isPipeTop(level.getBlock(i, j))){
								str+="t";
							}
							else if(Level.isSolid(level.getBlock(i, j))){
								str+="X";
							}
							else{
								str+="-";
							}
						}
					}
				}
				else{
					str+="-";
				}
			}
			str+="\n";
			
		}		
		return str;
	}
	/**
	public String GetScreenStringRepresentation(){
		StringBuffer buf = new StringBuffer();
		int marioX = 8;
		int marioY = -1;
		if(mario!=null){
			marioX = (int) mario.x/16;
			marioY = (int) mario.y/16;
		}
		int minX = 0;
		if (marioX-8>minX){
			minX = marioX-8;
		}		
		
		int levelHeight = level.getHeight()+1;
		for(int j = 0; j<levelHeight; j++){
			for(int i = minX; i<minX+16; i++){
				Sprite enemyHere = null;
				if(marioX==i && marioY==j ){
					buf.append("M"); // TODO: when Mario overlaps with enemy
				}
				else if(i<level.getWidth()){
					boolean enemyThere = false;
					for(int a = 0; a<sprites.size(); a++){
						if(mario!=sprites.get(a) && (SpriteTemplate.IsEnemy(sprites.get(a)) ||  sprites.get(a) instanceof BulletBill) ){
							int spriteX = (int)sprites.get(a).x/16;
							int spriteY = (int)sprites.get(a).y/16;
							if(spriteX==i && spriteY ==j){
								enemyThere = true;
								enemyHere = sprites.get(a);
								break;
							}
						}
					}
					
					if(enemyThere){
						
						SpriteTemplate template = enemyHere.spriteTemplate;
						
						//ENEMY TYPES
						if(enemyHere instanceof BulletBill){
							buf.append("D");
						}
						else if(template!=null){
							if (template.getType()==Enemy.SPIKY_WINGED || template.getType()==Enemy.SPIKY_WINGLESS){

								buf.append("F");
							}
							else{
								buf.append("E");
							}
						}
						else{
							buf.append("E");
						}
					}
					else{
						if(level.getBlock(i, j)==0){
							if(i==level.getxExit()){
								buf.append("g");
							}
							else{
								buf.append((char) 1);
							}
						}
						else{
							//check if solid
							if(Level.isCannon(level.getBlock(i, j))){
								buf.append("Y");
							}
							else if(Level.isCoin(level.getBlock(i, j))){
								buf.append("c");
							}
							else if(Level.BLOCK_COIN==(int)level.getBlock(i, j)){
								buf.append("d");
							}
							else if(Level.isPowerup(level.getBlock(i, j))){
								buf.append("e");
							}
							else if(Level.isBreakable(level.getBlock(i, j))){
								buf.append("W");//"B";
							}
							else if(Level.isPipeBottom(level.getBlock(i, j))){
								buf.append("X");//"T";
							}
							else if(Level.isPipeTop(level.getBlock(i, j))){
								buf.append("X");//"t";
							}
							else if(Level.isSolid(level.getBlock(i, j))){
								buf.append("X");
							}
							else{
								buf.append((char) 1);
							}
						}
					}
				}
				else{
					buf.append((char) 1);
				}
			}
			buf.append("\n");
			
		}		
		return buf.toString();
	}
	*/
	
	public String GetEmptyScreenStringRepresentation(){
		String str = "";
		for (int y = 0; y<16; y++){
			for (int x = 0; x<16; x++){
				str+="-";
			}
			str+="\n";
		}
		return str;
	}

	public int getWidth() {
		// TODO Auto-generated method stub
		return level.getWidth();
	}
	
	public int getHeight() {
		// TODO Auto-generated method stub
		return level.getHeight();
	}
	
	
	
	
	
}
