package com.handyapps.timesense.dataobjects;

import java.util.List;

public class BrodcastResult {

	private List<String> failedNumbers;
	private List<String> successNumbers;

	public List<String> getSuccessNumbers() {
		return successNumbers;
	}

	public void setSuccessNumbers(List<String> successNumbers) {
		this.successNumbers = successNumbers;
	}

	public List<String> getFailedNumbers() {
		return failedNumbers;
	}

	public void setFailedNumbers(List<String> failedNumbers) {
		this.failedNumbers = failedNumbers;
	}
}
