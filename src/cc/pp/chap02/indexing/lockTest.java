package cc.pp.chap02.indexing;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import cc.pp.common.TestUtil;

public class lockTest extends TestCase {

	private Directory dir;
	private File indexDir;

	@Override
	protected void setUp() throws IOException {

		indexDir = new File(System.getProperty("java.io.tmpdir", "tmp") + //
				System.getProperty("file.separator") + "index");
		dir = FSDirectory.open(indexDir);
	}

	public void testWriteLock() throws IOException {

		IndexWriter writer1 = new IndexWriter(dir, new SimpleAnalyzer(), //
				IndexWriter.MaxFieldLength.UNLIMITED);
		IndexWriter writer2 = null;
		try {
			writer2 = new IndexWriter(dir, new SimpleAnalyzer(), //
					IndexWriter.MaxFieldLength.UNLIMITED);
			fail("We should never reach this point");
		} catch (LockObtainFailedException e) {
			//			e.printStackTrace();
		} finally {
			writer1.close();
			assertNull(writer2);
			TestUtil.rmDir(indexDir);
		}
	}

}
