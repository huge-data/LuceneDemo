package cc.pp.chap05.advance.searching;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class MultiSearcherTest extends TestCase {

	private IndexSearcher[] searchers;

	@Override
	public void setUp() throws Exception {

		String[] animals = { "aardvark", "beaver", "coati", "dog", "elephant", //
				"frog", "gila monster", "horse", "iguana", "javelina", //
				"kangaroo", "lemur", "moose", "nematode", "orca", "python", //
				"quokka", "rat", "scorpion", "tarantula", "uromastyx", //
				"vicuna", "walrus", "xiphias", "yak", "zebra" };

		Analyzer analyzer = new WhitespaceAnalyzer();

		Directory adir = new RAMDirectory();
		Directory bdir = new RAMDirectory();

		IndexWriter awriter = new IndexWriter(adir, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);
		IndexWriter bwriter = new IndexWriter(bdir, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);

		for (int i = animals.length - 1; i >= 0; i--) {
			Document doc = new Document();
			String animal = animals[i];
			doc.add(new Field("animal", animal, Field.Store.YES, Field.Index.NOT_ANALYZED));
			if (animal.charAt(0) < 'n') {
				awriter.addDocument(doc);
			} else {
				bwriter.addDocument(doc);
			}
		}

		awriter.close();
		bwriter.close();

		searchers = new IndexSearcher[2];
		searchers[0] = new IndexSearcher(adir);
		searchers[1] = new IndexSearcher(bdir);
	}

	@SuppressWarnings("resource")
	public void testMulti() throws IOException {
		
		MultiSearcher searcher = new MultiSearcher(searchers);
		TermRangeQuery query = new TermRangeQuery("animal", "h", "t", true, true);
		TopDocs hits = searcher.search(query, 10);
		assertEquals("tarantula not included", 12, hits.totalHits);
	}

}
