package channelpopularity.state;

import java.util.HashMap;
import java.util.Map;

import channelpopularity.context.ContextI;
import channelpopularity.operation.OperationArgs;
import channelpopularity.util.Results;

public abstract class AbstractState implements StateI {

	protected Results results;
	protected ContextI con;
	protected StateName st;

	public AbstractState(Results results, ContextI con, StateName st) {
		this.results = results;
		this.con = con;
		this.st = st;
	}

	protected void calculatePopularityScore()  {
		Map<String, HashMap<String, Integer>> allVideos = con.getVideos();
		int views = 0, likes = 0, dislikes = 0, noOfVideos = 0;

		for (HashMap<String, Integer> it : allVideos.values()) {
			noOfVideos++;
			views = views + it.getOrDefault(OperationArgs.VIEWS.name(), 0);
			likes = likes + it.getOrDefault(OperationArgs.LIKES.name(), 0);
			dislikes = dislikes + it.getOrDefault(OperationArgs.DISLIKES.name(), 0);
		}
		if(noOfVideos!=0)
		con.setPopularityScore((views + 2 * (likes - dislikes)) / noOfVideos);
		else
			con.setPopularityScore(views + 2 * (likes - dislikes)) ;
			
		
	}

	public StateName changeState() {
		double newPopularityScore = con.getPopularityScore();
		if (newPopularityScore <= 1000)
			return StateName.UNPOPULAR;
		if (newPopularityScore <= 10000 && newPopularityScore > 1000)
			return StateName.MILDLY_POPULAR;
		if (newPopularityScore <= 100000 && newPopularityScore > 10000)
			return StateName.HIGHLY_POPULAR;
		if (newPopularityScore <= Integer.MAX_VALUE && newPopularityScore > 100000)
			return StateName.ULTRA_POPULAR;
		throw new RuntimeException("Invalid State");
	}

	public void addVideo(HashMap<String, ?> str) {
		con.getVideos().put((String) str.get(OperationArgs.VIDEONAME.toString()), new HashMap<String, Integer>());
		calculatePopularityScore();
		StateName s=this.changeState();
		System.out.println(s+OperationArgs.__VIDEO_ADDED.name()+"::"+str.get(OperationArgs.VIDEONAME.name()));
	}

	public void removeVideo(HashMap<String, ?> str) {
		con.getVideos().remove((String) str.get(OperationArgs.VIDEONAME.toString()));
		calculatePopularityScore();
		StateName s=this.changeState();
		System.out.println(s+OperationArgs.__VIDEO_REMOVED.name()+"::"+str.get(OperationArgs.VIDEONAME.name()));
	}

	public void metrics(HashMap<String, ?> str) {
		int views = (int) str.get(OperationArgs.VIEWS.toString());
		int likes = (int) str.get(OperationArgs.LIKES.toString());
		int dislikes = (int) str.get(OperationArgs.DISLIKES.toString());

		Map<String, Integer> metrics = con.getVideos().get(str.get(OperationArgs.VIDEONAME.name()));
		metrics.put(OperationArgs.VIEWS.name(), views + metrics.getOrDefault(OperationArgs.VIEWS.name(), 0));
		metrics.put(OperationArgs.LIKES.name(), likes + metrics.getOrDefault(OperationArgs.LIKES.name(), 0));
		metrics.put(OperationArgs.DISLIKES.name(), dislikes + metrics.getOrDefault(OperationArgs.DISLIKES.name(), 0));
		
		calculatePopularityScore();
		StateName s=this.changeState();
		System.out.println(s+OperationArgs.__POPULARITY_SCORE_UPDATE.name()+"::"+con.getPopularityScore());
	}

}
