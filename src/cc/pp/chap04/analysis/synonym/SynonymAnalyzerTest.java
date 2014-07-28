package cc.pp.chap04.analysis.synonym;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import cc.pp.common.TestUtil;

public class SynonymAnalyzerTest extends TestCase {

	private IndexSearcher searcher;
	private static SynonymAnalyzer synonymAnalyzer = new SynonymAnalyzer(new TestSynonymEngine());
	
	@Override
	public void setUp() throws Exception {

		RAMDirectory dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, synonymAnalyzer, //
				IndexWriter.MaxFieldLength.UNLIMITED);

		Document doc = new Document();
		doc.add(new Field("content", "The quick brown fox jumps over the lazy dog", //
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);
		writer.close();

		searcher = new IndexSearcher(dir, true);
	}

	@Override
	public void tearDown() throws IOException {
		searcher.close();
	}

	public void testJumps() throws IOException {

		TokenStream stream = synonymAnalyzer.tokenStream("contents", // 用synonymAnalyzer分析器分析
				new StringReader("jumps"));
		TermAttribute term = stream.addAttribute(TermAttribute.class);
		PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);

		int i = 0;
		String[] expected = new String[] { "jumps", "hops", "leaps" };
		while (stream.incrementToken()) {
			assertEquals(expected[i], term.term());
			// 验证同义词位置
			int expectedPos;
			if (i == 0) {
				expectedPos = 1;
			} else {
				expectedPos = 0;
			}
			assertEquals(expectedPos, posIncr.getPositionIncrement());
			i++;
		}
	}

	public void testSearchByAPI() throws IOException {
		// 查询“hops”
		TermQuery tq = new TermQuery(new Term("content", "hops"));
		assertEquals(1, TestUtil.hitCount(searcher, tq));
		// 查询短语“fox hops”
		PhraseQuery pq = new PhraseQuery();
		pq.add(new Term("content", "fox"));
		pq.add(new Term("content", "hops"));
		assertEquals(1, TestUtil.hitCount(searcher, pq));
	}

	public void testWithQueryParser() throws Exception {

		Query query = new QueryParser(Version.LUCENE_30, "content", //
				synonymAnalyzer).parse("\"fox hops\"");
		assertEquals(1, TestUtil.hitCount(searcher, query));
		System.out.println("With SynonymAnalyzer, \"fox jumps\" parses to " //
				+ query.toString("content"));

		query = new QueryParser(Version.LUCENE_30, "content", //
				new StandardAnalyzer(Version.LUCENE_30)).parse("\"fox jumps\"");
		assertEquals(1, TestUtil.hitCount(searcher, query));
		System.out.println("With StandardAnalyzer, \"fox jumps\" parses to " //
				+ query.toString("content"));
	}

}
