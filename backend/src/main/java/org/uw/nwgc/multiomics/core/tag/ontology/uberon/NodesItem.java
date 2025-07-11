package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NodesItem{

	@JsonProperty("propertyType")
	private String propertyType;

	@JsonProperty("id")
	private String id;

	@JsonProperty("type")
	private String type;

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("lbl")
	private String lbl;

	public void setPropertyType(String propertyType){
		this.propertyType = propertyType;
	}

	public String getPropertyType(){
		return propertyType;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return type;
	}

	public void setMeta(Meta meta){
		this.meta = meta;
	}

	public Meta getMeta(){
		return meta;
	}

	public void setLbl(String lbl){
		this.lbl = lbl;
	}

	public String getLbl(){
		return lbl;
	}

	@Override
 	public String toString(){
		return 
			"NodesItem{" + 
			"propertyType = '" + propertyType + '\'' + 
			",id = '" + id + '\'' + 
			",type = '" + type + '\'' + 
			",meta = '" + meta + '\'' + 
			",lbl = '" + lbl + '\'' + 
			"}";
		}
}