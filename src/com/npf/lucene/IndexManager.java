package com.npf.lucene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class IndexManager {
	
	public static ClassLoader manager = IndexManager.class.getClassLoader();

	public static final String dirPath = manager.getResource("com/npf/lucene/file").getPath();
	
	public static final String indexPath = manager.getResource("com/npf/lucene/index").getPath();
	
	@Test
	public void indexCreate() throws Exception{
		//创建文档列表,保存多个Document
		List<Document> docList = new ArrayList<Document>();
		
		//指定文件所在目录
		File dir = new File(dirPath); 
		
		for(File file : dir.listFiles()){
			//文件名称
			String fileName = file.getName();
			//文件内容
			String fileContext = FileUtils.readFileToString(file);
			//文件大小
			Long fileSize = FileUtils.sizeOf(file);
			
			//文档对象,文件系统中的一个文件就是一个Document对象
			Document doc = new Document();
			
			//第一个参数:域名
			//第二个参数:域值
			//第三个参数:是否存储,是为yes,不存储为no
			TextField nameFiled = new TextField("fileName", fileName, Store.YES);
			TextField contextFiled = new TextField("fileContext", fileContext, Store.YES);
			TextField sizeFiled = new TextField("fileSize", fileSize.toString(), Store.YES);
			
			//将所有的域都存入文档中
			doc.add(nameFiled);
			doc.add(contextFiled);
			doc.add(sizeFiled);
			
			docList.add(doc);
			
			//创建分词器,StandardAnalyzer标准分词器,标准分词器对英文分词效果很好,对中文是单字分词
			Analyzer analyzer = new StandardAnalyzer();
			//指定索引和文档存储的目录
			Directory directory = FSDirectory.open(new File(indexPath));
			//创建写对象的初始化对象
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
			//创建索引和文档写对象
			IndexWriter indexWriter = new IndexWriter(directory, config);
			
			//将文档加入到索引和文档的写对象中
			for(Document doc1 : docList){
				indexWriter.addDocument(doc1);
			}
			//提交
			indexWriter.commit();
			//关闭流
			indexWriter.close();
		}
	}
}
