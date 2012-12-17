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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributeView;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;
import org.kie.commons.java.nio.file.attribute.FileTime;

import static org.kie.commons.validation.Preconditions.*;

public abstract class AbstractBasicFileAttributeView<P extends Path>
        implements BasicFileAttributeView,
                   ExtendedAttributeView {

    private static final String IS_REGULAR_FILE    = "isRegularFile";
    private static final String IS_DIRECTORY       = "isDirectory";
    private static final String IS_SYMBOLIC_LINK   = "isSymbolicLink";
    private static final String IS_OTHER           = "isOther";
    private static final String SIZE               = "size";
    private static final String FILE_KEY           = "fileKey";
    private static final String LAST_MODIFIED_TIME = "lastModifiedTime";
    private static final String LAST_ACCESS_TIME   = "lastAccessTime";
    private static final String CREATION_TIME      = "creationTime";

    private static final Set<String> PROPERTIES = new HashSet<String>() {{
        add( IS_REGULAR_FILE );
        add( IS_DIRECTORY );
        add( IS_SYMBOLIC_LINK );
        add( IS_OTHER );
        add( SIZE );
        add( FILE_KEY );
        add( LAST_MODIFIED_TIME );
        add( LAST_ACCESS_TIME );
        add( CREATION_TIME );
    }};

    protected final P path;

    public AbstractBasicFileAttributeView( final P path ) {
        this.path = checkNotNull( "path", path );
    }

    @Override
    public String name() {
        return "basic";
    }

    @Override
    public void setTimes( final FileTime lastModifiedTime,
                          final FileTime lastAccessTime,
                          final FileTime createTime )
            throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public Map<String, Object> readAllAttributes() {
        final BasicFileAttributes attrs = readAttributes();
        return new HashMap<String, Object>() {{
            put( IS_REGULAR_FILE, attrs.isRegularFile() );
            put( IS_DIRECTORY, attrs.isDirectory() );
            put( IS_SYMBOLIC_LINK, attrs.isSymbolicLink() );
            put( IS_OTHER, attrs.isOther() );
            put( SIZE, attrs.size() );
            put( FILE_KEY, attrs.fileKey() );
            //todo check why errai can't serialize it
            put( LAST_MODIFIED_TIME, null );
            put( LAST_ACCESS_TIME, null );
            put( CREATION_TIME, null );
        }};
    }

    @Override
    public Map<String, Object> readAttributes( final String... attributes ) {
        final BasicFileAttributes attrs = readAttributes();
        return new HashMap<String, Object>() {{
            for ( final String attribute : attributes ) {
                checkNotEmpty( "attribute", attribute );
                if ( attribute.equals( "*" ) ) {
                    putAll( readAllAttributes() );
                    break;
                } else if ( attribute.equals( IS_REGULAR_FILE ) ) {
                    put( IS_REGULAR_FILE, attrs.isRegularFile() );
                } else if ( attribute.equals( IS_DIRECTORY ) ) {
                    put( IS_DIRECTORY, attrs.isDirectory() );
                } else if ( attribute.equals( IS_SYMBOLIC_LINK ) ) {
                    put( IS_SYMBOLIC_LINK, attrs.isSymbolicLink() );
                } else if ( attribute.equals( IS_OTHER ) ) {
                    put( IS_OTHER, attrs.isOther() );
                } else if ( attribute.equals( SIZE ) ) {
                    put( SIZE, new Long( attrs.size() ) );
                } else if ( attribute.equals( FILE_KEY ) ) {
                    put( FILE_KEY, attrs.fileKey() );
                } else if ( attribute.equals( LAST_MODIFIED_TIME ) ) {
                    put( LAST_MODIFIED_TIME, null );
                } else if ( attribute.equals( LAST_ACCESS_TIME ) ) {
                    put( LAST_ACCESS_TIME, null );
                } else if ( attribute.equals( CREATION_TIME ) ) {
                    put( CREATION_TIME, null );
                }
            }
        }};
    }

    @Override
    public void setAttribute( final String attribute,
                              final Object value ) throws IOException {
        checkNotEmpty( "attribute", attribute );
        checkCondition( "invalid attribute", PROPERTIES.contains( attribute ) );

        throw new NotImplementedException();
    }

    @Override
    public boolean isSerializable() {
        return false;
    }

}
