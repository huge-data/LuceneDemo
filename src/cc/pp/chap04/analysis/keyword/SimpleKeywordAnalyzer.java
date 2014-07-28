package cc.pp.chap04.analysis.keyword;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.TokenStream;

/**
 * 该类限制了token域的宽度不大于255，实现前提是假设关键词长度不大于255.
 * @author Administrator
 *
 */
public class SimpleKeywordAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new CharTokenizer(reader) {
			@Override
			protected boolean isTokenChar(char c) {
				return true;
			}
		};
	}

}
