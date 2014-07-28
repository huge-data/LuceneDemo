package cc.pp.chap03.searching;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class NumericRangeQueryTest extends TestCase {

	public void testInclusive() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexSearcher searcher = new IndexSearcher(dir);
		NumericRangeQuery<?> query = NumericRangeQuery. //
				newIntRange("pubmonth", 200605, 200609, true, true);
		TopDocs docs = searcher.search(query, 10);
		for (int i = 0; i < docs.totalHits; i++) {
			System.out.println("match: " + i + ": " + searcher.doc(docs.scoreDocs[i].doc).get("author"));
		}
		assertEquals(1, docs.totalHits);
		searcher.close();
		dir.close();
	}

	public void testExclusive() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexSearcher searcher = new IndexSearcher(dir);
		NumericRangeQuery<?> query = NumericRangeQuery. //
				newIntRange("pubmonth", 200605, 200609, false, false);
		TopDocs docs = searcher.search(query, 10);
		assertEquals(0, docs.totalHits);
		searcher.close();
		dir.close();
	}

}
