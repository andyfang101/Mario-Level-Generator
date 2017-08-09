package dk.itu.mario.engine;

public class AStarSearchNode{
	private LevelScene world;
	private float fScore;//Score from parent
	private float hScore;//Heuristic score
	private boolean[] actionFromParent;
	private AStarSearchNode parent;
	private String marioEvents;
	
	//private final float enemyDiscount = 200000;
	private final float actionScore = 1;
	
	public AStarSearchNode(boolean[] _action, LevelScene _parentWorld, AStarSearchNode _parent){
		this.parent = _parent;
		this.actionFromParent = _action;
		
		//Create my world
		try {
			LevelScene cloneWorld = _parentWorld.GetClone();
			cloneWorld.mario.event = "";
			cloneWorld.mario.currKeys = actionFromParent;
			cloneWorld.tick();//TODO;May need to update this to be more than one tick per action
			cloneWorld.mario.currKeys = null;
			marioEvents = cloneWorld.mario.event;
			this.world = cloneWorld;
		} catch (CloneNotSupportedException e) {
			this.world = _parentWorld;
		}
		
		//FScore Calculate
		float parentScore = 0;
		if(parent!=null){
			parentScore = parent.GetFScore();
		}
		fScore =parentScore +actionScore;
		
		//Heuristic Score Calculate
		float marioX = this.world.mario.x;
		hScore = (this.world.level.getxExit()-marioX)*2;//*(this.world.level.getxExit()*16-marioX);
		//hScore/=2;//small discount on heuristic
		hScore+= (this.world.mario.y)/ 4.0;//Discount being lower
		
		
		
		if(this.world.mario.deathTime>0){
			hScore+=99999999;
		}
		//TEST: Always want to be higher
		//hScore+= this.world.mario.y;
	}
	
	public boolean[] GetAction(){
		return actionFromParent;
	}
	
	public AStarSearchNode GetParent(){
		return parent;
	}
	
	public LevelScene GetWorld(){
		return world;
	}
	
	public String GetStateRepresentation(){
		return world.GetScreenStringRepresentationGranular();
	}
	
	public float GetFScore(){
		return fScore;
	}
	
	public float GetGScore(){
		return fScore+hScore;
	}
	
	public boolean IsGoal(){
		return this.world.level.getxExit()<=((int)this.world.mario.x/16);
	}
	
	public String GetMarioEvents(){
		return marioEvents;
	}
	
	public boolean IsEnemyDeath(){
		return world.mario.enemyDeath;
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) {
	        return false;
	    }
	    if (!AStarSearchNode.class.isAssignableFrom(obj.getClass())) {
	        return false;
	    }
	    final AStarSearchNode other = (AStarSearchNode) obj;
	    String currState = this.world.GetScreenStringRepresentationGranular();
	    String otherState = other.GetStateRepresentation();
	    if (!currState.equals(otherState)) {
	        return false;
	    }
	    //if (actionFromParent.equals(other.GetAction())) {
	    //    return false;
	    //}
	    if ((int)this.GetWorld().mario.xa!=(int)other.GetWorld().mario.xa){//actionFromParent.equals(other.GetAction())) {
	        return false;
	    }
	    if ((int)this.GetWorld().mario.ya!=(int)other.GetWorld().mario.ya){//actionFromParent.equals(other.GetAction())) {
	        return false;
	    }
	    if ((int)this.GetWorld().mario.x!=(int)other.GetWorld().mario.x){//actionFromParent.equals(other.GetAction())) {
	        return false;
	    }
	    if ((int)this.GetWorld().mario.y!=(int)other.GetWorld().mario.y){//actionFromParent.equals(other.GetAction())) {
	        return false;
	    }
	    if ((int)this.GetWorld().timeLeft!=(int)other.GetWorld().timeLeft){//actionFromParent.equals(other.GetAction())) {
	        return false;
	    }
	    return true;
	}

	@Override
	public int hashCode() {
	    int hash = 3;
	    hash = 53 * hash + (this.GetStateRepresentation().hashCode());
	    //hash = 53 * hash + (this.GetAction().hashCode());
	    //hash = 53 * hash + ( new Float(this.GetWorld().mario.xa).hashCode());
	    //hash = 53 * hash + ( new Float(this.GetWorld().mario.ya).hashCode());
	    hash = 53 * hash + ( new Integer((int)(this.GetWorld().mario.xa)).hashCode());
	    hash = 53 * hash + ( new Integer((int)(this.GetWorld().mario.ya)).hashCode());
	    hash = 53 * hash + ( new Integer((int)(this.GetWorld().mario.x)).hashCode());
	    hash = 53 * hash + ( new Integer((int)(this.GetWorld().mario.y)).hashCode());
	    hash = 53 * hash + ( new Integer((int)(this.GetWorld().timeLeft)).hashCode());
	    return hash;
	}
}


