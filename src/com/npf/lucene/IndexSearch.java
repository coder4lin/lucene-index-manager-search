package com.npf.lucene;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class IndexSearch {
	
	public static ClassLoader manager = IndexManager.class.getClassLoader();
	
	public static final String indexPath = manager.getResource("com/npf/lucene/index").getPath();
	
	public static void main(String[] args) throws Exception{
		//1.创建分词器(创建索引和使用时所用的分词器必须一致)
		Analyzer analyzer = createAnalyzer();
		//2.创建查询对象
		Query query = createQuery(analyzer);
		//3.指定索引的目录
		Directory dir = createDirectory();
		//4.创建索引的读取对象
		IndexReader indexReader = createIndexReader(dir);
		//5.创建索引的搜索对象
		IndexSearcher indexSearcher = createIndexSearcher(indexReader);
		//搜索:第一个参数为查询语句对象, 第二个参数:指定显示多少条
		TopDocs topdocs = indexSearcher.search(query, 2);
		//一共搜索到多少条记录
		System.out.println("=====count=====" + topdocs.totalHits);
		//从搜索结果对象中获取结果集
		ScoreDoc[] scoreDocs = topdocs.scoreDocs;
		for(ScoreDoc scoreDoc : scoreDocs){
			//获取docID
			int docID = scoreDoc.doc;
			//通过文档ID从硬盘中读取出对应的文档
			Document document = indexReader.document(docID);
			//get域名可以取出值 打印
			System.out.println("fileName:" + document.get("fileName"));
			System.out.println("fileSize:" + document.get("fileSize"));
			System.out.println("fileContext:" + document.get("fileContext"));
			System.out.println("============================================================");
		}
	}

	/**
	 * 创建索引的搜索对象
	 */
	public static IndexSearcher createIndexSearcher(IndexReader indexReader) {
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		return indexSearcher;
	}
	
	/**
	 * 创建分词器,StandardAnalyzer标准分词器,标准分词器对英文分词效果很好,对中文是单字分词
	 */
	public static Analyzer createAnalyzer(){
		Analyzer analyzer = new StandardAnalyzer();
		return analyzer;
	}
	
	/**
	 * 创建查询对象,第一个参数:默认搜索域, 第二个参数:分词器
	 * 默认搜索域作用:如果搜索语法中指定了域名,从指定域中搜索,如果搜索时只写了查询关键字,则从默认搜索域中进行搜索
	 */
	public static Query createQuery(Analyzer analyzer) throws Exception{
		QueryParser queryParser = new QueryParser("fileContext", analyzer);
		//查询语法=域名:搜索的关键字
		Query query = queryParser.parse("fileContext:recommended");
		return query;
	}
	
	/**
	 * 指定索引的目录
	 */
	public static Directory createDirectory() throws Exception{
		Directory directory = FSDirectory.open(new File(indexPath));
		return directory;
	}
	
	
	/**
	 * 索引的读取对象
	 */
	public static IndexReader createIndexReader(Directory dir) throws Exception{
		IndexReader indexReader = IndexReader.open(dir);
		return indexReader;
	}

}
