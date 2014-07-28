package cc.pp.chap11.admin;

import java.io.IOException;

import org.apache.lucene.benchmark.byTask.PerfRunData;
import org.apache.lucene.benchmark.byTask.tasks.CreateIndexTask;
import org.apache.lucene.benchmark.byTask.utils.Config;
import org.apache.lucene.index.IndexWriter;

/**
 * 可以使用contrib/benchmark算法创建ThreadedIndexWriter
 * @author wanggang
 *
 */
public class CreateThreadedIndexTask extends CreateIndexTask {

	public CreateThreadedIndexTask(PerfRunData runData) {
		super(runData);
	}

	@Override
	public int doLogic() throws IOException {

		PerfRunData runData = getRunData();
		Config config = runData.getConfig();
		IndexWriter writer = new ThreadedIndexWriter(runData.getDirectory(), //
				runData.getAnalyzer(), true, config.get("writer.num.threads", 4), //
				config.get("writer.max.threads.queue.size", 20), //
				IndexWriter.MaxFieldLength.UNLIMITED);
		CreateIndexTask.setIndexWriterConfig(writer, config);
		runData.setIndexWriter(writer);

		return -1;
	}

}
