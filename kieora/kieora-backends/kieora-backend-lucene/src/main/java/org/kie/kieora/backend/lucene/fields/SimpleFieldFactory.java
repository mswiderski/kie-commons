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

package org.kie.kieora.backend.lucene.fields;

import java.util.Collection;
import java.util.Date;

import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FloatField;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.kie.kieora.backend.lucene.FieldFactory;
import org.kie.kieora.model.KProperty;

import static org.kie.kieora.backend.lucene.util.Serializer.*;

public class SimpleFieldFactory implements FieldFactory {

    @Override
    public IndexableField build( final KProperty<?> property ) {

        if ( property.getValue().getClass() == String.class ) {
            if ( property.isSearchable() ) {
                return new TextField( property.getName(), property.getValue().toString(), Field.Store.YES );
            }
            return new StringField( property.getName(), property.getValue().toString(), Field.Store.YES );
        }

        if ( property.getValue().getClass() == Integer.class ) {
            if ( property.isSearchable() ) {
                return new IntField( property.getName(), (Integer) property.getValue(), Field.Store.YES );
            }
            return new StoredField( property.getName(), (Integer) property.getValue() );
        }

        if ( property.getValue().getClass() == Long.class ) {
            if ( property.isSearchable() ) {
                return new LongField( property.getName(), (Long) property.getValue(), Field.Store.YES );
            }
            return new StoredField( property.getName(), (Long) property.getValue() );
        }

        if ( property.getValue().getClass() == Double.class ) {
            if ( property.isSearchable() ) {
                return new DoubleField( property.getName(), (Double) property.getValue(), Field.Store.YES );
            }
            return new StoredField( property.getName(), (Double) property.getValue() );
        }

        if ( property.getValue().getClass() == Float.class ) {
            if ( property.isSearchable() ) {
                return new FloatField( property.getName(), (Float) property.getValue(), Field.Store.YES );
            }
            return new StoredField( property.getName(), (Float) property.getValue() );
        }

        if ( Date.class.isAssignableFrom( property.getValue().getClass() ) ) {
            if ( property.isSearchable() ) {
                return new LongField( property.getName(), ( (Date) property.getValue() ).getTime(), Field.Store.YES );
            }
            return new StoredField( property.getName(), ( (Date) property.getValue() ).getTime() );
        }

        if ( Collection.class.isAssignableFrom( property.getValue().getClass() ) ) {
            final StringBuilder sb = new StringBuilder();
            for ( final java.lang.Object ovalue : (Collection) property.getValue() ) {
                sb.append( ovalue ).append( ' ' );
            }

            if ( property.isSearchable() ) {
                return new TextField( property.getName(), sb.toString(), Field.Store.YES );
            }
            return new StringField( property.getName(), sb.toString(), Field.Store.YES );
        }

        try {
            return new StoredField( property.getName(), toByteArray( property.getValue() ) );
        } catch ( final Exception ex ) {
            return new StoredField( property.getName(), property.getValue().toString() );
        }
    }

}
