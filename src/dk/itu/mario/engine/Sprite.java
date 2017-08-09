package dk.itu.mario.engine;

import java.awt.Graphics;
import java.awt.Image;

public class Sprite 
{
    public static SpriteContext spriteContext;

    public float xOld, yOld, x, y, xa, ya;
    public LevelScene world;
    public int xPic, yPic;
    public int wPic = 32;
    public int hPic = 32;
    public int xPicO, yPicO;
    public boolean xFlipPic = false;
    public boolean yFlipPic = false;
    public Image[][] sheet;
    public boolean visible = true;

    public int layer = 1;

    public SpriteTemplate spriteTemplate;
    
    
    public void move()
    {
        x+=xa;
        y+=ya;
    }

    public void render(Graphics og, float alpha)
    {
        if (!visible) return;

        int xPixel = (int)(xOld+(x-xOld)*alpha)-xPicO;
        int yPixel = (int)(yOld+(y-yOld)*alpha)-yPicO;
        try{
        	og.drawImage(sheet[xPic][yPic], xPixel+(xFlipPic?wPic:0), yPixel+(yFlipPic?hPic:0), xFlipPic?-wPic:wPic, yFlipPic?-hPic:hPic, null);
        }
        catch(ArrayIndexOutOfBoundsException e){
        	
        }
    }
    
    public void specialRender(Graphics og, float alpha)
    {

        int xPixel = (int)(xOld+(x-xOld)*alpha)-xPicO;
        int yPixel = (int)(yOld+(y-yOld)*alpha)-yPicO;
        try{
        	og.drawImage(sheet[xPic][yPic], xPixel+(xFlipPic?wPic:0), yPixel+(yFlipPic?hPic:0), xFlipPic?-wPic:wPic, yFlipPic?-hPic:hPic, null);
        }
        catch(ArrayIndexOutOfBoundsException e){
        	
        }
    }


    public final void tick()
    {
        xOld = x;
        yOld = y;
        move();
    }

    public final void tickNoMove()
    {
        xOld = x;
        yOld = y;
    }

    public float getX(float alpha)
    {
        return (xOld+(x-xOld)*alpha)-xPicO;
    }

    public float getY(float alpha)
    {
        return (yOld+(y-yOld)*alpha)-yPicO;
    }

    public void collideCheck()
    {
    }

    public void bumpCheck(int xTile, int yTile)
    {
    }

    public boolean shellCollideCheck(Shell shell)
    {
        return false;
    }

    public void release(Mario mario)
    {
    }

    public boolean fireballCollideCheck(Fireball fireball)
    {
        return false;
    }
    
    @Override
   	public Object clone() throws CloneNotSupportedException
       {
       	Sprite s = new Sprite();
       	s.x = this.x;
       	s.y = this.y;
       	s.xOld = this.xOld;
       	s.yOld = this.yOld;
       	s.xPic = this.xPic;
       	s.yPic = this.yPic;
       	s.wPic = this.wPic;
       	s.hPic = this.hPic;
       	s.xPicO = this.xPicO;
       	s.yPicO = this.yPicO;
       	
       	s.xFlipPic = this.xFlipPic;
       	s.yFlipPic = this.yFlipPic;
       	
       	s.visible = this.visible;
       	if (spriteTemplate != null){
       		s.spriteTemplate = (SpriteTemplate) this.spriteTemplate.clone();
       		s.spriteTemplate.sprite =s;
       	}
       	return s;
       }
}
