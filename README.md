# Lucene-SearchEngine-for-Cranfield-Collection

This is an information retrieval system implemented using Lucene Core, a Java library providing powerful indexing and search features. The system indexes the documents in the Cranfield collection and allows users to search for relevant documents based on their queries.

## Implementation

The system consists of three main components: a parser, an indexer, and a searcher.

### Parsing

The parser reads in the Cranfield dataset and separates the documents into individual files before indexing.

### Indexing

The indexer creates a searchable index of all the documents in the corpus. Users can select from various analyzers to preprocess the documents before indexing. By default, the system uses a custom analyzer which includes tokenization, stop word removal, stemming, and normalization.

### Searching

The searcher takes user queries as input and applies the same preprocessing operations on them as applied on the documents during indexing. The system uses a boost map to selectively boost the importance of fields in the documents. The search results include the document and its relevance score.

## Evaluation and Results

The system was evaluated using trec eval, a standard tool used by the TREC community for evaluating the retrieved results. The mean average precision (MAP) scores for different analyzers and using different scoring functions are shown below:

| Similarity/Analyzer | Standard | English | Whitespace | Custom |
| --- | --- | --- | --- | --- |
| BM25 | 0.4073 | 0.4322 | 0.3641 | 0.4390 |
| CLASSIC (VSM) | 0.3478 | 0.4101 | 0.3133 | 0.4155 |
| BOOLEAN | 0.2559 | 0.3212 | 0.2310 | 0.3461 |
| LMDirichlet | 0.3268 | 0.3366 | 0.3040 | 0.3548 |

The system achieved the highest accuracy with the BM25 ranking function and the custom analyzer.

## References

- Lucene 8.5.0 API — lucene.apache.org, https://lucene.apache.org/core/8_5_0/core/index.html.
- Learn how to use trec eval to evaluate your information retrieval system - Rafael Glater — rafaelglater.com, http://www.rafaelglater.com/en/post/learn-how-to-use-trec_eval-to-evaluate-your-information-retrieval-system.
