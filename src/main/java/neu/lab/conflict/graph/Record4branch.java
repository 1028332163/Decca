package neu.lab.conflict.graph;

public class Record4branch extends IRecord {
	private String name;
	private double branch;
	private double distance;

	public Record4branch(String name, double branch, double distance) {
		super();
		this.name = name;
		this.branch = branch;
		this.distance = distance;
	}

	public double getBranch() {
		return branch;
	}

	
	public double getDistance() {
		return distance;
	}

	public String getName() {
		return name;
	}

	@Override
	public IRecord clone() {
		return new Record4branch(name, branch, distance);
	}

	public void updateBranch(double branch2) {
		if (branch2 < branch) {
			branch = branch2;
		}

	}

	public void updateDistance(double distance2) {
		if (distance2 < distance) {
			distance = distance2;
		}
	}

}
