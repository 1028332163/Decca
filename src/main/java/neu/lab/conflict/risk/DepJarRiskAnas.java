package neu.lab.conflict.risk;

import java.util.HashMap;
import java.util.Map;

import neu.lab.conflict.vo.DepJar;

public class DepJarRiskAnas {
	private Map<DepJar, DepJarRiskAna> jar2cg;
	public DepJarRiskAnas() {
		jar2cg = new HashMap<DepJar, DepJarRiskAna>();
	}
	public DepJarRiskAna getDepJarCg(DepJar depJar) {
		DepJarRiskAna riskAna =  jar2cg.get(depJar);
		if(null==riskAna) {
			riskAna = new DepJarRiskAna(depJar,null);
			jar2cg.put(depJar, riskAna);
		}
		return riskAna;
	}
}
