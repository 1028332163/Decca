package neu.lab.conflict.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import neu.lab.conflict.util.MavenUtil;

public class Dog {
	public static long runtime = 0;
	private IGraph graph;
	protected String pos;
	protected List<String> route;

	protected Map<String, Cross> graphMap = new HashMap<String, Cross>();

	protected Map<String, List<String>> circleMap = new HashMap<String, List<String>>();

	protected Map<String, IBook> books = new HashMap<String, IBook>();

	protected Map<String, IBook> tempBooks = new HashMap<String, IBook>();

	public Dog(IGraph graph) {
		this.graph = graph;
	}

	protected IBook buyNodeBook(String nodeName) {
		return graph.getNode(nodeName).getBook();
	}

	public Map<String, IBook> findRlt(Collection<String> entrys,int maxDep) {
		MavenUtil.i().getLog().info("dog starts running...");
		long start = System.currentTimeMillis();
		for (String mthd : entrys) {
			route = new ArrayList<String>();
			if (books.containsKey(mthd))
				continue;
			else {
				forward(mthd);
				while (pos != null) {
					if (needChildBook(maxDep)) {
						String frontNode = graphMap.get(pos).getBranch();
						getChildBook(frontNode);
					} else {
						back();
					}
				}
			}
		}
		long runtime = (System.currentTimeMillis() - start) / 1000;
		MavenUtil.i().getLog().info("dog finishes running.");
		MavenUtil.i().getLog().info("dog run time:" + runtime);
		Dog.runtime = Dog.runtime + runtime;
		return this.books;
	}

	public boolean needChildBook(int maxDep) {
		return graphMap.get(pos).hasBranch() && route.size() < maxDep;
		// return graphMap.get(pos).hasBranch();
	}

	private void getChildBook(String frontNode) {
		if (books.containsKey(frontNode)) {
			addChildBookInfo(frontNode, pos);
		} else {
			forward(frontNode);
		}

	}

	/**
	 * frontNode是一个手册没有完成的节点，需要为这个节点建立手册
	 * 
	 * @param frontNode
	 */
	private void forward(String frontNode) {
		// System.out.println("forward to " + frontNode);
		INode node = graph.getNode(frontNode);
		if (node != null) {
			if (!route.contains(frontNode)) {
				pos = frontNode;
				route.add(pos);
				IBook nodeRch = buyNodeBook(frontNode);
				this.tempBooks.put(frontNode, nodeRch);
				graphMap.put(pos, new Cross(node));
			} else {
				List<String> circle = new ArrayList<String>();
				int index = route.indexOf(frontNode) + 1;
				while (index < route.size()) {
					circle.add(route.get(index));
					index++;
				}
				this.circleMap.put(frontNode, circle);
			}
		}
	}

	private void back() {
		String donePos = route.get(route.size() - 1);
		// System.out.println("back from " + donePos);
		graphMap.remove(donePos);

		IBook book = this.tempBooks.get(donePos);
		book.afterAddAllChildren();

		this.tempBooks.remove(donePos);
		this.books.put(donePos, book);

		if (circleMap.containsKey(donePos)) {

			dealLoopNd(donePos);
			circleMap.remove(donePos);
		}

		route.remove(route.size() - 1);

		if (route.size() == 0) {
			pos = null;
		} else {
			pos = route.get(route.size() - 1);
			addChildBookInfo(donePos, pos);
		}
	}

	private void addChildBookInfo(String donePos, String pos) {
		IBook doneBook = this.books.get(donePos);
		IBook doingBook = this.tempBooks.get(pos);
		doingBook.addChild(doneBook);
	}

	protected void dealLoopNd(String donePos) {
		// TODO
	}

}
