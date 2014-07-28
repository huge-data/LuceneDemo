package cc.pp.chap02.indexing;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

@SuppressWarnings("unused")
public class Fragments {

	public static void indexNumbersMethod() {
		new Field("size", "4096", Field.Store.YES, Field.Index.NOT_ANALYZED);
		new Field("price", "10.99", Field.Store.YES, Field.Index.NOT_ANALYZED);
		new Field("author", "Arthur C. Clark", Field.Store.YES, Field.Index.NOT_ANALYZED);
	}

	public static final String COMPANY_DOMAIN = "example.com";
	public static final String BAD_DOMAIN = "yucky-domain.com";

	private String getSenderEmail() {
		return "bob@smith.com";
	}

	private String getSenderName() {
		return "Bob Smith";
	}

	private String getSenderDomain() {
		return COMPANY_DOMAIN;
	}

	private String getSubject() {
		return "Hi there Lisa";
	}

	private String getBody() {
		return "I don't have much to say";
	}

	private boolean isImportant(String lowerDomain) {
		return lowerDomain.endsWith(COMPANY_DOMAIN);
	}

	private boolean isUnimportant(String lowerDomain) {
		return lowerDomain.endsWith(BAD_DOMAIN);
	}

	public void ramDirExample() throws IOException {
		Analyzer analyzer = new WhitespaceAnalyzer();
		Directory ramDir = new RAMDirectory();
		@SuppressWarnings("resource")
		IndexWriter writer = new IndexWriter(ramDir, analyzer, //
				IndexWriter.MaxFieldLength.UNLIMITED);
	}

	public void dirCopy() throws IOException {
		Directory otherDir = null;
		@SuppressWarnings("resource")
		Directory ramDir = new RAMDirectory(otherDir);
	}

	public void addIndexes() throws IOException {
		Directory otherDir = null;
		Directory ramDir = null;
		Analyzer analyzer = null;
		@SuppressWarnings("resource")
		IndexWriter writer = new IndexWriter(otherDir, analyzer, //
				IndexWriter.MaxFieldLength.UNLIMITED);
		writer.addIndexesNoOptimize(new Directory[] { ramDir });
	}

	public void docBoostMethod() throws IOException {

		Directory dir = new RAMDirectory();
		IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30),//
				IndexWriter.MaxFieldLength.UNLIMITED);

		Document doc = new Document();
		String senderEmail = getSenderEmail();
		String senderName = getSenderName();
		String subject = getSubject();
		String body = getBody();
		doc.add(new Field("senderEmail", senderEmail, Field.Store.YES, Field.Index.NOT_ANALYZED));
		doc.add(new Field("senderName", senderName, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("subject", subject, Field.Store.YES, Field.Index.ANALYZED));
		doc.add(new Field("body", body, Field.Store.NO, Field.Index.ANALYZED));
		String lowerDomain = getSenderDomain().toLowerCase();
		if (isImportant(lowerDomain)) {
			doc.setBoost(1.5F);
		} else if (isUnimportant(lowerDomain)) {
			doc.setBoost(0.1F);
		}
		writer.addDocument(doc);
		writer.close();
	}

	public void fieldBoostMethod() {

		String senderName = getSenderName();
		String subject = getSubject();
		Field subjectField = new Field("subject", subject, Field.Store.YES, Field.Index.ANALYZED);
		subjectField.setBoost(1.2F);
	}

	public void numberField() {
		Document doc = new Document();
		doc.add(new NumericField("price").setDoubleValue(19.99));
	}

	public void numberTimestamp() {
		Document doc = new Document();
		doc.add(new NumericField("timestamp").setLongValue(new Date().getTime()));
		doc.add(new NumericField("day").setIntValue((int) (new Date().getTime() / 24 / 3600)));
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		doc.add(new NumericField("dayOfMonth").setIntValue(cal.get(Calendar.DAY_OF_MONTH)));
	}

	public void setInfoStream() throws IOException {
		Directory dir = null;
		Analyzer analyzer = null;
		@SuppressWarnings("resource")
		IndexWriter writer = new IndexWriter(dir, analyzer, true, //
				IndexWriter.MaxFieldLength.UNLIMITED);
		writer.setInfoStream(System.out);
	}

	public void dateMethod() {
		Document doc = new Document();
		doc.add(new Field("indexDate", //
				DateTools.dateToString(new Date(), DateTools.Resolution.DAY),
				Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

	public void numericField() {

		Document doc = new Document();
		NumericField price = new NumericField("price");
		price.setDoubleValue(19.99);
		doc.add(price);

		NumericField timestamp = new NumericField("timestamp");
		timestamp.setLongValue(new Date().getTime());
		doc.add(timestamp);

		Date b = new Date();
		NumericField birthday = new NumericField("birthday");
		String v = DateTools.dateToString(b, DateTools.Resolution.DAY);
		birthday.setIntValue(Integer.parseInt(v));
		doc.add(birthday);
	}

	public void indexAuthors() {
		String[] authors = new String[] { "lisa", "tom" };
		Document doc = new Document();
		for (String author : authors) {
			doc.add(new Field("author", author, Field.Store.YES, Field.Index.ANALYZED));
		}
	}

}
