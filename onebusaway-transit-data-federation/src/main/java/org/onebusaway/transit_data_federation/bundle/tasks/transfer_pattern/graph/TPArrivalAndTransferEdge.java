package org.onebusaway.transit_data_federation.bundle.tasks.transfer_pattern.graph;

import org.onebusaway.transit_data_federation.impl.otp.GraphContext;
import org.onebusaway.transit_data_federation.impl.otp.graph.AbstractEdge;
import org.onebusaway.transit_data_federation.impl.otp.graph.EdgeNarrativeImpl;
import org.onebusaway.transit_data_federation.services.tripplanner.StopTimeInstance;
import org.onebusaway.transit_data_federation.services.tripplanner.StopTransfer;
import org.opentripplanner.routing.algorithm.NegativeWeightException;
import org.opentripplanner.routing.core.State;
import org.opentripplanner.routing.core.StateData;
import org.opentripplanner.routing.core.TraverseOptions;
import org.opentripplanner.routing.core.TraverseResult;

public class TPArrivalAndTransferEdge extends AbstractEdge {

  private StopTimeInstance _instance;

  private StopTransfer _transfer;

  public TPArrivalAndTransferEdge(GraphContext context,
      StopTimeInstance instance, StopTransfer transfer) {
    super(context);
    _instance = instance;
    _transfer = transfer;
  }

  @Override
  public TraverseResult traverse(State s0, TraverseOptions options)
      throws NegativeWeightException {

    /**
     * Check if we've reached our transfer limit
     */
    StateData data = s0.getData();
    if (data.getNumBoardings() >= options.maxTransfers)
      return null;

    int transferTime = computeTransferTime(options);

    State s1 = s0.incrementTimeInSeconds(transferTime + options.minTransferTime);

    /**
     * We're using options.boardCost as a transfer penalty
     */
    double weight = transferTime * options.walkReluctance + options.boardCost
        + options.minTransferTime * options.waitReluctance;

    EdgeNarrativeImpl narrative = createNarrative(s1.getTime());
    return new TraverseResult(weight, s1, narrative);
  }

  @Override
  public TraverseResult traverseBack(State s0, TraverseOptions options)
      throws NegativeWeightException {

    int transferTime = computeTransferTime(options);

    State s1 = s0.setTime(_instance.getArrivalTime());

    double weight = transferTime * options.walkReluctance + options.boardCost
        + options.minTransferTime * options.waitReluctance;

    EdgeNarrativeImpl narrative = createNarrative(s0.getTime());
    return new TraverseResult(weight, s1, narrative);
  }

  /****
   * Private Methods
   ****/

  private EdgeNarrativeImpl createNarrative(long time) {
    TPBlockArrivalVertex fromVertex = new TPBlockArrivalVertex(_context,
        _instance);
    TPDepartureVertex toVertex = new TPDepartureVertex(_context,
        _transfer.getStop());
    return new EdgeNarrativeImpl(fromVertex, toVertex);
  }

  private int computeTransferTime(TraverseOptions options) {

    int transferTime = _transfer.getMinTransferTime();
    if (transferTime > 0)
      return transferTime;

    double walkingVelocity = options.speed;
    double distance = _transfer.getDistance();

    // time to walk = meters / (meters/sec) = sec
    int t = (int) (distance / walkingVelocity);

    // transfer time = time to walk + min transfer buffer time
    return t;
  }
}
