package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Definition{

	@JsonProperty("val")
	private String val;

	@JsonProperty("xrefs")
	private List<String> xrefs;

	public void setVal(String val){
		this.val = val;
	}

	public String getVal(){
		return val;
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
			"Definition{" + 
			"val = '" + val + '\'' + 
			",xrefs = '" + xrefs + '\'' + 
			"}";
		}
}