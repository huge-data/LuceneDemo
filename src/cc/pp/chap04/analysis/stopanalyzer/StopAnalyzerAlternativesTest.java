package cc.pp.chap04.analysis.stopanalyzer;

import java.io.IOException;

import junit.framework.TestCase;
import cc.pp.chap04.analysis.AnalyzerUtils;

public class StopAnalyzerAlternativesTest extends TestCase {

	public void testOutput() throws IOException {

		String text = "The quick aa2233@ss brown Q36...";
		AnalyzerUtils.displayTokens(new StopAnalyzer1(), text);
		System.out.println();
		AnalyzerUtils.displayTokens(new StopAnalyzer2(), text);
		//		AnalyzerUtils.displayTokens(new StopAnalyzerFlawed(), "The quick brown...");
	}

	public void testStopAnalyzer1() throws IOException {

		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer1(), //
				"The quick brown...", new String[] { "quick", "brown" });
	}

	public void testStopAnalyzer2() throws IOException {

		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzer2(), //
				"The quick brown...", new String[] { "quick", "brown" });
	}

	public void testStopAnalyzerFlawed() throws IOException {

		AnalyzerUtils.assertAnalyzesTo(new StopAnalyzerFlawed(), //
				"The quick brown...", new String[] { "the", "quick", "brown" });
	}

}
