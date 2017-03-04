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
		//1.�����ִ���(����������ʹ��ʱ���õķִ�������һ��)
		Analyzer analyzer = createAnalyzer();
		//2.������ѯ����
		Query query = createQuery(analyzer);
		//3.ָ��������Ŀ¼
		Directory dir = createDirectory();
		//4.���������Ķ�ȡ����
		IndexReader indexReader = createIndexReader(dir);
		//5.������������������
		IndexSearcher indexSearcher = createIndexSearcher(indexReader);
		//����:��һ������Ϊ��ѯ������, �ڶ�������:ָ����ʾ������
		TopDocs topdocs = indexSearcher.search(query, 2);
		//һ����������������¼
		System.out.println("=====count=====" + topdocs.totalHits);
		//��������������л�ȡ�����
		ScoreDoc[] scoreDocs = topdocs.scoreDocs;
		for(ScoreDoc scoreDoc : scoreDocs){
			//��ȡdocID
			int docID = scoreDoc.doc;
			//ͨ���ĵ�ID��Ӳ���ж�ȡ����Ӧ���ĵ�
			Document document = indexReader.document(docID);
			//get��������ȡ��ֵ ��ӡ
			System.out.println("fileName:" + document.get("fileName"));
			System.out.println("fileSize:" + document.get("fileSize"));
			System.out.println("fileContext:" + document.get("fileContext"));
			System.out.println("============================================================");
		}
	}

	/**
	 * ������������������
	 */
	public static IndexSearcher createIndexSearcher(IndexReader indexReader) {
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		return indexSearcher;
	}
	
	/**
	 * �����ִ���,StandardAnalyzer��׼�ִ���,��׼�ִ�����Ӣ�ķִ�Ч���ܺ�,�������ǵ��ִַ�
	 */
	public static Analyzer createAnalyzer(){
		Analyzer analyzer = new StandardAnalyzer();
		return analyzer;
	}
	
	/**
	 * ������ѯ����,��һ������:Ĭ��������, �ڶ�������:�ִ���
	 * Ĭ������������:��������﷨��ָ��������,��ָ����������,�������ʱֻд�˲�ѯ�ؼ���,���Ĭ���������н�������
	 */
	public static Query createQuery(Analyzer analyzer) throws Exception{
		QueryParser queryParser = new QueryParser("fileContext", analyzer);
		//��ѯ�﷨=����:�����Ĺؼ���
		Query query = queryParser.parse("fileContext:recommended");
		return query;
	}
	
	/**
	 * ָ��������Ŀ¼
	 */
	public static Directory createDirectory() throws Exception{
		Directory directory = FSDirectory.open(new File(indexPath));
		return directory;
	}
	
	
	/**
	 * �����Ķ�ȡ����
	 */
	public static IndexReader createIndexReader(Directory dir) throws Exception{
		IndexReader indexReader = IndexReader.open(dir);
		return indexReader;
	}

}
