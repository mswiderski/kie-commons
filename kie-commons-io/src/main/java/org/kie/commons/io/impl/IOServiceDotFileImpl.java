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

package org.kie.commons.io.impl;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.AbstractBasicFileAttributeView;
import org.kie.commons.java.nio.base.AttrHolder;
import org.kie.commons.java.nio.base.NeedsPreloadedAttrs;
import org.kie.commons.java.nio.base.Properties;
import org.kie.commons.java.nio.base.dotfiles.DotFileOption;
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
import org.kie.commons.java.nio.file.attribute.FileAttributeView;

import static org.kie.commons.java.nio.base.dotfiles.DotFileUtils.*;
import static org.kie.commons.java.nio.file.StandardCopyOption.*;
import static org.kie.commons.validation.Preconditions.*;

public class IOServiceDotFileImpl
        extends AbstractIOService
        implements IOService {

    @Override
    public synchronized void delete( final Path path )
            throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException,
            IOException, SecurityException {
        Files.delete( path );
        Files.deleteIfExists( dot( path ) );
        if ( path instanceof AttrHolder ) {
            ( (AttrHolder) path ).getAttrStorage().clear();
        }
    }

    @Override
    public synchronized boolean deleteIfExists( final Path path )
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        final boolean result = Files.deleteIfExists( path );
        Files.deleteIfExists( dot( path ) );
        if ( path instanceof AttrHolder ) {
            ( (AttrHolder) path ).getAttrStorage().clear();
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

        return result;
    }

    @Override
    public synchronized Path createDirectory( final Path dir,
                                              final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        return internalCreateDirectory( dir, false, attrs );
    }

    @Override
    public synchronized Path createDirectories( final Path dir,
                                                final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        final Path result = Files.createDirectories( dir, attrs );

        buildDotFile( dir, newOutputStream( dot( dir ) ), attrs );

        return result;
    }

    @Override
    public synchronized Path copy( final Path source,
                                   final Path target,
                                   final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException, SecurityException {
        final Path result = Files.copy( source, target, buildOptions( options ) );

        if ( Files.exists( dot( source ) ) ) {
            Files.copy( dot( source ), dot( target ), forceBuildOptions( options ) );
        }

        return result;
    }

    @Override
    public synchronized Path move( final Path source,
                                   final Path target,
                                   final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, AtomicMoveNotSupportedException, IOException, SecurityException {
        final Path result = Files.move( source, target, options );

        if ( Files.exists( dot( source ) ) ) {
            Files.move( dot( source ), dot( target ), forceBuildOptions( options ) );
        }

        return result;
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView( final Path path,
                                                                 final Class<V> type )
            throws IllegalArgumentException {

        final V value = Files.getFileAttributeView( path, type );

        if ( value == null && path instanceof AttrHolder ) {
            final AttrHolder holder = ( (AttrHolder) path );
            final V holderView = holder.getAttrView( type );
            if ( holderView == null && AbstractBasicFileAttributeView.class.isAssignableFrom( type ) ) {
                return (V) newView( holder, (Class<? extends AbstractBasicFileAttributeView>) type );
            }
            return holderView;
        }

        return value;
    }

    @Override
    public Map<String, Object> readAttributes( final Path path,
                                               final String attributes )
            throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException,
            IOException, SecurityException {
        checkNotNull( "path", path );
        checkNotEmpty( "attributes", attributes );

        final Properties original = new Properties( Files.readAttributes( path, attributes ) );
        if ( attributes.equals( "*" ) && exists( dot( path ) ) ) {
            boolean isAttrHolder = path instanceof AttrHolder;
            if ( isAttrHolder && ( (AttrHolder) path ).getAttrStorage().getContent().size() > 0 ) {
                return ( (AttrHolder) path ).getAttrStorage().getAllContent();
            }
            final Properties content = new Properties();
            content.load( newInputStream( dot( path ) ) );
            content.putAll( original );

            if ( isAttrHolder ) {
                ( (AttrHolder) path ).getAttrStorage().loadContent( content );
            }
            return content;
        }

        return original;
    }

    @Override
    public Path setAttribute( final Path path,
                              final String attribute,
                              final Object value )
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        return setAttributes( path, new FileAttribute<Object>() {
            @Override
            public String name() {
                return attribute;
            }

            @Override
            public Object value() {
                return value;
            }
        } );
    }

    @Override
    public synchronized Path setAttributes( final Path path,
                                            final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        checkNotNull( "path", path );
        if ( Files.isDirectory( path ) ) {
            return internalCreateDirectory( path, true, attrs );
        }
        return write( path, readAllBytes( path ), Collections.<OpenOption>emptySet(), attrs );
    }

    @Override
    public Object getAttribute( final Path path,
                                final String attribute )
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        checkNotNull( "path", path );

        Object value;
        try {
            value = Files.getAttribute( path, attribute );
        } catch ( UnsupportedOperationException ex ) {
            value = null;
        }

        if ( value == null && path instanceof AttrHolder ) {
            final AttrHolder holder = ( (AttrHolder) path );
            final String[] attr = split( attribute );
            if ( holder.getAttrStorage().getContent().isEmpty() ) {
                loadDotFile( path );
            }
            return holder.getAttrStorage().getAllContent().get( attr[ 1 ] );
        }

        return value;
    }

    @Override
    protected Set<? extends OpenOption> buildOptions( final Set<? extends OpenOption> options ) {
        return new HashSet<OpenOption>( options ) {{
            add( new DotFileOption() );
        }};
    }

    protected CopyOption[] buildOptions( final CopyOption... options ) {
        final CopyOption[] result = new CopyOption[ options.length + 1 ];
        System.arraycopy( options, 0, result, 0, options.length );
        result[ result.length - 1 ] = new DotFileOption();
        return result;
    }

    protected CopyOption[] forceBuildOptions( final CopyOption[] options ) {
        final CopyOption[] result = new CopyOption[ options.length + 1 ];
        System.arraycopy( options, 0, result, 0, options.length );
        result[ result.length - 1 ] = REPLACE_EXISTING;
        return result;
    }

    protected boolean isFileScheme( final Path path ) {
        if ( path == null || path.getFileSystem() == null || path.getFileSystem().provider() == null ) {
            return false;
        }

        return path.getFileSystem().provider().getScheme().equals( "file" );
    }

    protected void loadDotFile( final Path path ) {
        final Properties content = new Properties();
        content.load( newInputStream( dot( path ) ) );

        if ( path instanceof AttrHolder ) {
            ( (AttrHolder) path ).getAttrStorage().loadContent( content );
        }
    }

    protected <V extends AbstractBasicFileAttributeView> V newView( final AttrHolder holder,
                                                                    final Class<V> type ) {
        if ( NeedsPreloadedAttrs.class.isAssignableFrom( type ) ) {
            readAttributes( (Path) holder );
        }
        try {
            final Constructor<V> constructor = (Constructor<V>) type.getConstructors()[ 0 ];
            final V view = constructor.newInstance( holder );
            holder.addAttrView( view );
            return view;
        } catch ( final Exception e ) {
        }
        return null;
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

        buildDotFile( dir, newOutputStream( dot( dir ) ), allAttrs );

        return dir;
    }

    protected String[] split( final String attribute ) {
        final String[] s = new String[ 2 ];
        final int pos = attribute.indexOf( ':' );
        if ( pos == -1 ) {
            s[ 0 ] = "basic";
            s[ 1 ] = attribute;
        } else {
            s[ 0 ] = attribute.substring( 0, pos );
            s[ 1 ] = ( pos == attribute.length() ) ? "" : attribute.substring( pos + 1 );
        }
        return s;
    }
}
