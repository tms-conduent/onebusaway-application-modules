package org.onebusaway.transit_data_federation.bundle.tasks.transfer_pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onebusaway.collections.tuple.Pair;
import org.onebusaway.collections.tuple.Tuples;
import org.onebusaway.transit_data_federation.services.transit_graph.StopEntry;

public class TransferPattern {

  private final Map<StopEntry, Set<Entry>> _stops = new HashMap<StopEntry, Set<Entry>>();

  private final Entry _root;

  public TransferPattern(StopEntry origin) {
    _root = new Entry(origin, true, null, 0);
  }

  public void addPath(List<Pair<StopEntry>> path) {

    if (path.isEmpty())
      return;

    Entry node = _root;

    for (Pair<StopEntry> segment : path) {
      StopEntry from = segment.getFirst();
      StopEntry to = segment.getSecond();

      if (node == _root) {
        if (from != _root.stop)
          throw new IllegalStateException();
      } else {
        node = node.extend(from);
      }

      node = node.extend(to);
    }

    StopEntry stop = node.stop;
    Set<Entry> nodes = _stops.get(stop);
    if (nodes == null) {
      nodes = new HashSet<Entry>();
      _stops.put(stop, nodes);
    }
    nodes.add(node);
  }

  public Set<StopEntry> getStops() {
    return _stops.keySet();
  }

  public List<List<Pair<StopEntry>>> getPathsForStop(StopEntry stop) {

    List<List<Pair<StopEntry>>> paths = new ArrayList<List<Pair<StopEntry>>>();
    Set<Entry> nodes = _stops.get(stop);

    for (Entry node : nodes) {
      List<Pair<StopEntry>> path = new ArrayList<Pair<StopEntry>>();
      paths.add(path);
      while (node != null) {
        Entry b = node;
        Entry a = node.parent;
        if (a == null)
          throw new IllegalStateException();
        node = a.parent;
        path.add(0, Tuples.pair(a.stop, b.stop));
      }
    }

    return paths;
  }

  private static class Entry {

    private final StopEntry stop;
    private final boolean transfer;
    private final Map<StopEntry, Entry> _children = new HashMap<StopEntry, TransferPattern.Entry>();
    private final Entry parent;
    private final int depth;

    public Entry(StopEntry stop, boolean transfer, Entry parent, int depth) {
      if (stop == null)
        throw new IllegalArgumentException();
      this.stop = stop;
      this.transfer = transfer;
      this.parent = parent;
      this.depth = depth;
    }

    public Entry extend(StopEntry from) {
      Entry node = _children.get(from);
      if (node == null) {
        node = new Entry(from, !this.transfer, this, depth + 1);
        _children.put(from, node);
      }
      return node;
    }

    @Override
    public String toString() {
      return stop.getId() + " " + transfer;
    }
  }

}
