package cc.pp.chap04.analysis.stopanalyzer;

import junit.framework.TestCase;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.util.Version;

import cc.pp.chap04.analysis.AnalyzerUtils;

public class StopAnalyzerTest extends TestCase {

	private final StopAnalyzer analyzer = new StopAnalyzer(Version.LUCENE_30);

	public void testHoles() throws Exception {

		String[] expected = { "one", "enough" };
		AnalyzerUtils.assertAnalyzesTo(analyzer, "one is not enough", expected);
		AnalyzerUtils.assertAnalyzesTo(analyzer, "one is enough", expected);
		AnalyzerUtils.assertAnalyzesTo(analyzer, "one enough", expected);
		AnalyzerUtils.assertAnalyzesTo(analyzer, "one but not enough", expected);
	}

}
