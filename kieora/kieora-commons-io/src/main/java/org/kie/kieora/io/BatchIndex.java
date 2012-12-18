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

import org.kie.commons.java.nio.IOException;
import org.kie.commons.java.nio.base.Properties;
import org.kie.commons.java.nio.file.FileVisitResult;
import org.kie.commons.java.nio.file.Files;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.SimpleFileVisitor;
import org.kie.commons.java.nio.file.attribute.BasicFileAttributes;
import org.kie.kieora.engine.MetaIndexEngine;

import static org.kie.commons.java.nio.base.dotfiles.DotFileUtils.*;
import static org.kie.commons.validation.PortablePreconditions.*;
import static org.kie.kieora.io.KObjectUtil.*;

/**
 *
 */
public final class BatchIndex {

    private final Path            root;
    private final MetaIndexEngine indexEngine;

    public BatchIndex( final MetaIndexEngine indexEngine,
                       final Path root ) {
        this.root = checkNotNull( "root", root );
        this.indexEngine = checkNotNull( "indexEngine", indexEngine );
    }

    public void run() {

        Files.walkFileTree( root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile( final Path file,
                                              final BasicFileAttributes attrs ) throws IOException {
                checkNotNull( "file", file );
                checkNotNull( "attrs", attrs );

                if ( Files.exists( dot( file ) ) ) {
                    final Properties properties = new Properties();
                    properties.load( Files.newInputStream( dot( file ) ) );
                    indexEngine.index( toKObject( file, consolidate( properties ) ) );
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory( final Path dir,
                                                      final BasicFileAttributes attrs )
                    throws IOException {
                checkNotNull( "dir", dir );
                checkNotNull( "attrs", attrs );

                if ( Files.exists( dot( dir ) ) ) {
                    final Properties properties = new Properties();
                    properties.load( Files.newInputStream( dot( dir ) ) );
                    indexEngine.index( toKObject( dir, consolidate( properties ) ) );
                }

                return FileVisitResult.CONTINUE;
            }

        } );

    }

    public void dispose() {
        indexEngine.dispose();
    }

}
