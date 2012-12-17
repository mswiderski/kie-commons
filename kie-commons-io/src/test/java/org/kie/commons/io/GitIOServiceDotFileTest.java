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

package org.kie.commons.io;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.commons.java.nio.file.Path;
import org.kie.commons.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;

/**
 *
 */
public class GitIOServiceDotFileTest extends CommonIOExceptionsServiceDotFileTest {

    @Test
    public void testRoot() throws IOException {
        final Path path = getRootPath();

        ioService.setAttributes( path, new FileAttribute<Object>() {
            @Override
            public String name() {
                return "my_new_key";
            }

            @Override
            public Object value() {
                return "value";
            }
        } );

        final Map<String, Object> attrsValue = ioService.readAttributes( path );

        assertEquals( 6, attrsValue.size() );
        assertTrue( attrsValue.containsKey( "my_new_key" ) );

        ioService.setAttributes( path, new FileAttribute<Object>() {
            @Override
            public String name() {
                return "my_new_key";
            }

            @Override
            public Object value() {
                return null;
            }
        } );

        final Map<String, Object> attrsValue2 = ioService.readAttributes( path );

        assertEquals( 5, attrsValue2.size() );
        assertFalse( attrsValue2.containsKey( "my_new_key" ) );
    }

    @Override
    public Path getFilePath() {

        final Path file = ioService.get( URI.create( "git://repo-test/myfile" + new Random( 10L ).nextInt() + ".txt" ) );
        ioService.deleteIfExists( file );

        return file;
    }

    @Override
    public Path getTargetPath() {
        final Path file = ioService.get( URI.create( "git://repo-test/myTargetFile" + new Random( 10L ).nextInt() + ".txt" ) );
        ioService.deleteIfExists( file );

        return file;
    }

    @Override
    public Path getDirectoryPath() {
        final Path dir = ioService.get( URI.create( "git://repo-test/someDir" + new Random( 10L ).nextInt() ) );
        ioService.deleteIfExists( dir );

        return dir;
    }

    @Override
    public Path getComposedDirectoryPath() {
        return ioService.get( URI.create( "git://repo-test/path/to/someNewRandom" + new Random( 10L ).nextInt() ) );
    }

    private Path getRootPath() {
        return ioService.get( URI.create( "git://repo-test/" ) );
    }

    @BeforeClass
    public static void setup() {
        try {
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty( "org.kie.nio.git.dir", path );
            System.out.println(".niogit: " + path);

            final URI newRepo = URI.create( "git://repo-test" );

            try {
                ioService.newFileSystem( newRepo, new HashMap<String, Object>() );
            } catch ( final Exception ex ) {
            }

        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

}
