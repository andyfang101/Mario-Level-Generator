package dk.itu.mario.engine;

import java.util.Random;

public class TestAgent {
	//CHANGE ME TO FALSE
	static boolean aStar = true;
	
	public static void main(String[] args)
    {
    	//levelNames 
		String levelName = "102";
		if (args.length>0){
			levelName = args[0];
		}
		System.out.println(levelName);
		TrainAndRun(levelName);		
	}
	
	
	public static void TrainAndRun(String levelName){
		//Load assets
        Art.init();
        //Create world
        LevelSceneTest randomLevel = new LevelSceneTest(new Random().nextLong(),0,0,true);
    	if (randomLevel.mario!=null){
	    	randomLevel.mario.fire = false;
	    	randomLevel.mario.large = false;
	    	randomLevel.mario.coins = 0;
	    	randomLevel.mario.lives = 3;
    	}
    	randomLevel.init();
    	LevelSceneTest scene = randomLevel;
    	
    	//Load level
    	LabeledLevel labeledLevel = new LabeledLevel(levelName);
    	Level currentLevel = labeledLevel.getLevel();
		scene.level = currentLevel;
		
		//Set up agent
		Agent currAgent;
		if (aStar){
			currAgent = new AStarAgent();
		}
		else{
			currAgent = new ValueIterationAgent();
		}
		//Run agent
    	 try {
				currAgent.Train(scene);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	}
}
