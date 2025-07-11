package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SynonymsItem{

	@JsonProperty("val")
	private String val;

	@JsonProperty("pred")
	private String pred;

	@JsonProperty("synonymType")
	private String synonymType;

	@JsonProperty("xrefs")
	private List<String> xrefs;

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

	public void setSynonymType(String synonymType){
		this.synonymType = synonymType;
	}

	public String getSynonymType(){
		return synonymType;
	}

	public void setXrefs(List<String> xrefs){
		this.xrefs = xrefs;
	}

	public List<String> getXrefs(){
		return xrefs;
	}

	@Override
 	public String toString(){
		return 
			"SynonymsItem{" + 
			"val = '" + val + '\'' + 
			",pred = '" + pred + '\'' + 
			",synonymType = '" + synonymType + '\'' + 
			",xrefs = '" + xrefs + '\'' + 
			"}";
		}
}