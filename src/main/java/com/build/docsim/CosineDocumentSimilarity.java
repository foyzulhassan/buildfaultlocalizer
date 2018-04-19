package com.build.docsim;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FileUtils;
import org.apache.commons.math3.linear.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.*;

public class CosineDocumentSimilarity {

	public static final String CONTENT = "text";

	private final Set<String> terms = new HashSet<>();
	private final RealVector v1;
	private final RealVector v2;

	CosineDocumentSimilarity(String s1, String s2) throws IOException {
		// Directory directory = createIndex(s1, s2);
		// IndexReader reader = DirectoryReader.open(directory);
		RAMDirectory ramDir = new RAMDirectory();

		// Index the full text of both documents
		//CharArraySet stopword=
		List<String> stopWords = Arrays.asList(
			      "a", "an", "and", "are", "as", "at", "be", "but", "by",
			      "for", "if", "in", "into", "is", "it",
			      "no", "not", "of", "on", "or", "such",
			      "that", "the", "their", "then", "there", "these",
			      "they", "this", "to", "was", "will", "with", "java","build"
			    );
		
		CharArraySet stopSet = new CharArraySet(stopWords, false);
		Analyzer analyzer = new StandardAnalyzer(stopSet);
		
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
//		IndexWriter writer = new IndexWriter(ramDir, new StandardAnalyzer(Version.LUCENE_36), true,
//				IndexWriter.MaxFieldLength.UNLIMITED);
		IndexWriter writer = new IndexWriter(ramDir, iwc);
		Document doc = new Document();
		doc.add(new Field("text", FileUtils.readFileToString(new File(s1), "UTF-8").replace(".", " "), Field.Store.NO,
				Field.Index.ANALYZED, Field.TermVector.YES));
		writer.addDocument(doc);
		doc = new Document();
		doc.add(new Field("text", FileUtils.readFileToString(new File(s2), "UTF-8"), Field.Store.NO,
				Field.Index.ANALYZED, Field.TermVector.YES));
		writer.addDocument(doc);
		writer.close();

		IndexReader reader = DirectoryReader.open(ramDir);

		Map<String, Integer> f1 = getTermFrequencies(reader, 0);
		Map<String, Integer> f2 = getTermFrequencies(reader, 1);
		reader.close();
		v1 = toRealVector(f1);		
		v2 = toRealVector(f2);
	}


	double getCosineSimilarity() {
		return (v1.dotProduct(v2)) / (v1.getNorm() * v2.getNorm());
	}

	public static double getCosineSimilarity(String s1, String s2) throws IOException {
		return new CosineDocumentSimilarity(s1, s2).getCosineSimilarity();
	}

	Map<String, Integer> getTermFrequencies(IndexReader reader, int docId) throws IOException {
		Terms vector = reader.getTermVector(docId, CONTENT);
		TermsEnum termsEnum = null;
		termsEnum = vector.iterator(termsEnum);
		Map<String, Integer> frequencies = new HashMap<>();
		BytesRef text = null;
		while ((text = termsEnum.next()) != null) {
			String term = text.utf8ToString();
			int freq = (int) termsEnum.totalTermFreq();
			frequencies.put(term, freq);
			terms.add(term);
		}
		return frequencies;
	}

	RealVector toRealVector(Map<String, Integer> map) {
		RealVector vector = new ArrayRealVector(terms.size());
		int i = 0;
		for (String term : terms) {
			int value = map.containsKey(term) ? map.get(term) : 0;
			vector.setEntry(i++, value);
		}
		return (RealVector) vector.mapDivide(vector.getL1Norm());
	}
}