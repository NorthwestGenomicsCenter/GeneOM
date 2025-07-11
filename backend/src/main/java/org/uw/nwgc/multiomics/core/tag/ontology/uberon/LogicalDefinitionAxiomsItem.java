package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LogicalDefinitionAxiomsItem{

	@JsonProperty("genusIds")
	private List<String> genusIds;

	@JsonProperty("restrictions")
	private List<RestrictionsItem> restrictions;

	@JsonProperty("definedClassId")
	private String definedClassId;

	public void setGenusIds(List<String> genusIds){
		this.genusIds = genusIds;
	}

	public List<String> getGenusIds(){
		return genusIds;
	}

	public void setRestrictions(List<RestrictionsItem> restrictions){
		this.restrictions = restrictions;
	}

	public List<RestrictionsItem> getRestrictions(){
		return restrictions;
	}

	public void setDefinedClassId(String definedClassId){
		this.definedClassId = definedClassId;
	}

	public String getDefinedClassId(){
		return definedClassId;
	}

	@Override
 	public String toString(){
		return 
			"LogicalDefinitionAxiomsItem{" + 
			"genusIds = '" + genusIds + '\'' + 
			",restrictions = '" + restrictions + '\'' + 
			",definedClassId = '" + definedClassId + '\'' + 
			"}";
		}
}