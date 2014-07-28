package cc.pp.chap05.advance.searching;

import java.io.File;

import junit.framework.TestCase;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import cc.pp.common.TestUtil;

public class MultiFieldQueryParserTest extends TestCase {

	public void testDefaultOperator() throws Exception {

		Query query = new MultiFieldQueryParser(Version.LUCENE_30, //
				new String[] { "title", "subject" }, //
				new SimpleAnalyzer()).parse("development");
		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexSearcher searcher = new IndexSearcher(dir, true);

		TopDocs hits = searcher.search(query, 10);

		assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Ant in Action"));

		assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Extreme Programming Explained"));

		searcher.close();
		dir.close();
	}

	public void testSpecifiedOperator() throws Exception {

		Query query = MultiFieldQueryParser.parse(Version.LUCENE_30, "lucene", //
				new String[] { "title", "subject" }, //
				new BooleanClause.Occur[] { BooleanClause.Occur.MUST, BooleanClause.Occur.MUST }, //
				new SimpleAnalyzer());
		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexSearcher searcher = new IndexSearcher(dir, true);

		TopDocs hits = searcher.search(query, 10);

		assertTrue(TestUtil.hitsIncludeTitle(searcher, hits, "Lucene in Action, Second Edition"));

		assertEquals("one and only one", 1, hits.scoreDocs.length);

		searcher.close();
		dir.close();
	}

}
