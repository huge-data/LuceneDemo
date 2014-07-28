package cc.pp.chap04.analysis.positional;

import junit.framework.TestCase;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import cc.pp.common.TestUtil;

public class PositionalPorterStopAnalyzerTest extends TestCase {

	private static PositionalPorterStopAnalyzer porterAnalyzer = new PositionalPorterStopAnalyzer();

	private IndexSearcher searcher;
	private QueryParser parser;

	@Override
	public void setUp() throws Exception {

		RAMDirectory dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, porterAnalyzer, //
				IndexWriter.MaxFieldLength.UNLIMITED);
		Document doc = new Document();
		doc.add(new Field("contents", "The quick brown fox jumps over the lazy dog",//
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);
		writer.close();
		searcher = new IndexSearcher(dir, true);
		parser = new QueryParser(Version.LUCENE_30, "contents", porterAnalyzer);
	}

	public void testWithSlop() throws Exception {

		parser.setPhraseSlop(1);
		Query query = parser.parse("\"over the lazy\"");
		assertEquals("hole accounted for", 1, TestUtil.hitCount(searcher, query));
	}

	public void testStems() throws Exception {

		Query query = new QueryParser(Version.LUCENE_30, "contents", //
				porterAnalyzer).parse("laziness");
		assertEquals("lazi", 1, TestUtil.hitCount(searcher, query));

		query = parser.parse("\"fox jumped\"");
		assertEquals("jump jumps jumped jumping", 1, TestUtil.hitCount(searcher, query));
	}

}
