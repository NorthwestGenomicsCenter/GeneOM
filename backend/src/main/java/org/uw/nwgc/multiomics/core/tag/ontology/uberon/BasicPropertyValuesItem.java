package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BasicPropertyValuesItem{

	@JsonProperty("val")
	private String val;

	@JsonProperty("pred")
	private String pred;

	public void setVal(String val){
		this.val = val;
	}

	public String getVal(){
		return val;
	}

	public void setPred(String pred){
		this.pred = pred;
	}

	public String getPred(){
		return pred;
	}

	@Override
 	public String toString(){
		return 
			"BasicPropertyValuesItem{" + 
			"val = '" + val + '\'' + 
			",pred = '" + pred + '\'' + 
			"}";
		}
}