package cc.pp.chap05.advance.searching;

import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermRangeFilter;

public class Fragments {

	@SuppressWarnings("unused")
	public void frags1() {

		String lowerTerm = null;
		String upperTerm = null;
		String fieldName = null;
		Filter filter;
		// 开始
		filter = new TermRangeFilter(fieldName, null, upperTerm, false, true);
		filter = new TermRangeFilter(fieldName, lowerTerm, null, true, false);
		filter = TermRangeFilter.Less(fieldName, upperTerm);
		filter = TermRangeFilter.More(fieldName, lowerTerm);
		// 结束
	}

}
