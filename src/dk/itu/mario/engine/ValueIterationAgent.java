package dk.itu.mario.engine;

import java.util.*;

public class ValueIterationAgent  extends Agent{
	
	//Constants 
	final int MAX_LEVEL_TIME = 200*15;//Max time to get through a level (DO NOT ALTER)
	final int MAX_ITERATIONS = 10000;//Max number of restarts allowed (OPTIONAL TO ALTER)
	final float EPSILON = 0.95f;//Chance of taking non-policy action (OPTIONAL TO ALTER)
	
	//Non-constant variables (feel free to add to these)
	protected HashMap<String, Float> stateValues;//Value table
	protected HashMap<String, String> policy;//Policy
	private boolean marioWon = false;//has mario won yet the current training level yet
	private boolean marioDied = false;//has mario died in the current training level
	private HashMap<String, Float> prevStateValues;
	
	
	
	public ValueIterationAgent(){
		stateValues = new HashMap<String, Float>();//State representation to float
		policy = new HashMap<String,String>();//State to action
		prevStateValues =  new HashMap<String, Float>(); 
	}
	
	//Get action for current state (used during testing)
	@Override public boolean[] GetAction(LevelScene currentWorld){
		String currState = GetStateRepresentation(currentWorld);
		
		//Try to get action based on policy
		if(policy.containsKey(currState)){
			String maxString = policy.get(currState);
		
			boolean[] bs = CharToBooleanActions(maxString.toCharArray());
			return bs;
			
		}
		else{
			//If we don't have an action, return a random one
			return GetRandomBooleanAction();
		}
	
	}
	
	//Get action for current state (used during training)
	private boolean[] GetPolicyAction(String currState, LevelScene trainingWorld){
		//If policy contains current state, return best action according to policy
		if(policy.containsKey(currState)){
			String maxString = policy.get(currState);
			
			boolean[] bs = CharToBooleanActions(maxString.toCharArray());
			return bs;
		}
		else{
			//Not in policy, instantiate from best in Q table
			boolean[][] allActions = AllPossibleActions();
			float maxReward = Float.NEGATIVE_INFINITY;
			boolean[] bestAction = new boolean[0];
			for(int a = 0; a<allActions.length; a++){
				AStarSearchNode neighbor = new AStarSearchNode(allActions[a], trainingWorld, null);
				
				float currReward = RewardFunction(neighbor.GetWorld());//GetValueOfState( GetStateRepresentation(neighbor.GetWorld()));
				if(currReward>maxReward){
					maxReward = currReward;
					bestAction = allActions[a];
				}
			}
			policy.put(currState, new String(BooleanToCharActions(bestAction)) );
			return bestAction;
			
		}
	}
	 
	//Return the value of a given state (OPTIONAL TO ALTER)
	private float GetValueOfState(String currState){
		if(stateValues.containsKey(currState)){
			return stateValues.get(currState);
		}
		return -1;//Return some default
	}
	
	//Get action to take during training
	private boolean[] GetValueIterationAction(String currState, LevelScene world){
		Random r = new Random();
		
		if(r.nextFloat()>=EPSILON){
			return GetRandomBooleanAction();
		}
		else{
			return GetPolicyAction(currState, world);
		}
	}
	
	//Reward function (YOU MUST ALTER THIS)
	private float RewardFunction(LevelScene trainingWorld){
		float thisValue = 0.0f;
		if(trainingWorld.mario.event.length()>0){
			switch(trainingWorld.mario.event){
			case Mario.WON://When mario reaches the end of the level
				thisValue+= 0;
				marioWon = true;
				break;
			case Mario.DIED://When mario dies/loses
				thisValue+= 0;
				marioDied = true;
				break;
			case Mario.COIN://When mario gets a coin
				thisValue+= 0;
				break;
			case Mario.POWERUP://When mario gets a powerup
				thisValue+= 0;
				break;
			case Mario.STOMP://When mario kills an enemy
				thisValue+= 0;
				break;
			case Mario.HURT://When mario is hurt
				thisValue+= 0;
				break;
			}
		}
		trainingWorld.mario.event = "";//Required to reset event buffer
		double movementCost =0;
		thisValue-= movementCost;
		return thisValue;
	}
	
	//OPTIONAL TO ALTER
	private boolean DoneTraining(){
		return marioWon;
	}
	
	//Main training function (YOU MUST ALTER THIS)
	@Override public void Train(LevelScene world) throws CloneNotSupportedException{
		int iterations = 0;
		
		marioWon = false;
		while(iterations< MAX_ITERATIONS && !DoneTraining()){
			iterations++;
			//Create the world for this run
			LevelScene trainingWorld = world.GetClone();
			
			int timeStep = 0;
			marioDied = false;
			trainingWorld.timeLeft = MAX_LEVEL_TIME;
			prevStateValues.putAll(stateValues);
			while(timeStep<MAX_LEVEL_TIME+1    && (!marioDied || !marioWon)){
				timeStep+=1;
				String prevState = GetStateRepresentation(trainingWorld);
				
				//Print out current world
				//System.out.println(""+iterations+":"+timeStep);
				//System.out.println(trainingWorld.GetScreenStringRepresentationGranular());
				
				//Mario take the action
				boolean[] actionToTake = GetValueIterationAction(prevState, trainingWorld);
				trainingWorld.mario.currKeys = actionToTake;
				trainingWorld.tick();
				
				String currState = GetStateRepresentation(trainingWorld);
				float reward = RewardFunction(trainingWorld);
								
				//Update Values
				float currentPrevStateValue = GetValueOfState(prevState);
				float currStateValue = GetValueOfState(currState);
				float newPrevStateValue =currStateValue;//(YOU MUST ALTER THIS LINE)
				
				//check if our policy has changed
				if(newPrevStateValue>currentPrevStateValue){
					//Alter it if so
					String stringActionToTake = new String(BooleanToCharActions(actionToTake));
					policy.put(prevState, stringActionToTake);
				}
				stateValues.put(prevState, newPrevStateValue);
				
			}
			
			
		}
		
	}
	
	//Return the current state representation given the currentWorld (YOU MUST ALTER THIS)
	public String GetStateRepresentation(LevelScene currentWorld){
		String currState = currentWorld.GetScreenStringRepresentationGranular();
		return currState;
	}
}
