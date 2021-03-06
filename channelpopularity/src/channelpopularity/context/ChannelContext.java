package channelpopularity.context;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import channelpopularity.operation.Operation;
import channelpopularity.state.StateI;
import channelpopularity.state.StateName;
import channelpopularity.state.factory.SimpleStateFactoryI;
import channelpopularity.util.Results;

public class ChannelContext implements ContextI {

	double avgPopularityScore;
	protected StateI currentState;
	protected final Map<StateName, StateI> availableStates = new HashMap<>();
	Map<String, HashMap<String, Integer>> videoMap = new HashMap<>();

	public StateI getCurState() {
		return currentState;
	}

	@Override
	public void setCurrentState(StateName name) {
		this.currentState = availableStates.get(name);
	}

	public ChannelContext(SimpleStateFactoryI stateFactoryIn, StateName[] stateNames, Results res) {
		for (StateName state : stateNames)
			availableStates.put(state, stateFactoryIn.create(state, res, this));
		setCurrentState(StateName.UNPOPULAR);
	}

	/**
	 * Handles the Operations
	 * 
	 * @param str: HashMap of videonames and parameters(metrics and adv length)
	 * @param op:  Operations enum
	 *
	 */
	@Override
	public void operationHandler(Operation op, HashMap<String, ?> str) throws IOException {

		switch (op) {
		case ADD_VIDEO:
			currentState.addVideo(str);
			break;
		case AD_REQUEST:
			currentState.addRequest(str);
			break;
		case METRICS:
			currentState.metrics(str);
			break;
		case REMOVE_VIDEO:
			currentState.removeVideo(str);
			break;
		default:
			break;
		}
	}

	@Override
	public Map<String, HashMap<String, Integer>> getVideos() {
		return videoMap;

	}

	@Override
	public void setPopularityScore(double score) {
		avgPopularityScore = score;

	}

	@Override
	public double getPopularityScore() {
		return avgPopularityScore;
	}

}
