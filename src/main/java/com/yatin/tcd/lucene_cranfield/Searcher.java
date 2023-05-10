package com.yatin.tcd.lucene_cranfield;

import com.yatin.tcd.parser.*;
import com.yatin.tcd.utils.*;
import com.yatin.tcd.datamodels.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class Searcher {

    private static Logger logger = LoggerFactory.getLogger(Searcher.class);

    private static int NUM_RESULTS = 1;

    /**
     * Method to run queries on the indexer
     * Takes in querypath, number of results to produce, analyzer, similarity  
     */
    public static void runQueries(String queryPath, int numResults, Indexer.Analyzers analyserChoice,
                                  Indexer.Similarities similarityChoice) {
        try {
            Directory directory = FSDirectory.open(Paths.get(Constants.INDEX_PATH));
            DirectoryReader indexReader = DirectoryReader.open(directory);

            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(Indexer.getSimilarity(similarityChoice));
            Analyzer analyzer = Indexer.getAnalyzer(analyserChoice);

            File resultFile = new File(Constants.OUTPUT_FILE);
            resultFile.getParentFile().mkdirs();
            PrintWriter writer = new PrintWriter(resultFile, StandardCharsets.UTF_8.name());

            ArrayList<SearchQuery> queries = Parser.parseQuery(queryPath);

            HashMap<String, Float> boostMap = new HashMap<String, Float>();
            //selectively boost the importance of fields in the documents
            boostMap.put("title", 5f); 
            boostMap.put("author", 2f);
            boostMap.put("words", 10f);

            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                    new String[]{"title", "author", "words"}, analyzer, boostMap);

            NUM_RESULTS = numResults;
            logger.debug("Running search engine, max_hits set to: " + NUM_RESULTS);

            for (SearchQuery element : queries) {
                String queryString = QueryParser.escape(element.query.trim());
                Query query = queryParser.parse(queryString);
                search(indexSearcher, query, writer, queries.indexOf(element) + 1,similarityChoice);
            }

            indexReader.close();
            writer.close();
            directory.close();
            logger.info("Searching complete output written to " + Constants.OUTPUT_FILE);

        } catch (IOException exception) {
            logger.error("Error while running queries", exception);
        } catch (ParseException exception) {
            logger.error("Error while parsing query", exception);
        }
    }

    /**
     *This method searches for hits for every query and 
     *writes the releance score along with the queryID,docId in the results file
	  . 
     */
    public static void search(IndexSearcher indexSearcher, Query query, PrintWriter writer, int queryID, Indexer.Similarities similarityChoice) throws IOException{
        ScoreDoc[] hits = indexSearcher.search(query, NUM_RESULTS).scoreDocs;
        for (int i = 0; i < hits.length; i++) {
            Document hitDocument = indexSearcher.doc(hits[i].doc);
            writer.println(queryID + " 0 " + hitDocument.get("docid") + " 0 " + hits[i].score + " "+similarityChoice.toString());
        }
	}

}
