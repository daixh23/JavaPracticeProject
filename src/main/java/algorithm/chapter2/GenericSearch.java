package algorithm.chapter2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class GenericSearch {

    public static class Node<T> implements Comparable<Node<T>> {
		final T state;
		Node<T> parent;
		double cost;
		double heuristic; // 启发式的，探索式的

		// for dfs and bfs we won't use cost and heuristic
		Node(T state, Node<T> parent) {
			this.state = state;
			this.parent = parent;
		}

		// for astar we will use cost and heuristic
		Node(T state, Node<T> parent, double cost, double heuristic) {
			this.state = state;
			this.parent = parent;
			this.cost = cost;
			this.heuristic = heuristic;
		}

		@Override
		public int compareTo(Node<T> other) {
			Double mine = cost + heuristic;
			Double theirs = other.cost + other.heuristic;
			return mine.compareTo(theirs);
		}
	}


    // 广度优先搜索算法
    public static <T> Node<T> bfs(T initial, Predicate<T> goalTest,
			Function<T, List<T>> successors) {
		// frontier is where we've yet to go
		Queue<Node<T>> frontier = new LinkedList<>();
		frontier.offer(new Node<>(initial, null));
		// explored is where we've been
		Set<T> explored = new HashSet<>();
		explored.add(initial);

		// keep going while there is more to explore
		while (!frontier.isEmpty()) {
			Node<T> currentNode = frontier.poll();
			T currentState = currentNode.state;
			// if we found the goal, we're done
			if (goalTest.test(currentState)) {
				return currentNode;
			}
			// check where we can go next and haven't explored
			for (T child : successors.apply(currentState)) {
				if (explored.contains(child)) {
					continue; // skip children we already explored
				}
				explored.add(child);
				frontier.offer(new Node<>(child, currentNode));
			}
		}
		return null; // went through everything and never found goal
	}

    public static <T> List<T> nodeToPath(Node<T> node) {
        List<T> path = new ArrayList();
        path.add(node.state);

        // work backwards from end to front
        while (node.parent != null) {
            node = node.parent;
            path.add(0, node.state); // add to front
        }

        return path;
    }

}