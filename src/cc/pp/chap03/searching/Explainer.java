package cc.pp.chap03.searching;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Explainer {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws Exception {

		String indexDir = "index/chap03index/";
		String queryExpression = "junit";
		Directory dir = FSDirectory.open(new File(indexDir));
		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new SimpleAnalyzer());
		Query query = parser.parse(queryExpression);

		System.out.println("Query: " + queryExpression);

		IndexSearcher searcher = new IndexSearcher(dir);
		TopDocs docs = searcher.search(query, 10);
		for (ScoreDoc match : docs.scoreDocs) {
			Explanation explanation = searcher.explain(query, match.doc);
			System.out.println("---------------");
			Document doc = searcher.doc(match.doc);
			System.out.println(doc.get("title"));
			System.out.println(explanation.toString());
		}

		searcher.close();
		dir.close();
	}

}
