package cc.pp.chap03.searching;

import java.io.IOException;
import java.util.Vector;

import junit.framework.TestCase;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class ScoreTest extends TestCase {

	private Directory dir;

	@Override
	public void setUp() {
		dir = new RAMDirectory();
	}

	@Override
	public void tearDown() throws IOException {
		dir.close();
	}

	public void testSimple() throws Exception {

		indexSingleFieldDocs(new Field[] { new Field("contents", "x", //
				Field.Store.YES, Field.Index.ANALYZED) });
		IndexSearcher searcher = new IndexSearcher(dir);
		searcher.setSimilarity(new SimpleSimilarity());

		Query query = new TermQuery(new Term("contents", "x"));
		Explanation explanation = searcher.explain(query, 0);
		System.out.println(explanation);

		TopDocs docs = searcher.search(query, 10);
		assertEquals(1, docs.totalHits);
		assertEquals(1F, docs.scoreDocs[0].score, 0.0);

		searcher.close();
	}

	public void testWildcard() throws Exception {

		indexSingleFieldDocs(new Field[] { new Field("contents", "wild", Field.Store.YES, Field.Index.ANALYZED),
				new Field("contents", "child", Field.Store.YES, Field.Index.ANALYZED),
				new Field("contents", "mild", Field.Store.YES, Field.Index.ANALYZED),
				new Field("contentd", "mildew", Field.Store.YES, Field.Index.ANALYZED) });
		IndexSearcher searcher = new IndexSearcher(dir);
		Query query = new WildcardQuery(new Term("contents", "?ild*"));
		TopDocs docs = searcher.search(query, 10);
		assertEquals("child no match", 3, docs.totalHits);
		assertEquals("score the same", docs.scoreDocs[0].score, //
				docs.scoreDocs[1].score, 0.0);
		assertEquals("score the same", docs.scoreDocs[1].score, //
				docs.scoreDocs[2].score, 0.0);

		searcher.close();
	}

	public void testFuzzy() throws Exception {

		indexSingleFieldDocs(new Field[] { new Field("contents", "fuzzy", Field.Store.YES, Field.Index.ANALYZED),
				new Field("contents", "wuzzy", Field.Store.YES, Field.Index.ANALYZED) });
		IndexSearcher searcher = new IndexSearcher(dir);
		Query query = new FuzzyQuery(new Term("contents", "wuzza"));
		TopDocs docs = searcher.search(query, 10);
		assertEquals("both close enough", 2, docs.totalHits);
		assertTrue("wuzzy closer than fuzzy", docs.scoreDocs[0].score != docs.scoreDocs[1].score);

		Document doc = searcher.doc(docs.scoreDocs[0].doc);
		assertEquals("wuzza bear", "wuzzy", doc.get("contents"));
		searcher.close();
	}

	private void indexSingleFieldDocs(Field[] fields) throws Exception {

		IndexWriter writer = new IndexWriter(dir, new WhitespaceAnalyzer(),//
				IndexWriter.MaxFieldLength.UNLIMITED);
		for (Field f : fields) {
			Document doc = new Document();
			doc.add(f);
			writer.addDocument(doc);
		}
		writer.optimize();
		writer.close();
	}

	public static class SimpleSimilarity extends Similarity {

		private static final long serialVersionUID = 1L;

		@Override
		public float coord(int overlap, int maxOverlap) {
			return 1.0f;
		}

		@SuppressWarnings("rawtypes")
		public float idf(Vector terms, Searcher searcher) {
			return 1.0f;
		}

		@Override
		public float lengthNorm(String field, int numTerms) {
			return 1.0f;
		}

		@Override
		public float queryNorm(float sumOfSquaredWeights) {
			return 1.0f;
		}

		@Override
		public float sloppyFreq(int distance) {
			return 2.0f;
		}

		@Override
		public float tf(float freq) {
			return freq;
		}

		@Override
		public float idf(int docFreq, int numDocs) {
			return 1.0f;
		}

	}

}
