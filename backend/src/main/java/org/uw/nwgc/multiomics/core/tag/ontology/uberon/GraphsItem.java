package org.uw.nwgc.multiomics.core.tag.ontology.uberon;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class GraphsItem{

	@JsonProperty("nodes")
	private List<NodesItem> nodes;

	@JsonProperty("meta")
	private Meta meta;

	@JsonProperty("edges")
	private List<EdgesItem> edges;

	@JsonProperty("domainRangeAxioms")
	private List<DomainRangeAxiomsItem> domainRangeAxioms;

	@JsonProperty("logicalDefinitionAxioms")
	private List<LogicalDefinitionAxiomsItem> logicalDefinitionAxioms;

	@JsonProperty("id")
	private String id;

	@JsonProperty("propertyChainAxioms")
	private List<PropertyChainAxiomsItem> propertyChainAxioms;

	public void setNodes(List<NodesItem> nodes){
		this.nodes = nodes;
	}

	public List<NodesItem> getNodes(){
		return nodes;
	}

	public void setMeta(Meta meta){
		this.meta = meta;
	}

	public Meta getMeta(){
		return meta;
	}

	public void setEdges(List<EdgesItem> edges){
		this.edges = edges;
	}

	public List<EdgesItem> getEdges(){
		return edges;
	}

	public void setDomainRangeAxioms(List<DomainRangeAxiomsItem> domainRangeAxioms){
		this.domainRangeAxioms = domainRangeAxioms;
	}

	public List<DomainRangeAxiomsItem> getDomainRangeAxioms(){
		return domainRangeAxioms;
	}

	public void setLogicalDefinitionAxioms(List<LogicalDefinitionAxiomsItem> logicalDefinitionAxioms){
		this.logicalDefinitionAxioms = logicalDefinitionAxioms;
	}

	public List<LogicalDefinitionAxiomsItem> getLogicalDefinitionAxioms(){
		return logicalDefinitionAxioms;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setPropertyChainAxioms(List<PropertyChainAxiomsItem> propertyChainAxioms){
		this.propertyChainAxioms = propertyChainAxioms;
	}

	public List<PropertyChainAxiomsItem> getPropertyChainAxioms(){
		return propertyChainAxioms;
	}

	@Override
 	public String toString(){
		return 
			"GraphsItem{" + 
			"nodes = '" + nodes + '\'' + 
			",meta = '" + meta + '\'' + 
			",edges = '" + edges + '\'' + 
			",domainRangeAxioms = '" + domainRangeAxioms + '\'' + 
			",logicalDefinitionAxioms = '" + logicalDefinitionAxioms + '\'' + 
			",id = '" + id + '\'' + 
			",propertyChainAxioms = '" + propertyChainAxioms + '\'' + 
			"}";
		}
}