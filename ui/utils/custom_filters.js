import {FilterService} from "@primevue/core";

function removeAccents(str) {
	if (str) {
		str = str.normalize('NFKD').replace(/\p{Diacritic}/gu, '');
	}
	
	return str;
}

const trimContains = function trimContains(value, filter, filterLocale) {
	if (filter === undefined || filter === null || filter === '') {
		return true;
	}
	
	if (value === undefined || value === null) {
		return false;
	}
	
	let filterValue = removeAccents(filter.toString()).toLocaleLowerCase(filterLocale).trim();
	let stringValue = removeAccents(value.toString()).toLocaleLowerCase(filterLocale).trim();
	
	return stringValue.indexOf(filterValue) !== -1;
}

const listContains = function listContains(value, filter, filterLocale) {
	if (filter === undefined || filter === null || filter === '') {
		return true;
	}
	
	if (value === undefined || value === null) {
		return false;
	}
	
	let filterValues = []
	for (let filterText in value) {
		filterValues.push(removeAccents(filterText.toString()).toLocaleLowerCase(filterLocale).trim())
	}
	let stringValue = removeAccents(value.toString()).toLocaleLowerCase(filterLocale).trim();
	
	debugger;
	for (let filterText in filterValues) {
		if (stringValue.indexOf(filterText) !== -1) {
			return true
		}
	}
	
	return false;
}

export default listContains;

// export default function() {
// 	FilterService.register("CONTAINS_TRIM", trimContains);
// 	FilterService.register("LIST_CONTAINS", listContains);
// }