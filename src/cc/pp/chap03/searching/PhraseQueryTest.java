package cc.pp.chap03.searching;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class PhraseQueryTest extends TestCase {

	private Directory dir;
	private IndexSearcher searcher;

	@Override
	protected void setUp() throws Exception {

		dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(), IndexWriter.MaxFieldLength.UNLIMITED);
		Document doc = new Document();
		doc.add(new Field("field", "the quick brown fox jumped over tha lazy dog", //
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);
		writer.close();

		searcher = new IndexSearcher(dir);
	}

	@Override
	protected void tearDown() throws IOException {
		searcher.close();
		dir.close();
	}

	private boolean matched(String[] phrase, int slop) throws IOException {

		PhraseQuery query = new PhraseQuery();
		query.setSlop(slop);
		for (String word : phrase) {
			query.add(new Term("field", word));
		}
		TopDocs docs = searcher.search(query, 10);
		return docs.totalHits > 0;
	}

	public void testSlopComparsion() throws IOException {

		String[] phrase = new String[] { "quick", "fox" };
		assertFalse("exact phrase not found", matched(phrase, 0));
		assertTrue("close enough", matched(phrase, 1));
	}

	public void testReverse() throws IOException {

		String[] phrase = new String[] { "fox", "quick" };
		assertFalse("hop flop", matched(phrase, 2));
		assertTrue("hop hop flop", matched(phrase, 3));
	}

	public void testMultiple() throws IOException {

		assertFalse("not close enough", matched(new String[] { "quick", "jumped", "lazy" }, 3));
		assertTrue("just enough", matched(new String[] { "quick", "jumped", "lazy" }, 4));
		assertFalse("almost but not quite", matched(new String[] { "lazy", "jumped", "quick" }, 7));
		assertTrue("bingo", matched(new String[] { "lazy", "jumped", "quick" }, 8));
	}

}
