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

package org.kie.kieora.io;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Set;

import org.kie.commons.io.impl.IOServiceDotFileImpl;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.Properties;
import org.kie.commons.java.nio.channels.SeekableByteChannel;
import org.kie.commons.java.nio.file.AtomicMoveNotSupportedException;
import org.kie.commons.java.nio.file.CopyOption;
import org.kie.commons.java.nio.file.DirectoryNotEmptyException;
import org.kie.commons.java.nio.file.FileAlreadyExistsException;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.commons.java.nio.file.OpenOption;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.attribute.FileAttribute;
import org.kie.kieora.engine.MetaIndexEngine;
import org.kie.kieora.model.KObject;
import org.kie.kieora.model.KObjectKey;
import org.kie.kieora.model.KProperty;
import org.kie.kieora.model.schema.MetaType;

import static org.kie.commons.java.nio.base.dotfiles.DotFileUtils.*;
import static org.kie.commons.validation.Preconditions.*;

public class IOServiceIndexedImpl extends IOServiceDotFileImpl {

    private static final MessageDigest DIGEST;

    static {
        try {
            DIGEST = MessageDigest.getInstance( "SHA1" );
        } catch ( final NoSuchAlgorithmException e ) {
            throw new RuntimeException( e );
        }
    }

    private static final MetaType META_TYPE = new MetaType() {
        @Override
        public String getName() {
            return Path.class.getName();
        }
    };

    private final MetaIndexEngine indexEngine;

    public IOServiceIndexedImpl( final MetaIndexEngine indexEngine ) {
        this.indexEngine = checkNotNull( "indexEngine", indexEngine );
    }

    @Override
    public synchronized void delete( final Path path )
            throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException,
            IOException, SecurityException {
        super.delete( path );
        indexEngine.delete( toKObjectKey( path ) );
    }

    @Override
    public synchronized boolean deleteIfExists( final Path path )
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        final boolean result = super.deleteIfExists( path );
        if ( result ) {
            indexEngine.delete( toKObjectKey( path ) );
        }
        return result;
    }

    @Override
    public synchronized SeekableByteChannel newByteChannel( final Path path,
                                                            final Set<? extends OpenOption> options,
                                                            final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        checkNotNull( "path", path );

        final Properties properties = new Properties();
        if ( exists( dot( path ) ) ) {
            properties.load( newInputStream( dot( path ) ) );
        }
        final FileAttribute<?>[] allAttrs = consolidate( properties, attrs );

        final SeekableByteChannel result = Files.newByteChannel( path, buildOptions( options ), allAttrs );

        if ( isFileScheme( path ) ) {
            buildDotFile( path, newOutputStream( dot( path ) ), allAttrs );
        }

        indexEngine.index( toKObject( path, allAttrs ) );

        return result;
    }

    @Override
    public synchronized Path createDirectories( final Path dir,
                                                final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        final Path result = super.createDirectories( dir, attrs );

        final Properties properties = new Properties();
        if ( exists( dot( dir ) ) ) {
            properties.load( newInputStream( dot( dir ) ) );
        }
        final FileAttribute<?>[] allAttrs = consolidate( properties, attrs );

        indexEngine.index( toKObject( dir, allAttrs ) );

        return result;
    }

    @Override
    public synchronized Path copy( final Path source,
                                   final Path target,
                                   final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException, SecurityException {
        final Path result = super.copy( source, target, options );

        final Properties properties = new Properties();
        if ( exists( dot( target ) ) ) {
            properties.load( newInputStream( dot( target ) ) );
        }

        indexEngine.index( toKObject( target, convert( properties ) ) );

        return result;
    }

    @Override
    public synchronized Path move( final Path source,
                                   final Path target,
                                   final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        final Path result = super.move( source, target, options );

        indexEngine.rename( toKObjectKey( source ), toKObjectKey( target ) );

        return result;
    }

    protected synchronized Path internalCreateDirectory( final Path dir,
                                                         final boolean skipAlreadyExistsException,
                                                         final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        checkNotNull( "dir", dir );

        FileAttribute<?>[] allAttrs = attrs;
        try {
            Files.createDirectory( dir, attrs );
        } catch ( final FileAlreadyExistsException ex ) {
            final Properties properties = new Properties();
            if ( exists( dot( dir ) ) ) {
                properties.load( newInputStream( dot( dir ) ) );
            }
            allAttrs = consolidate( properties, attrs );
            if ( !skipAlreadyExistsException ) {
                throw ex;
            }
        }

        indexEngine.index( toKObject( dir, allAttrs ) );

        buildDotFile( dir, newOutputStream( dot( dir ) ), allAttrs );

        return dir;
    }

    private KObjectKey toKObjectKey( final Path path ) {
        return new KObjectKey() {
            @Override
            public String getId() {
                return sha1( getType().getName() + "|" + getKey() );
            }

            @Override
            public MetaType getType() {
                return META_TYPE;
            }

            @Override
            public String getKey() {
                return path.toString();
            }
        };
    }

    private KObject toKObject( final Path path,
                               final FileAttribute<?>... attrs ) {
        return new KObject() {
            @Override
            public String getId() {
                return sha1( getType().getName() + "|" + getKey() );
            }

            @Override
            public MetaType getType() {
                return META_TYPE;
            }

            @Override
            public String getKey() {
                return path.toString();
            }

            @Override
            public Iterable<KProperty<?>> getProperties() {
                return new ArrayList<KProperty<?>>( attrs.length ) {{
                    for ( final FileAttribute<?> attr : attrs ) {
                        add( new KProperty<Object>() {
                            @Override
                            public String getName() {
                                return attr.name();
                            }

                            @Override
                            public Object getValue() {
                                return attr.value();
                            }

                            @Override
                            public boolean isSearchable() {
                                return true;
                            }
                        } );
                    }
                }};
            }
        };
    }

    private static String sha1( final String input ) {
        byte[] result = DIGEST.digest( input.getBytes() );
        final StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < result.length; i++ ) {
            sb.append( Integer.toString( ( result[ i ] & 0xff ) + 0x100, 16 ).substring( 1 ) );
        }

        return sb.toString();
    }

}
