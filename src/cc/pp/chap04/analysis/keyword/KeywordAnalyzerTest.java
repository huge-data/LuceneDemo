package cc.pp.chap04.analysis.keyword;

import junit.framework.TestCase;

import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import cc.pp.common.TestUtil;

public class KeywordAnalyzerTest extends TestCase {

	private IndexSearcher searcher;

	@Override
	public void setUp() throws Exception {
		
		Directory dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, new SimpleAnalyzer(), //
				IndexWriter.MaxFieldLength.UNLIMITED);

		Document doc = new Document();
		doc.add(new Field("partnum", "Q36", Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
		doc.add(new Field("description", "Illidium Space Modulator", //
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);
		writer.close();

		searcher = new IndexSearcher(dir);
	}

	public void testTermQuery() throws Exception {

		Query query = new TermQuery(new Term("partnum", "Q36"));
		assertEquals(1, TestUtil.hitCount(searcher, query));
	}

	public void testBasicQueryParser() throws Exception {

		Query query = new QueryParser(Version.LUCENE_30, "description", //
				new SimpleAnalyzer()).parse("partnum:Q36 AND SPACE");
		assertEquals("note Q36 -> q", "+partnum:q +space", query.toString("description"));
		assertEquals("doc not found :(", 0, TestUtil.hitCount(searcher, query));
	}

	public void testPerFieldAnalyzer() throws Exception {

		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new SimpleAnalyzer());
		analyzer.addAnalyzer("partnum", new KeywordAnalyzer());
		Query query = new QueryParser(Version.LUCENE_30, "description", //
				analyzer).parse("partnum:Q36 AND SPACE");
		assertEquals("Q36 kept as-is", "+partnum:Q36 +space", query.toString("description"));
		assertEquals("doc found!", 1, TestUtil.hitCount(searcher, query));
	}

}
