package com.yatin.tcd.lucene_cranfield;

import com.yatin.tcd.parser.*;
import com.yatin.tcd.utils.*;
import com.yatin.tcd.customAnalyzer.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Indexer {

    private static Logger logger = LoggerFactory.getLogger(Indexer.class);
    
/* Method to create Index of documents 
 * Takes in document path , analyzer, similarity as parameters
 * Returns True or False based on success or failure
 */
    public static boolean createIndex(String docPath, Analyzers analyserChoice, Similarities similarityChoice) {

        Analyzer analyzer = getAnalyzer(analyserChoice);
        Similarity similarity = getSimilarity(similarityChoice);

        try{
            assert analyzer != null;
            logger.debug("Analyzer: " + analyzer.toString() + " Similarity: " + similarity.toString());
            Directory directory = FSDirectory.open(Paths.get(Constants.INDEX_PATH));
            IndexWriterConfig iConfig = new IndexWriterConfig(analyzer);
            iConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            iConfig.setSimilarity(similarity);
            IndexWriter indexWriter = new IndexWriter(directory, iConfig);

            // parse and get the list of documents
            ArrayList<Document> documents = Parser.parse(docPath);
            indexWriter.addDocuments(documents);
            indexWriter.close();
            logger.info("Finished indexing " + documents.size() + " documents");
            logger.info("Index created at " + Constants.INDEX_PATH);

        }catch (IOException ioe){
            logger.error("Error while indexing", ioe);
        }
        return true;
    }

    public enum Analyzers {
        WHITESPACE("whitespace"), SIMPLE("simple"),
        STANDARD("standard"), ENGLISH("english"), CUSTOM("custom");
        public String type;
        private Analyzers(String type){this.type = type;}
        public String getType() {return type;}
        public static Analyzers fromName(String name) {
            for (Analyzers a : values()) {
                if (a.getType().equalsIgnoreCase(name))
                    return a;
            }
            return null;
        }
    }

    public static Analyzer getAnalyzer(Analyzers choice) {
        switch (choice) {
            case SIMPLE: return new SimpleAnalyzer();
            case STANDARD: return new StandardAnalyzer();
            case WHITESPACE: return new WhitespaceAnalyzer();
            case ENGLISH: return new EnglishAnalyzer();
            case CUSTOM: return new CustomAnalyzer();
        }
        return new EnglishAnalyzer();
    }

    public enum Similarities {
        CLASSIC("classic"), BOOLEAN("boolean"), BM25("bm25"), LMDS("lmds");
        public String type;
        private Similarities(String type){this.type = type;}
        public String getType() {return type;}
        public static Similarities fromName(String name) {
            for (Similarities s : values()) {
                if (s.getType().equalsIgnoreCase(name))
                    return s;
            }
            return null;
        }
    }

    public static Similarity getSimilarity(Similarities choice) {
        switch (choice) {
            case CLASSIC: return new ClassicSimilarity();
            case BOOLEAN: return new BooleanSimilarity();
            case BM25: return new BM25Similarity();
            case LMDS: return new LMDirichletSimilarity();
        }
        return new BM25Similarity();
    }

}
