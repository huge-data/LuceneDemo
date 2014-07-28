package cc.pp.chap04.analysis.codec;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.TokenStream;

public class MetaphoneReplacementAnalyzer extends Analyzer {

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new MetaphoneReplacementFilter(new LetterTokenizer(reader));
	}

}
