package com.yatin.tcd.parser;

import com.yatin.tcd.datamodels.*;
import com.yatin.tcd.utils.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Parser {

    private static Logger logger = LoggerFactory.getLogger(Parser.class);

    private static ArrayList<Document> cranDocuments = new ArrayList<Document>();
    private static ArrayList<SearchQuery> queries = new ArrayList<SearchQuery>();


    /* Method to parse documents 
     * Takes in document path as parameter
     * Returns an ArrayList of parsed Documents 
     */
    public static ArrayList<Document> parse(String docPath) throws IOException {
        try {
            List<String> fileData = Files.readAllLines(Paths.get(docPath), StandardCharsets.UTF_8);

            String text = "";
            CranfieldDocument cranDocument = null;
            char fieldToAdd = Character.MIN_VALUE ;

            for (String line : fileData) {
                if (line.trim().length() > 0 && line.charAt(0) == Constants.DOT) {
                    if (fieldToAdd != Character.MIN_VALUE) {
                        switch (fieldToAdd) {
                            case Constants.Title: cranDocument.title = text; break;
                            case Constants.Author: cranDocument.author = text; break;
                            case Constants.Biblio: cranDocument.biblio = text; break;
                            case Constants.Words: cranDocument.words = text;break;
                            default: break;
                        }
                    }
                    text = "";
                    char field = line.charAt(1);
                    switch (field) {
                        case Constants.Index:
                            if (cranDocument != null)
                                cranDocuments.add(createLuceneDocument(cranDocument));
                            cranDocument = new CranfieldDocument();
                            cranDocument.docid = line.substring(3);
                            break;
                        case Constants.Title: 
                        case Constants.Author:
                        case Constants.Biblio:
                        case Constants.Words:
                        default: fieldToAdd = field;break;
                    }
                } else
                    text += line + Constants.SPACE;
            }
            if (cranDocument != null) {
                cranDocument.words = text;
                cranDocuments.add(createLuceneDocument(cranDocument));
            }
        } catch (IOException ioe) {
            logger.error("Error while parsing document", ioe);
        }
        return cranDocuments;
    }



    /* Method to parse queries 
     * Takes in query path as parameter
     * Returns an ArrayList of parsed Queries 
     */
    public static ArrayList<SearchQuery> parseQuery(String queryPath) throws IOException {
        try {
            List<String> fileData = Files.readAllLines(Paths.get(queryPath), StandardCharsets.UTF_8);

            String text = "";
            SearchQuery searchQuery = null;
            char fieldToAdd = Character.MIN_VALUE;

            for (String line : fileData) {
                if (line.trim().length() > 0 && line.charAt(0) == Constants.DOT) {
                    if (fieldToAdd != Character.MIN_VALUE) {
                    	searchQuery.query = text;
                    }
                    text = "";
                    char field = line.charAt(1);
                    switch (field) {
                        case Constants.Index:
                            if (searchQuery != null)
                                queries.add(searchQuery);
                            searchQuery = new SearchQuery();
                            searchQuery.queryid = line.substring(3);
                            break;
                        case Constants.Words:
                            fieldToAdd = field; break;
                        default: break;
                    }
                } else
                    text += line + Constants.SPACE;
            }
            if (searchQuery != null) {
            	searchQuery.query = text;
                queries.add(searchQuery);
            }
        } catch (IOException ioe) {
            logger.error("Error while parsing query", ioe);
        }
        return queries;
    }


    /* Method to create LuceneDocument from CranfieldDocument object 
     * Takes in CranfieldDocument object  as parameter
     * Returns a Lucene Document object 
     */
    private static Document createLuceneDocument(CranfieldDocument doc) {
        Document document = new Document();
        document.add(new StringField("docid", doc.docid, Field.Store.YES));
        document.add(new TextField("title", doc.title, Field.Store.YES));
        document.add(new TextField("author", doc.author, Field.Store.YES));
        document.add(new TextField("biblio", doc.biblio, Field.Store.YES));
        document.add(new TextField("words", doc.words, Field.Store.YES));
        return document;
    }
}

