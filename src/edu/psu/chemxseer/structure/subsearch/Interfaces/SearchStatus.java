package edu.psu.chemxseer.structure.subsearch.Interfaces;

/**
 * Record the search status of the searcher.
 * @author dayuyuan
 * 
 */
public class SearchStatus {
	private long postFetchingTime;
	private long dbLoadingTime;
	private long filteringTime;
	private long verifyTime;
	private int verifiedCount;
	private int trueAnswerCount;
	
	/**
	 * Reset all record to 0
	 */
	public void refresh() {
		postFetchingTime = 0;
		dbLoadingTime = 0;
		filteringTime = 0;
		verifyTime = 0;
		verifiedCount = 0;
		trueAnswerCount = 0;
	}

	public String toString() {
		String str = postFetchingTime + "\t" 
					+ dbLoadingTime + 	"\t"
					+ filteringTime +	"\t" 
					+ verifyTime+ 		"\t" 
					+ verifiedCount+ 	"\t" 
					+ trueAnswerCount + "\t";
		return str;
	}
	
	public void addPostFetchingTime(long delta) {
		this.postFetchingTime += delta;
	}

	public void addDbLoadingTime(long delta) {
		this.dbLoadingTime += delta;
	}

	public void addFilteringTime(long delta) {
		this.filteringTime += delta;
	}

	public void addVerifyTime(long delta) {
		this.verifyTime += delta;
	}

	public void addVerifiedCount(int delta) {
		this.verifiedCount += delta;
	}

	public void addTrueAnswerCount(int delta) {
		this.trueAnswerCount += delta;
	}

	public void setVerifiedCount(int verifiedCount) {
		this.verifiedCount = verifiedCount;
	}

	public void setTrueAnswerCount(int trueAnswerCount) {
		this.trueAnswerCount = trueAnswerCount;
	}

	public void setVerifyTime(int verifyTime) {
		this.verifyTime = verifyTime;
	}

	public long getPostFetchingTime() {
		return postFetchingTime;
	}

	public long getDbLoadingTime() {
		return dbLoadingTime;
	}

	public long getFilteringTime() {
		return filteringTime;
	}

	public long getVerifyTime() {
		return verifyTime;
	}

	public int getVerifiedCount() {
		return verifiedCount;
	}

	public int getTrueAnswerCount() {
		return trueAnswerCount;
	}


}
