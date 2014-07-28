package cc.pp.chap04.analysis;

import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

@SuppressWarnings({ "null", "unused", "resource" })
public class Fragments {

	public void frag1() throws Exception {

		Directory dir = null;
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		IndexWriter writer = new IndexWriter(dir, analyzer,//
				IndexWriter.MaxFieldLength.UNLIMITED);
	}

	public void frag2() throws Exception {

		IndexWriter writer = null;
		Document doc = new Document();
		doc.add(new Field("title", "This is the title", Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("contents", "...some document contentd...", //
				Field.Store.YES, Field.Index.ANALYZED));
		writer.addDocument(doc);
	}

	public void frag3() throws Exception {

		Analyzer analyzer = null;
		String text = null;
		TokenStream stream = analyzer.tokenStream("contents", new StringReader(text));
		PositionIncrementAttribute posIncr = stream.addAttribute(PositionIncrementAttribute.class);
		while (stream.incrementToken()) {
			System.out.println("posIncr=" + posIncr.getPositionIncrement());
		}
	}

}
