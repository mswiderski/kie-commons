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

package org.kie.commons.java.nio.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kie.commons.java.nio.file.attribute.AttributeView;

public class AttrsStorageImpl implements AttrsStorage {

    private static final String DEFAULT_ATTR = "basic";

    final Map<String, AttributeView>   viewsNameIndex = new HashMap<String, AttributeView>();
    final Map<Class<?>, AttributeView> viewsTypeIndex = new HashMap<Class<?>, AttributeView>();

    @Override
    public AttrsStorage getAttrStorage() {
        return this;
    }

    @Override
    public <V extends AttributeView> void addAttrView( final V view ) {
        viewsNameIndex.put( view.name(), view );
        viewsTypeIndex.put( view.getClass(), view );
    }

    @Override
    public <V extends AttributeView> V getAttrView( final Class<V> type ) {
        return (V) viewsTypeIndex.get( type );
    }

    @Override
    public <V extends AttributeView> V getAttrView( final String name ) {
        return (V) viewsNameIndex.get( name );
    }

    @Override
    public void clear() {
        viewsNameIndex.clear();
        viewsTypeIndex.clear();
    }

    @Override
    public Properties toProperties() {
        final Properties properties = new Properties();

        for ( final Map.Entry<String, AttributeView> view : viewsNameIndex.entrySet() ) {
            if ( view.getValue() instanceof ExtendedAttributeView && ( (ExtendedAttributeView) view.getValue() ).isSerializable() ) {
                final ExtendedAttributeView extendedView = (ExtendedAttributeView) view.getValue();
                for ( final Map.Entry<String, Object> attr : extendedView.readAllAttributes().entrySet() ) {
                    //this maybe a problem!
                    properties.put( view.getKey() + "." + attr.getKey(), attr.getValue() );
                }
            }
        }

        return properties;
    }
}

