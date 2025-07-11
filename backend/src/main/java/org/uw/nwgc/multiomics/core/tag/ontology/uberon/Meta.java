package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Meta{

	@JsonProperty("basicPropertyValues")
	private List<BasicPropertyValuesItem> basicPropertyValues;

	@JsonProperty("version")
	private String version;

	@JsonProperty("xrefs")
	private List<XrefsItem> xrefs;

	@JsonProperty("definition")
	private Definition definition;

	@JsonProperty("comments")
	private List<String> comments;

	@JsonProperty("synonyms")
	private List<SynonymsItem> synonyms;

	@JsonProperty("subsets")
	private List<String> subsets;

	@JsonProperty("deprecated")
	private boolean deprecated;

	public void setBasicPropertyValues(List<BasicPropertyValuesItem> basicPropertyValues){
		this.basicPropertyValues = basicPropertyValues;
	}

	public List<BasicPropertyValuesItem> getBasicPropertyValues(){
		return basicPropertyValues;
	}

	public void setVersion(String version){
		this.version = version;
	}

	public String getVersion(){
		return version;
	}

	public void setXrefs(List<XrefsItem> xrefs){
		this.xrefs = xrefs;
	}

	public List<XrefsItem> getXrefs(){
		return xrefs;
	}

	public void setDefinition(Definition definition){
		this.definition = definition;
	}

	public Definition getDefinition(){
		return definition;
	}

	public void setComments(List<String> comments){
		this.comments = comments;
	}

	public List<String> getComments(){
		return comments;
	}

	public void setSynonyms(List<SynonymsItem> synonyms){
		this.synonyms = synonyms;
	}

	public List<SynonymsItem> getSynonyms(){
		return synonyms;
	}

	public void setSubsets(List<String> subsets){
		this.subsets = subsets;
	}

	public List<String> getSubsets(){
		return subsets;
	}

	public void setDeprecated(boolean deprecated){
		this.deprecated = deprecated;
	}

	public boolean isDeprecated(){
		return deprecated;
	}

	@Override
 	public String toString(){
		return 
			"Meta{" + 
			"basicPropertyValues = '" + basicPropertyValues + '\'' + 
			",version = '" + version + '\'' + 
			",xrefs = '" + xrefs + '\'' + 
			",definition = '" + definition + '\'' + 
			",comments = '" + comments + '\'' + 
			",synonyms = '" + synonyms + '\'' + 
			",subsets = '" + subsets + '\'' + 
			",deprecated = '" + deprecated + '\'' + 
			"}";
		}
}