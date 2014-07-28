package cc.pp.chap03.searching;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class TermRangeQueryTest extends TestCase {

	public void testTermRangeQuery() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexSearcher searcher = new IndexSearcher(dir);

		TermRangeQuery query = new TermRangeQuery("title2", "d", "j", true, true);
		TopDocs docs = searcher.search(query, 100);
		for (int i = 0; i < docs.totalHits; i++) {
			System.out.println("match " + i + ": " + searcher.doc(docs.scoreDocs[i].doc).get("title2"));
		}
		assertEquals(3, docs.totalHits);

		searcher.close();
		dir.close();
	}

}
