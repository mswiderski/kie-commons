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

package org.kie.kieora.backend.lucene.metamodels;

import java.io.File;
import java.util.concurrent.ConcurrentMap;

import org.kie.kieora.engine.MetaModelStore;
import org.kie.kieora.model.schema.MetaObject;
import org.mapdb.DB;
import org.mapdb.DBMaker;

/**
 *
 */
public class MapDbMetaModelStore implements MetaModelStore {

    private final ConcurrentMap<String, MetaObject> metaModel;
    private final DB                                db;

    public MapDbMetaModelStore( final File dir ) {
        this.db = DBMaker
                .newFileDB( new File( "testdb" ) )
                .closeOnJvmShutdown()
                .make();
        this.metaModel = db.getHashMap( "metaModel" );
    }

    @Override
    public void add( final MetaObject metaObject ) {
        metaModel.put( metaObject.getType().getName(), metaObject );
        db.commit();
    }

    @Override
    public void update( final MetaObject metaObject ) {
        metaModel.put( metaObject.getType().getName(), metaObject );
        db.commit();
    }

    @Override
    public MetaObject getMetaObject( final String type ) {
        return metaModel.get( type );
    }

    @Override
    public void dispose() {
        db.close();
    }
}
