package dk.itu.mario.engine;

import java.util.HashMap;

public class Agent {

	public static final int KEY_LEFT = 0;
    public static final int KEY_RIGHT = 1;
    public static final int KEY_DOWN = 2;
    public static final int KEY_UP = 3;
    public static final int KEY_JUMP = 4;
    public static final int KEY_SPEED = 5;
    public static final int KEY_ENTER = 6;
	
    //Get the action to take given the current state
	public boolean[] GetAction(LevelScene currentWorld){
		boolean[] actions = new boolean[7];
		
		// base is a random agent
		for(int i = 0; i<actions.length; i++){
			actions[i] = Math.random()<0.5;
    		
    	}
		
		return actions;
	}
	
	//Train on this world
	public void Train(LevelScene world) throws CloneNotSupportedException{}
	
	
	//Go from a boolean to a char array action representation
	protected char[] BooleanToCharActions(boolean[] actions){
		char[] stringActions = new char[actions.length];
		
		for(int b = 0; b<actions.length; b++){
			if(actions[b]){
				stringActions[b]='1';
			}
			else{
				stringActions[b] = '0';
			}
		}
		
		return stringActions;
	}
	
	//Go from a char array to boolean actions
	protected boolean[] CharToBooleanActions(char[] actions){
		boolean[] booleanActions = new boolean[actions.length];
		
		for(int b = 0; b<actions.length; b++){
			if(actions[b] == '1'){
				booleanActions[b]=true;
			}
			else{
				booleanActions[b] = false;
			}
		}
		
		return booleanActions;
	}
		
	//Returns a random action in the boolean representation
	protected boolean[] GetRandomBooleanAction(){
		boolean[] actions = new boolean[7];
		
		for(int i = 0; i<actions.length; i++){
			actions[i] = Math.random()<0.5;
    	}
		
		return actions;
	}
	
	//Get string representation of action (for debugging)
	protected String GetActionDescription(boolean[] action){
		String toReturn = "";
		String[] meanings = new String[]{"Left", "Right", "Down", "Up", "Jump", "Speed", "Enter"};
		
		for(int a = 0; a<action.length; a++){
			if(action[a]){
				toReturn+=meanings[a]+"-";
			}
		}
		
		return toReturn;
	}
	
	//Get the list of all possible, legal actions
	public boolean[][] AllPossibleActions(){
		boolean[][] allActions = new boolean[10][];
		
		//Left
		boolean[] actions = new boolean[7];
		actions[0] = true;
		allActions[0] = actions;
		
		//Right
		actions = new boolean[7];
		actions[1] = true;
		allActions[1] = actions;
		
		//Down
		actions = new boolean[7];
		actions[2] = true;
		allActions[2] = actions;
		
		//Jump
		actions = new boolean[7];
		actions[4] = true;
		allActions[3] = actions;
		
		//Left Speed
		actions = new boolean[7];
		actions[0] = true;
		actions[5] = true;
		allActions[4] = actions;
		
		//Right Speed
		actions = new boolean[7];
		actions[1] = true;
		actions[5] = true;
		allActions[5] = actions;
		
		//Left Jump
		actions = new boolean[7];
		actions[0] = true;
		actions[4] = true;
		allActions[6] = actions;
				
		//Right Jump
		actions = new boolean[7];
		actions[1] = true;
		actions[4] = true;
		allActions[7] = actions;
		
		//Left Speed Jump
		actions = new boolean[7];
		actions[0] = true;
		actions[5] = true;
		actions[4] = true;
		allActions[8] = actions;
		
		//Right Speed Jump
		actions = new boolean[7];
		actions[1] = true;
		actions[5] = true;
		actions[4] = true;
		allActions[9] = actions;
		
		
		return allActions;
	}
	
	//Reset any variables used during play
	public void resetAgent(){}
	
	
	
}
