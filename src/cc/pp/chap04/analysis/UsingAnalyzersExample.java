package cc.pp.chap04.analysis;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * 该类不做任何事，只求正确编译。
 * 用来展示Analyzers分析器被使用时的快照。
 */
public class UsingAnalyzersExample {

	/**
	 * 测试函数
	 * @param args
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public static void main(String[] args) throws Exception {

		RAMDirectory dir = new RAMDirectory();

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		IndexWriter writer = new IndexWriter(dir, analyzer, IndexWriter.MaxFieldLength.UNLIMITED);

		Document doc = new Document();
		doc.add(new Field("title", "This is the title", Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("contents", "...some document contents...", //
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);

		writer.addDocument(doc, analyzer);
		writer.close();

		String expression = "some document";

		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", analyzer);
		Query query = parser.parse(expression);

		System.out.println("the query is: " + query.toString());

	}


}
