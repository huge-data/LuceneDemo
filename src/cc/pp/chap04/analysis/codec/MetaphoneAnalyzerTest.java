package cc.pp.chap04.analysis.codec;

import junit.framework.TestCase;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import cc.pp.chap04.analysis.AnalyzerUtils;

public class MetaphoneAnalyzerTest extends TestCase {

	public void testMetaphoneReplacementAnalyzer() throws Exception {

		MetaphoneReplacementAnalyzer analyzer = new MetaphoneReplacementAnalyzer();
		AnalyzerUtils.displayTokens(analyzer, "The quick brown fox jumped over the lazy dog");

		System.out.println("-------------------");
		AnalyzerUtils.displayTokens(analyzer, "Tha quick brown phox jumpd ovvar tha lazi dag");
	}

	public void testKoolKat() throws Exception {

		RAMDirectory dir = new RAMDirectory();
		Analyzer analyzer = new MetaphoneReplacementAnalyzer();
		IndexWriter writer = new IndexWriter(dir, analyzer, //
				true, IndexWriter.MaxFieldLength.UNLIMITED);

		Document doc = new Document();
		doc.add(new Field("contents", "cool cat", Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);
		writer.close();

		IndexSearcher searcher = new IndexSearcher(dir);
		Query query = new QueryParser(Version.LUCENE_30, "contents", analyzer).parse("kool kat");
		TopDocs hits = searcher.search(query, 1);
		assertEquals(1, hits.totalHits);

		int docID = hits.scoreDocs[0].doc;
		doc = searcher.doc(docID);
		assertEquals("cool cat", doc.get("contents"));

		searcher.close();
	}

}
