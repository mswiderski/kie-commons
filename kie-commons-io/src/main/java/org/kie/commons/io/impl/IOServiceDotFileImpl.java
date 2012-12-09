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

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.kie.commons.io.IOService;
import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.channels.SeekableByteChannel;
import org.kie.commons.java.nio.file.AtomicMoveNotSupportedException;
import org.kie.commons.java.nio.file.CopyOption;
import org.kie.commons.java.nio.file.DirectoryNotEmptyException;
import org.kie.commons.java.nio.file.FileAlreadyExistsException;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.NoSuchFileException;
import org.kie.commons.java.nio.file.OpenOption;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;
import org.kie.commons.java.nio.file.attribute.FileAttribute;
import org.kie.commons.java.nio.file.attribute.FileAttributeView;

import static org.kie.commons.java.nio.file.StandardCopyOption.*;

public class IOServiceDotFileImpl
        extends AbstractIOService
        implements IOService {

    @Override
    public synchronized void delete( final Path path )
            throws IllegalArgumentException, NoSuchFileException, DirectoryNotEmptyException,
            IOException, SecurityException {
        Files.delete( path );
        try {
            Files.delete( dot( path ) );
        } catch ( final Exception ex ) {
        }
    }

    @Override
    public synchronized boolean deleteIfExists( final Path path )
            throws IllegalArgumentException, DirectoryNotEmptyException, IOException, SecurityException {
        final boolean result = Files.deleteIfExists( path );
        try {
            Files.delete( dot( path ) );
        } catch ( final Exception ex ) {
        }
        return result;
    }

    @Override
    public synchronized SeekableByteChannel newByteChannel( final Path path,
                                                            final Set<? extends OpenOption> options,
                                                            final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException,
            FileAlreadyExistsException, IOException, SecurityException {
        final SeekableByteChannel result = Files.newByteChannel( path, buildOptions( options ), attrs );

        if ( isFileScheme( path ) ) {
            buildDotFile( path, attrs );
        }

        return result;
    }

    @Override
    public synchronized Path createFile( final Path path,
                                         final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        final Path result = Files.createFile( path, attrs );

        if ( isFileScheme( path ) ) {
            buildDotFile( path, attrs );
        }

        return result;
    }

    @Override
    public synchronized Path createDirectory( final Path dir,
                                              final FileAttribute<?>... attrs )
            throws IllegalArgumentException, UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        final Path result = Files.createDirectory( dir, attrs );

        buildDotFile( dir, attrs );

        return result;
    }

    @Override
    public synchronized Path createDirectories( final Path dir,
                                                final FileAttribute<?>... attrs )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            IOException, SecurityException {
        final Path result = Files.createDirectories( dir, attrs );

        buildDotFile( dir, attrs );

        return result;
    }

    @Override
    public synchronized Path copy( final Path source,
                                   final Path target,
                                   final CopyOption... options )
            throws UnsupportedOperationException, FileAlreadyExistsException,
            DirectoryNotEmptyException, IOException, SecurityException {
        final Path result = Files.copy( source, target, buildOptions( options ) );

        if ( isFileScheme( source ) && Files.exists( dot( source ) ) ) {
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

        if ( isFileScheme( source ) && Files.exists( dot( source ) ) ) {
            Files.move( dot( source ), dot( target ), forceBuildOptions( options ) );
        }

        return result;
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView( final Path path,
                                                                 final Class<V> type )
            throws IllegalArgumentException {
        //TODO
        return Files.getFileAttributeView( path, type );
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes( final Path path,
                                                             final Class<A> type )
            throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException,
            IOException, SecurityException {
        //TODO
        return Files.readAttributes( path, type );
    }

    @Override
    public Map<String, Object> readAttributes( final Path path,
                                               final String attributes )
            throws UnsupportedOperationException, NoSuchFileException, IllegalArgumentException,
            IOException, SecurityException {
        //TODO
        return Files.readAttributes( path, attributes );
    }

    @Override
    public Path setAttribute( final Path path,
                              final String attribute,
                              final Object value )
            throws UnsupportedOperationException, IllegalArgumentException, ClassCastException, IOException, SecurityException {
        //TODO
        return Files.setAttribute( path, attribute, value );
    }

    @Override
    public Object getAttribute( final Path path,
                                final String attribute )
            throws UnsupportedOperationException, IllegalArgumentException, IOException, SecurityException {
        //TODO
        return Files.getAttribute( path, attribute );
    }

    @Override
    public synchronized Path write( final Path path,
                                    final byte[] bytes,
                                    final Set<? extends OpenOption> options,
                                    final FileAttribute<?>... attrs ) throws IllegalArgumentException, IOException, UnsupportedOperationException {
        final SeekableByteChannel byteChannel = Files.newByteChannel( path, buildOptions( options ), attrs );

        try {
            byteChannel.write( ByteBuffer.wrap( bytes ) );
            byteChannel.close();
        } catch ( final java.io.IOException e ) {
            throw new IOException( e );
        }

        if ( isFileScheme( path ) ) {
            buildDotFile( path, attrs );
        }

        return path;
    }

    private Set<? extends OpenOption> buildOptions( final Set<? extends OpenOption> options ) {
        return new HashSet<OpenOption>( options ) {{
//            add( new DotFileOption() );
        }};
    }

    private CopyOption[] forceBuildOptions( final CopyOption[] options ) {
        final CopyOption[] result = new CopyOption[ options.length + 1 ];
        System.arraycopy( options, 0, result, 0, options.length );
        result[ result.length ] = REPLACE_EXISTING;
        return result;
    }

    private CopyOption[] buildOptions( final CopyOption... options ) {
        final CopyOption[] result = new CopyOption[ options.length + 1 ];
        System.arraycopy( options, 0, result, 0, options.length );
//        result[ result.length ] = new DotFileOption();
        return result;
    }

    private void buildDotFile( final Path path,
                               final FileAttribute<?>[] attrs ) {
//        if ( attrs.length > 0 ) {
//            final Properties properties = PropertiesBuilder.build( attrs );
//            final OutputStream out = Files.newOutputStream( dot( path ) );
//            try {
//                properties.store( out, "" );
//            } catch ( java.io.IOException e ) {
//                throw new IOException( e );
//            } finally {
//                try {
//                    out.close();
//                } catch ( java.io.IOException e ) {
//                }
//            }
//        }
    }

    private boolean isFileScheme( final Path path ) {
        if ( path == null || path.getFileSystem() == null || path.getFileSystem().provider() == null ) {
            return false;
        }

        return path.getFileSystem().provider().getScheme().equals( "file" );
    }

    private Path dot( final Path path ) {
        return path.resolveSibling( "." + path.getFileName() );
    }

}
