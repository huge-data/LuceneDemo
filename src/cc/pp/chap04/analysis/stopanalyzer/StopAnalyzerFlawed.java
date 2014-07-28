package cc.pp.chap04.analysis.stopanalyzer;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;

public class StopAnalyzerFlawed extends Analyzer {

	private final Set<?> stopWords;

	public StopAnalyzerFlawed() {
		this.stopWords = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
	}

	public StopAnalyzerFlawed(String[] stopWords) {
		this.stopWords = StopFilter.makeStopSet(stopWords);
	}

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {
		return new LowerCaseFilter(new StopFilter(true, new LetterTokenizer(reader), stopWords));
	}

}
