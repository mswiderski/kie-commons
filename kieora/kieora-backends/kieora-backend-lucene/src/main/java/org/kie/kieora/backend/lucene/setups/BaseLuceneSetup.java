/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kieora.backend.lucene.setups;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;
import org.kie.kieora.backend.lucene.LuceneSetup;

public abstract class BaseLuceneSetup implements LuceneSetup {

    @Override
    public void indexDocument( final String id,
                               final Document doc ) {
        try {
            deleteIfExists( id );
            writer().addDocument( doc );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void deleteIfExists( final String... docIds ) {
        final IndexSearcher searcher = nrtSearcher();
        try {
            final int[] answers = lookupDocIdByPK( searcher, docIds );
            for ( final int docId : answers ) {
                if ( docId != -1 ) {
                    writer().tryDeleteDocument( searcher.getIndexReader(), docId );
                }
            }
        } catch ( Exception ex ) {
        } finally {
            nrtRelease( searcher );
        }
    }

    @Override
    public void rename( final String sourceId,
                        final String targetId ) {
        final IndexSearcher searcher = nrtSearcher();
        try {
            int docId = lookupDocIdByPK( searcher, sourceId )[ 0 ];
            final Document source = searcher.getIndexReader().document( docId );

            source.removeField( "id" );
            source.add( new StringField( "id", targetId, Field.Store.YES ) );

            indexDocument( sourceId, source );
        } catch ( IOException ex ) {
        } finally {
            nrtRelease( searcher );
        }
    }

    protected int[] lookupDocIdByPK( final IndexSearcher searcher,
                                     final String... ids ) throws IOException {
        final List<AtomicReaderContext> subReaders = searcher.getIndexReader().leaves();
        final TermsEnum[] termsEnums = new TermsEnum[ subReaders.size() ];
        final DocsEnum[] docsEnums = new DocsEnum[ subReaders.size() ];
        for ( int subIDX = 0; subIDX < subReaders.size(); subIDX++ ) {
            termsEnums[ subIDX ] = subReaders.get( subIDX ).reader().fields().terms( "id" ).iterator( null );
        }

        int[] results = new int[ ids.length ];

        for ( int i = 0; i < results.length; i++ ) {
            results[ i ] = -1;
        }

        for ( int idx = 0; idx < ids.length; idx++ ) {
            int base = 0;
            final BytesRef id = new BytesRef( ids[ idx ] );
            for ( int subIDX = 0; subIDX < subReaders.size(); subIDX++ ) {
                final AtomicReader sub = subReaders.get( subIDX ).reader();
                final TermsEnum termsEnum = termsEnums[ subIDX ];
                if ( termsEnum.seekExact( id, false ) ) {
                    final DocsEnum docs = docsEnums[ subIDX ] = termsEnum.docs( sub.getLiveDocs(), docsEnums[ subIDX ], 0 );
                    if ( docs != null ) {
                        final int docID = docs.nextDoc();
                        if ( docID != DocIdSetIterator.NO_MORE_DOCS ) {
                            results[ idx ] = base + docID;
                            break;
                        }
                    }
                }
                base += sub.maxDoc();
            }
        }

        return results;
    }

    public abstract IndexWriter writer();

    public abstract IndexSearcher nrtSearcher();

    public abstract void nrtRelease( final IndexSearcher searcher );

}
