package cc.pp.chap04.analysis.synonym;

import java.io.IOException;

import cc.pp.chap04.analysis.AnalyzerUtils;

public class SynonymAnalyzerViewer {

	/**
	 * 主函数
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		SynonymEngine engine = new TestSynonymEngine();

		AnalyzerUtils.displayTokensWithPositions(new SynonymAnalyzer(engine), //
				"The quick brown fox jumps over the lazy dog");

		//		AnalyzerUtils.displayTokensWithPositions(new SynonymAnalyzer(engine), //
		//				"\"Oh, we get both kinds - country AND western!\" - B.B.");
	}

}
