package dk.itu.mario.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

//A Star Agent implementation, successfully gets through levels
public class AStarAgent extends Agent{
	private boolean[][] path;
	private int currPathIndex = 0;
	public int statesSearched;
	private int numEnemiesDeaths = 0;
	private int numGapDeaths = 0;
	private int numEnemiesKilled = 0;
	
	private final int max_size = 300;
	
	@Override public void resetAgent(){
		currPathIndex = 0;
	}
	
	@Override public void Train(LevelScene world) throws CloneNotSupportedException{		
		//A Star Algorithm my dude
		AStarSearchNodeComparator comparator = new AStarSearchNodeComparator();
		PriorityQueue<AStarSearchNode> openSet = new PriorityQueue<AStarSearchNode>(max_size+5,comparator);
		ArrayList<AStarSearchNode> closedSet = new ArrayList<AStarSearchNode>();
		LevelScene trainingWorld = world.GetClone();//World to actually do this training world
		
		//Initial Set Up
		boolean[][] allActions = AllPossibleActions();
		for(int a = 0; a<allActions.length; a++){
			openSet.add(new AStarSearchNode(allActions[a], trainingWorld, null));
		}
		
		boolean goalReached = false;
		AStarSearchNode goalNode = null;
		
		statesSearched = 0;
		while(!goalReached){
			AStarSearchNode currNode = openSet.poll();
			closedSet.add(currNode);
			statesSearched +=1;
			
			if(currNode.IsGoal()){
				goalReached = true;
				goalNode = currNode;
				break;
			}
			
			//Cheating
			if (openSet.size()>max_size){
				ArrayList<AStarSearchNode> toKeep = new ArrayList<AStarSearchNode>();
				for(int i = 0; i<(int)(max_size*0.2); i++){
					toKeep.add(openSet.poll());
				}
				openSet.clear();
				for(int i = 0; i<(int)(max_size*0.2); i++){
					openSet.add(toKeep.get(i));
				}
			}
			//Further cheat to speed things up
			if(closedSet.size()>max_size){
				ArrayList<AStarSearchNode> toKeep = new ArrayList<AStarSearchNode>();
				for(int i = closedSet.size()-1; i>closedSet.size()-(int)(max_size*0.25); i--){
					toKeep.add(closedSet.get(i));
				}
				closedSet.clear();
				for(int i = 0; i<toKeep.size(); i++){
					closedSet.add(toKeep.get(i));
				}
			}
			
			//Check for stomp
			if(currNode.GetMarioEvents().contains(Mario.STOMP)){
				numEnemiesKilled +=1;
			}
			//Remove this to speed things up
			System.out.println(currNode.GetStateRepresentation());
			System.out.println("Training Heuristic Score: "+currNode.GetGScore());
			
			//Generate the neighbors
			for(int a = 0; a<allActions.length; a++){
				AStarSearchNode neighbor = new AStarSearchNode(allActions[a], currNode.GetWorld(), currNode);
				//Make sure this is a novel state
				if(!closedSet.contains(neighbor) && (neighbor.GetWorld().mario.deathTime>0)){
					if(neighbor.IsEnemyDeath()){
						numEnemiesDeaths +=1;
					}
					else{
						numGapDeaths +=1;
					}
				}
				if(!closedSet.contains(neighbor) && !(neighbor.GetWorld().mario.deathTime>0)){
					
					//Check and make sure this is not in open state
					if(!openSet.contains(neighbor)){
						openSet.add(neighbor);
					}
				}
			}
		}
		
		//System.out.println("STATES EXPANDED: "+statesSearched);
		//System.out.println("ENEMY DEATHS: "+numEnemiesDeaths);
		//System.out.println("GAP DEATHS: "+numGapDeaths);
		//System.out.println("ENEMIES KILLED: "+numEnemiesKilled);
		//End Algorithm my man
		GetPath(goalNode);//set up path
		currPathIndex = 0;
	}
	
	@Override public boolean[] GetAction(LevelScene currentWorld){
		boolean[] actionToTake = GetRandomBooleanAction();
		if(path!=null){
			if(currPathIndex<path.length){
				actionToTake = path[currPathIndex];
				currPathIndex+=1;
			}
		}
		return actionToTake;
	}
	
	//Get Path From Goal Node
	private void GetPath(AStarSearchNode goalNode){
		ArrayList<AStarSearchNode> l = new ArrayList<AStarSearchNode>();
		
		AStarSearchNode currNode = goalNode;
		while(currNode!=null){
			l.add(currNode);
			currNode = currNode.GetParent();
		}
		
		Collections.reverse(l);
		
		path = new boolean[l.size()][];
		
		for(int i = 0; i<l.size(); i++){
			path[i] = l.get(i).GetAction();
		}
	}
	
	public boolean[][] GetPath(){
		return path;
	}
	
	public String GetPathString(){
		String toReturn = "";
   	 	
		for(int i = 0; i<path.length; i++){
			toReturn+= (path[i].toString());
		}
		
		return toReturn;
   	 
	}
	
	public int GetEnemiesDeaths(){
		return numEnemiesDeaths;
	}
	
	public int GetGapDeaths(){
		return numGapDeaths;
	}
	
	public int GetNumEnemiesKilled(){
		return numEnemiesKilled;
	}
}
