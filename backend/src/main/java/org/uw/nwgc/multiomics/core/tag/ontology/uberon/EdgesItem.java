package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EdgesItem{

	@JsonProperty("sub")
	private String sub;

	@JsonProperty("pred")
	private String pred;

	@JsonProperty("obj")
	private String obj;

	@JsonProperty("meta")
	private Meta meta;

	public void setSub(String sub){
		this.sub = sub;
	}

	public String getSub(){
		return sub;
	}

	public void setPred(String pred){
		this.pred = pred;
	}

	public String getPred(){
		return pred;
	}

	public void setObj(String obj){
		this.obj = obj;
	}

	public String getObj(){
		return obj;
	}

	public void setMeta(Meta meta){
		this.meta = meta;
	}

	public Meta getMeta(){
		return meta;
	}

	@Override
 	public String toString(){
		return 
			"EdgesItem{" + 
			"sub = '" + sub + '\'' + 
			",pred = '" + pred + '\'' + 
			",obj = '" + obj + '\'' + 
			",meta = '" + meta + '\'' + 
			"}";
		}
}