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

public class IndexManager {
	
	public static ClassLoader manager = IndexManager.class.getClassLoader();

	public static final String dirPath = manager.getResource("com/npf/lucene/file").getPath();
	
	public static final String indexPath = manager.getResource("com/npf/lucene/index").getPath();
	
	
	public static void main(String[] args) throws Exception{
		//1. 创建文档列表,用来保存多个Document
		List<Document> docList = createDocumentList();
		//2.获取源文件所在目录
		File dir = getSourceFileDirectory(); 
		//3.遍历源文件所在目录下的所有文件，并将每个具体文件封装成Document,加入Document列表中
		docList = lookupFileIntoDocument(docList,dir);
		//4.创建分词器Analyzer
		Analyzer analyzer = createAnalyzer();
		//5.指定索引存储的目录
		Directory directory = createDirectory();
		//6.创建索引写对象
		IndexWriter indexWriter = createIndexWriter(analyzer,directory);
		//7.将文档加入索引写对象中
		addDocumentToIndexWriter(indexWriter,docList);
		//8.提交索引写对象
		indexWriterCommit(indexWriter);
		//9.关闭索引写对象
		indexWriterClose(indexWriter);
	}
	
	/**
	 * 创建文档列表,保存多个Document
	 */
	public static List<Document> createDocumentList(){
		List<Document> docList = new ArrayList<Document>();
		return docList;
	}
	
	/**
	 * 获取源文件所在目录
	 */
	public static File getSourceFileDirectory(){
		File dir = new File(dirPath); 
		return dir;
	}
	
	/**
	 * 遍历源文件所在目录下的所有文件，并将每个具体文件封装成Document,加入Document列表中
	 */
	public static List<Document> lookupFileIntoDocument(final List<Document> docList,File dir) throws Exception{
		for(File file : dir.listFiles()){
			String fileName = file.getName();
			String fileContext = FileUtils.readFileToString(file);
			Long fileSize = FileUtils.sizeOf(file);
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
		}
		return docList;
	}
	
	/**
	 * 创建分词器,StandardAnalyzer标准分词器,标准分词器对英文分词效果很好,对中文是单字分词
	 */
	public static Analyzer createAnalyzer(){
		Analyzer analyzer = new StandardAnalyzer();
		return analyzer;
	}
	
	/**
	 * 指定索引和文档存储的目录
	 */
	public static Directory createDirectory() throws Exception{
		Directory directory = FSDirectory.open(new File(indexPath));
		return directory;
	}
	
	/**
	 * 创建索引写对象
	 */
	public static IndexWriter createIndexWriter(Analyzer analyzer,Directory directory) throws Exception{
		//创建索引和文档的写对象的初始化对象
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		//创建索引和文档的写对象
		IndexWriter indexWriter = new IndexWriter(directory, config);
		return indexWriter;
	}
	
	/**
	 * 将文档加入到索引写对象中
	 */
	public static void addDocumentToIndexWriter(IndexWriter indexWriter,List<Document> docList) throws Exception{
		for(Document doc1 : docList){
			indexWriter.addDocument(doc1);
		}
	}
	
	/**
	 * 索引写对象的提交
	 */
	public static void indexWriterCommit(IndexWriter indexWriter) throws Exception{
		indexWriter.commit();
	}
	
	/**
	 * 索引写对象的关闭
	 */
	public static void indexWriterClose(IndexWriter indexWriter) throws Exception{
		indexWriter.close();
	}
}
