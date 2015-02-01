package com.dataobjects;

import java.util.List;

public class RelationRequest {

	String ownerId;
	
	List<String> relatedNumbers;

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public List<String> getRelatedNumbers() {
		return relatedNumbers;
	}

	public void setRelatedNumbers(List<String> relatedNumbers) {
		this.relatedNumbers = relatedNumbers;
	}
}
