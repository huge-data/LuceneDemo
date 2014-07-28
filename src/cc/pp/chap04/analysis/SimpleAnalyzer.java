package cc.pp.chap04.analysis;

import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

public class SimpleAnalyzer extends Analyzer {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		AnalyzerUtils.displayTokensWithFullDetails(new SimpleAnalyzer(), //
				"The quick brown fox....");
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new LowerCaseTokenizer(reader);
	}

	@Override
	public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {

		Tokenizer tokenizer = (Tokenizer) getPreviousTokenStream();
		if (tokenizer == null) {
			tokenizer = new LowerCaseTokenizer(reader);
			setPreviousTokenStream(tokenizer);
		} else {
			tokenizer.reset(reader);
		}

		return tokenizer;
	}

}
