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
		//1. �����ĵ��б�,����������Document
		List<Document> docList = createDocumentList();
		//2.��ȡԴ�ļ�����Ŀ¼
		File dir = getSourceFileDirectory(); 
		//3.����Դ�ļ�����Ŀ¼�µ������ļ�������ÿ�������ļ���װ��Document,����Document�б���
		docList = lookupFileIntoDocument(docList,dir);
		//4.�����ִ���Analyzer
		Analyzer analyzer = createAnalyzer();
		//5.ָ�������洢��Ŀ¼
		Directory directory = createDirectory();
		//6.��������д����
		IndexWriter indexWriter = createIndexWriter(analyzer,directory);
		//7.���ĵ���������д������
		addDocumentToIndexWriter(indexWriter,docList);
		//8.�ύ����д����
		indexWriterCommit(indexWriter);
		//9.�ر�����д����
		indexWriterClose(indexWriter);
	}
	
	/**
	 * �����ĵ��б�,������Document
	 */
	public static List<Document> createDocumentList(){
		List<Document> docList = new ArrayList<Document>();
		return docList;
	}
	
	/**
	 * ��ȡԴ�ļ�����Ŀ¼
	 */
	public static File getSourceFileDirectory(){
		File dir = new File(dirPath); 
		return dir;
	}
	
	/**
	 * ����Դ�ļ�����Ŀ¼�µ������ļ�������ÿ�������ļ���װ��Document,����Document�б���
	 */
	public static List<Document> lookupFileIntoDocument(final List<Document> docList,File dir) throws Exception{
		for(File file : dir.listFiles()){
			String fileName = file.getName();
			String fileContext = FileUtils.readFileToString(file);
			Long fileSize = FileUtils.sizeOf(file);
			Document doc = new Document();
			//��һ������:����
			//�ڶ�������:��ֵ
			//����������:�Ƿ�洢,��Ϊyes,���洢Ϊno
			TextField nameFiled = new TextField("fileName", fileName, Store.YES);
			TextField contextFiled = new TextField("fileContext", fileContext, Store.YES);
			TextField sizeFiled = new TextField("fileSize", fileSize.toString(), Store.YES);
			//�����е��򶼴����ĵ���
			doc.add(nameFiled);
			doc.add(contextFiled);
			doc.add(sizeFiled);
			docList.add(doc);
		}
		return docList;
	}
	
	/**
	 * �����ִ���,StandardAnalyzer��׼�ִ���,��׼�ִ�����Ӣ�ķִ�Ч���ܺ�,�������ǵ��ִַ�
	 */
	public static Analyzer createAnalyzer(){
		Analyzer analyzer = new StandardAnalyzer();
		return analyzer;
	}
	
	/**
	 * ָ���������ĵ��洢��Ŀ¼
	 */
	public static Directory createDirectory() throws Exception{
		Directory directory = FSDirectory.open(new File(indexPath));
		return directory;
	}
	
	/**
	 * ��������д����
	 */
	public static IndexWriter createIndexWriter(Analyzer analyzer,Directory directory) throws Exception{
		//�����������ĵ���д����ĳ�ʼ������
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
		//�����������ĵ���д����
		IndexWriter indexWriter = new IndexWriter(directory, config);
		return indexWriter;
	}
	
	/**
	 * ���ĵ����뵽����д������
	 */
	public static void addDocumentToIndexWriter(IndexWriter indexWriter,List<Document> docList) throws Exception{
		for(Document doc1 : docList){
			indexWriter.addDocument(doc1);
		}
	}
	
	/**
	 * ����д������ύ
	 */
	public static void indexWriterCommit(IndexWriter indexWriter) throws Exception{
		indexWriter.commit();
	}
	
	/**
	 * ����д����Ĺر�
	 */
	public static void indexWriterClose(IndexWriter indexWriter) throws Exception{
		indexWriter.close();
	}
}
