package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class XrefsItem{

	@JsonProperty("val")
	private String val;

	public void setVal(String val){
		this.val = val;
	}

	public String getVal(){
		return val;
	}

	@Override
 	public String toString(){
		return 
			"XrefsItem{" + 
			"val = '" + val + '\'' + 
			"}";
		}
}