package neu.lab.conflict.graph;

public class Record4MthdProb extends IRecord {
	private String tgtMthd;
	private Double prob;

	public Record4MthdProb(String mthd, Double prob) {
		this.tgtMthd = mthd;
		this.prob = prob;
	}

	public String getTgtMthd() {
		return tgtMthd;
	}

	public Double getProb() {
		return prob;
	}

	public void updateProb(Double prob2) {
		if (prob2 < this.prob) {
			this.prob = prob2;
		}
		// this.prob = this.prob + prob2;
	}

	@Override
	public IRecord clone() {
		return new Record4MthdProb(tgtMthd, prob);
	}

	@Override
	public String toString() {
		return "Record4MthdProb [tgtMthd=" + tgtMthd + ", prob=" + prob + "]";
	}

}
