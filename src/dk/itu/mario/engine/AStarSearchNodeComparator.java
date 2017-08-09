package dk.itu.mario.engine;

import java.util.Comparator;

public class AStarSearchNodeComparator implements Comparator<AStarSearchNode>{

	@Override
	public int compare(AStarSearchNode o1, AStarSearchNode o2) {
		if(o1.GetGScore()>o2.GetGScore()){
			return 1;
		}
		else if(o1.GetGScore()<o2.GetGScore()){
			return -1;
		}
		
		return 0;
	}
	
}
