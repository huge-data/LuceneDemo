package cc.pp.chap05.advance.searching;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import junit.framework.TestCase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.function.CustomScoreProvider;
import org.apache.lucene.search.function.CustomScoreQuery;
import org.apache.lucene.search.function.FieldScoreQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class FunctionQueryTest extends TestCase {

	IndexSearcher searcher;
	IndexWriter writer;

	private void addDoc(int score, String content) throws Exception {

		Document doc = new Document();
		doc.add(new Field("score", Integer.toString(score), Field.Store.NO, //
				Field.Index.NOT_ANALYZED_NO_NORMS));
		doc.add(new Field("content", content, Field.Store.NO, Field.Index.ANALYZED));
		writer.addDocument(doc);
	}

	@Override
	public void setUp() throws Exception {

		Directory dir = new RAMDirectory();
		writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), //
				IndexWriter.MaxFieldLength.UNLIMITED);
		addDoc(7, "this hat is green");
		addDoc(42, "this hat is blue");
		writer.close();

		searcher = new IndexSearcher(dir, true);
	}

	public void tearDowm() throws Exception {
		super.tearDown();
		searcher.close();
	}

	public void testFieldScoreQuery() throws IOException {
		
		Query query = new FieldScoreQuery("score", FieldScoreQuery.Type.BYTE);
		TopDocs hits = searcher.search(query, 10);
		assertEquals(2, hits.scoreDocs.length); // 匹配所以文档
		assertEquals(1, hits.scoreDocs[0].doc); // 第一个是分数最高的文档，文档编号为1
		assertEquals(42, (int) hits.scoreDocs[0].score);
		assertEquals(0, hits.scoreDocs[1].doc); // 第2个是分数次之的文档，文档编号为0
		assertEquals(7, (int) hits.scoreDocs[1].score);
	}

	public void testCustomScoreQuery() throws Exception {
		
		Query query = new QueryParser(Version.LUCENE_30, "content", //
				new StandardAnalyzer(Version.LUCENE_30)).parse("the green hat");
		FieldScoreQuery queryField = new FieldScoreQuery("score", FieldScoreQuery.Type.BYTE);
		CustomScoreQuery customQuery = new CustomScoreQuery(query, queryField) {
			private static final long serialVersionUID = 1L;
			@Override
			public CustomScoreProvider getCustomScoreProvider(IndexReader reader) {
				return new CustomScoreProvider(reader) {
					@Override
					public float customScore(int doc, float subQueryScore, float valSrcScore) {
						return (float) (Math.sqrt(subQueryScore) * valSrcScore);
					}
				};
			}
		};

		TopDocs hits = searcher.search(customQuery, 10);

		assertEquals(2, hits.scoreDocs.length);
		/**
		 * 即使第一个文档的内容更接近搜索语句，但是第二个评分远大于第一个，所以第一个文档被放在搜索首位。
		 */
		assertEquals(1, hits.scoreDocs[0].doc);
		assertEquals(0, hits.scoreDocs[1].doc);
	}

	public static class RecencyBoostingQuery extends CustomScoreQuery {

		private static final long serialVersionUID = 1L;

		double multiplier;
		int today;
		int maxDayAgo;
		String dayField;
		private static int MSEC_PER_DAY = 24 * 3600 * 1000;

		public RecencyBoostingQuery(Query q, double multiplier, //
				int maxDaysAgo, String dayField) {
			super(q);
			today = (int) (new Date().getTime() / MSEC_PER_DAY);
			this.multiplier = multiplier;
			this.maxDayAgo = maxDaysAgo;
			this.dayField = dayField;
		}

		private class RecencyBooster extends CustomScoreProvider {

			final int[] publishDay;

			public RecencyBooster(IndexReader reader) throws IOException {
				super(reader);
				publishDay = FieldCache.DEFAULT.getInts(reader, dayField);
			}

			@Override
			public float customScore(int doc, float subQueryScore, float valSrcScore) {
				int daysAgo = today - publishDay[doc];
				if (daysAgo < maxDayAgo) {
					float boost = (float) (multiplier * (maxDayAgo - daysAgo)) / maxDayAgo;
					return (float) (subQueryScore * (1.0 + boost));
				} else {
					return subQueryScore;
				}
			}
		}

		@Override
		public CustomScoreProvider getCustomScoreProvider(IndexReader reader) throws IOException {
			return new RecencyBooster(reader);
		}
	}

	public void testRecency() throws Exception {

		Directory dir = FSDirectory.open(new File("index/chap03index/"));
		IndexReader reader = IndexReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(dir);
		searcher.setDefaultFieldSortScoring(true, true);

		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", //
				new StandardAnalyzer(Version.LUCENE_30));
		Query query = parser.parse("java in action");
		Query query2 = new RecencyBoostingQuery(query, 2.0, 2 * 365, "pubmonthAsDay");
		Sort sort = new Sort(new SortField[] { SortField.FIELD_SCORE, //
				new SortField("title2", SortField.STRING) });
		TopDocs hits = searcher.search(query2, null, 5, sort);

		for (int i = 0; i < hits.scoreDocs.length; i++) {
			Document doc = reader.document(hits.scoreDocs[i].doc);
			System.out.println((i + 1) + ": " + doc.get("title") + ": pubmonth = " + //
					doc.get("pubmonth") + " score = " + hits.scoreDocs[i].score);
		}

		searcher.close();
		reader.clone();
		dir.close();
	}

}
