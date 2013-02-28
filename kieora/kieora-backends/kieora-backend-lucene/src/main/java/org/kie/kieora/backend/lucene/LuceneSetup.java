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

package org.kie.kieora.backend.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;

/**
 *
 */
public interface LuceneSetup {

    Analyzer getAnalyzer();

    void indexDocument( final String id,
                        final Document doc );

    void deleteIfExists( final String... ids );

    void rename( final String source,
                 final String target );

    IndexSearcher nrtSearcher();

    void nrtRelease( final IndexSearcher searcher );

    void dispose();

    boolean freshIndex();

    void commit();
}
