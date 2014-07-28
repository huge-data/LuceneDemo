package cc.pp.chap11.admin;

import java.util.Collection;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexCommit;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.store.Directory;

public class Fragments {

	@SuppressWarnings({ "unused", "resource" })
	public static void main(String[] args) throws Exception {

		Directory dir = null;
		Analyzer analyzer = null;
		// start
		IndexDeletionPolicy policy = new KeepOnlyLastCommitDeletionPolicy();
		SnapshotDeletionPolicy snapshotter = new SnapshotDeletionPolicy(policy);
		IndexWriter writer = new IndexWriter(dir, analyzer, snapshotter, //
				IndexWriter.MaxFieldLength.UNLIMITED);
		// end

		try {
			IndexCommit commit = snapshotter.snapshot();
			Collection<String> fileNames = commit.getFileNames();
		} finally {
			snapshotter.release();
		}
	}

}
