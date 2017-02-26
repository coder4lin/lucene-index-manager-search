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
		//�����ĵ��б�,������Document
		List<Document> docList = new ArrayList<Document>();
		
		//ָ���ļ�����Ŀ¼
		File dir = new File(dirPath); 
		
		for(File file : dir.listFiles()){
			//�ļ�����
			String fileName = file.getName();
			//�ļ�����
			String fileContext = FileUtils.readFileToString(file);
			//�ļ���С
			Long fileSize = FileUtils.sizeOf(file);
			
			//�ĵ�����,�ļ�ϵͳ�е�һ���ļ�����һ��Document����
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
			
			//�����ִ���,StandardAnalyzer��׼�ִ���,��׼�ִ�����Ӣ�ķִ�Ч���ܺ�,�������ǵ��ִַ�
			Analyzer analyzer = new StandardAnalyzer();
			//ָ���������ĵ��洢��Ŀ¼
			Directory directory = FSDirectory.open(new File(indexPath));
			//����д����ĳ�ʼ������
			IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
			//�����������ĵ�д����
			IndexWriter indexWriter = new IndexWriter(directory, config);
			
			//���ĵ����뵽�������ĵ���д������
			for(Document doc1 : docList){
				indexWriter.addDocument(doc1);
			}
			//�ύ
			indexWriter.commit();
			//�ر���
			indexWriter.close();
		}
	}
}
