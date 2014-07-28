package cc.pp.chap05.advance.searching;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.QueryWrapperFilter;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import cc.pp.common.TestUtil;

public class SecurityFilterTest extends TestCase {

	private IndexSearcher searcher;

	@Override
	protected void setUp() throws Exception {

		Directory dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), //
				IndexWriter.MaxFieldLength.UNLIMITED);

		Document doc = new Document();
		doc.add(new Field("owner", "elwood", Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("keywords", "elwood's sensitive info", //
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);

		doc = new Document();
		doc.add(new Field("owner", "jake", Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("keywords", "jake's sensitive info", //
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);

		writer.close();
		searcher = new IndexSearcher(dir);
	}

	public void testSecurityFilter() throws IOException {

		TermQuery query = new TermQuery(new Term("keywords", "info"));
		assertEquals("Both documents match", 2, TestUtil.hitCount(searcher, query));
		/**
		 * 把owner为jake的文档过滤出来，作为筛选条件
		 */
		Filter jakeFilter = new QueryWrapperFilter(new TermQuery(new Term("owner", "jake")));
		TopDocs hits = searcher.search(query, jakeFilter, 10);
		assertEquals(1, hits.totalHits);
		assertEquals("elwood is safe", "jake's sensitive info", //
				searcher.doc(hits.scoreDocs[0].doc).get("keywords"));
	}

}
