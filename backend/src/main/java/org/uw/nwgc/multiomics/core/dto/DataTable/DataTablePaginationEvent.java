package org.uw.nwgc.multiomics.core.dto.DataTable;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DataTablePaginationEvent{

	@JsonProperty("pageCount")
	private int pageCount;

	@JsonProperty("sortOrder")
	private Object sortOrder;

	@JsonProperty("sortField")
	private Object sortField;

	@JsonProperty("originalEvent")
	private OriginalEvent originalEvent;

	@JsonProperty("filters")
	private Filters filters;

	@JsonProperty("page")
	private int page;

	@JsonProperty("rows")
	private int rows;

	@JsonProperty("first")
	private int first;

	@JsonProperty("multiSortMeta")
	private List<Object> multiSortMeta;

	public void setPageCount(int pageCount){
		this.pageCount = pageCount;
	}

	public int getPageCount(){
		return pageCount;
	}

	public void setSortOrder(Object sortOrder){
		this.sortOrder = sortOrder;
	}

	public Object getSortOrder(){
		return sortOrder;
	}

	public void setSortField(Object sortField){
		this.sortField = sortField;
	}

	public Object getSortField(){
		return sortField;
	}

	public void setOriginalEvent(OriginalEvent originalEvent){
		this.originalEvent = originalEvent;
	}

	public OriginalEvent getOriginalEvent(){
		return originalEvent;
	}

	public void setFilters(Filters filters){
		this.filters = filters;
	}

	public Filters getFilters(){
		return filters;
	}

	public void setPage(int page){
		this.page = page;
	}

	public int getPage(){
		return page;
	}

	public void setRows(int rows){
		this.rows = rows;
	}

	public int getRows(){
		return rows;
	}

	public void setFirst(int first){
		this.first = first;
	}

	public int getFirst(){
		return first;
	}

	public void setMultiSortMeta(List<Object> multiSortMeta){
		this.multiSortMeta = multiSortMeta;
	}

	public List<Object> getMultiSortMeta(){
		return multiSortMeta;
	}

	@Override
 	public String toString(){
		return 
			"DataTablePaginationEvent{" + 
			"pageCount = '" + pageCount + '\'' + 
			",sortOrder = '" + sortOrder + '\'' + 
			",sortField = '" + sortField + '\'' + 
			",originalEvent = '" + originalEvent + '\'' + 
			",filters = '" + filters + '\'' + 
			",page = '" + page + '\'' + 
			",rows = '" + rows + '\'' + 
			",first = '" + first + '\'' + 
			",multiSortMeta = '" + multiSortMeta + '\'' + 
			"}";
		}
}