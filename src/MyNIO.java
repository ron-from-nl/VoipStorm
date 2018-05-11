/*
 * Copyright © 2008 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public class MyNIO
{
    private static int		verbosity = 1;
    
    public  static FileSystem	getDefaultFS() { return FileSystems.getDefault(); } // FS Object default SSD
    public  static Path		getUserDir() { return getDefaultFS().getPath(System.getProperty("user.dir")); }
    public  static Path		getJarFile() { return Paths.get(getUserDir().toString(), "VoipStorm.jar"); }
    public  static FileSystem	getJarFS() throws IOException { return FileSystems.newFileSystem(getJarFile(), null); }

    public static void listTree(Path source)
    {
        if (1<=verbosity) { System.out.println("listTree: " + source); }
	
        try { Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileVisit()
        {
            @Override public FileVisitResult visitFile(Object path, BasicFileAttributes attrs)
            {
                Path mypath = (Path) path;
                Path name = mypath.getFileName();
                long size = 0;
                try {size = (Long) Files.getAttribute(mypath, "basic:size");} catch (IOException ex) {System.err.println(ex);}
                if (name != null ) { try {System.out.println(mypath.toRealPath().toString() + " size " + size +" bytes");}
                catch (IOException ex) {System.err.println(ex);}
            }
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs)   { System.out.println((Path) dir); return FileVisitResult.CONTINUE; }
            @Override public FileVisitResult postVisitDirectory(Object dir, IOException exc)            { return FileVisitResult.CONTINUE; }
            @Override public FileVisitResult visitFileFailed(Object file, IOException exc)              { System.out.println(exc); return FileVisitResult.CONTINUE; }
        }
        
        ); } catch (IOException ex) { System.err.println("Files.walkFileTree ListTree IOException" + ex); }
        if (1<=verbosity) { System.out.println(); }
    }
    
    public static void copyTree(Path source, final Path target)
    {
        if (1<=verbosity) { System.out.println("copyTree: " + source + " -> " + target); }
        try { Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileVisit()
        {
            @Override public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs)
            {
                if (dir != null)
                {
                    Path targetdir = Paths.get(target.toString(), dir.toString());
//                    System.out.println("preVisitDir: " + targetdir.toString());
                    if (Files.notExists(targetdir)) { try {Files.createDirectories(targetdir);} catch (IOException e) {System.err.println("createDirectories: " + e);} }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult visitFile(Object file, BasicFileAttributes attrs)
            {
                if (file != null)
                {
                    Path sourcepath = (Path) file;
                    Path targetpath = Paths.get(target.toString(), file.toString());
                    if (2<=verbosity) { System.out.println("copying " + targetpath); }
                    try { Files.copy(sourcepath, targetpath, COPY_ATTRIBUTES, NOFOLLOW_LINKS);} catch (IOException e) { /*System.err.println("copy: " + e);*/ } // Don't turn on, flud!
                }
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult visitFileFailed(Object file, IOException exc)
            {
                System.err.println("visitFileFailed " + exc);
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult postVisitDirectory(Object dir, IOException exc)
            {
//                System.out.println("postVisitDirectory " + exc);
                return FileVisitResult.CONTINUE;
            }
        }
        
        ); } catch (IOException ex) { System.err.println(ex); }
//        if (1<=verbosity) { System.out.println(); }
    }
    
    public static void moveTree(Path source, final Path target)
    {        
        if (1<=verbosity) { System.out.println("moveTree: " + source + " -> " + target); }
        try { Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileVisit()
        {
            @Override public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs)
            {
                if (dir != null)
                {
                    Path targetdir = (Path) dir;
//                    System.out.println("preVisitDir: " + targetdir.toString());
                    if (Files.notExists(targetdir)) { try {Files.createDirectories(targetdir);} catch (IOException e) {System.err.println("createDirectories: " + e);} }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult visitFile(Object file, BasicFileAttributes attrs)
            {
                if (file != null)
                {
                    Path sourcepath = (Path) file;
                    Path targetpath = Paths.get(target.toString(), file.toString());
                    if (2<=verbosity) { System.out.println("moving " + targetpath); }
                    try { Files.copy(sourcepath, targetpath, COPY_ATTRIBUTES, NOFOLLOW_LINKS);} catch (IOException e) {System.err.println("copy: " + e);return FileVisitResult.CONTINUE;}
                    try {Files.deleteIfExists(sourcepath);} catch (IOException e) {System.err.println("deleteIfExists: " + e);}

                }
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult visitFileFailed(Object file, IOException exc)
            {
                System.err.println("visitFileFailed " + exc);
                return FileVisitResult.CONTINUE;
            }

            @Override public FileVisitResult postVisitDirectory(Object dir, IOException exc)
            {
                if (2<=verbosity) { System.out.println("deleting " + dir); }
                try {Files.deleteIfExists((Path) dir);} catch (IOException e) {System.err.println("deleteIfExists " + e);}
                return FileVisitResult.CONTINUE;
            }
        }
        
        ); } catch (IOException ex) { System.err.println(ex); }
        if (1<=verbosity) { System.out.println(); }
    }
    
    public static void deleteTree(Path source)
    {
        if (1<=verbosity) { System.out.println("deleteTree: " + source); }
        try { Files.walkFileTree(source, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileVisit()
        {
            @Override public FileVisitResult visitFile(Object path, BasicFileAttributes attrs)
            {
                if (2<=verbosity) { System.out.println("deleting " + path); }
                try {Files.deleteIfExists((Path) path);} catch (IOException e) { System.err.println("deleteIfExists: " + e); }
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs) { return FileVisitResult.CONTINUE; }
            @Override public FileVisitResult postVisitDirectory(Object dir, IOException exc)
            {
                if (2<=verbosity) { System.out.println("deleting " + dir); }
                try {Files.deleteIfExists((Path) dir);} catch (IOException e) { System.err.println("deleteIfExists: " + e);}
                return FileVisitResult.CONTINUE;
            }
            @Override public FileVisitResult visitFileFailed(Object file, IOException exc) { System.err.println("visitFileFailed" + exc); return FileVisitResult.CONTINUE; }
        }
        
        ); } catch (IOException ex) { System.err.println("Files.walkFileTree ListTree IOException" + ex); }
        if (1<=verbosity) { System.out.println(); }
    }

    public static void sTree(final Path file, String wildcard) // "*.txt"
    {
	final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + wildcard);
        if (1<=verbosity) { System.out.println("listTree: " + wildcard); }
        try
	{ Files.walkFileTree(file, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE, new FileVisit()
	    {
		@Override public FileVisitResult visitFile(Object path, BasicFileAttributes attrs)
		{
		    if (file.getFileName() != null && matcher.matches(file.getFileName()))
		    {
			try { System.out.println(file.toRealPath().toString());	} catch (IOException ex) { System.err.println("file.getFileName() IOException" + ex); }
		    }
		    return FileVisitResult.CONTINUE;
		}
		@Override public FileVisitResult preVisitDirectory(Object dir, BasicFileAttributes attrs)   { System.out.println((Path) dir); return FileVisitResult.CONTINUE; }
		@Override public FileVisitResult postVisitDirectory(Object dir, IOException exc)            { return FileVisitResult.CONTINUE; }
		@Override public FileVisitResult visitFileFailed(Object file, IOException exc)              { System.out.println(exc); return FileVisitResult.CONTINUE; }
	    }
        
      );} catch (IOException ex) { System.err.println("Files.walkFileTree ListTree IOException" + ex); }
        if (1<=verbosity) { System.out.println(); }
    }

    public static void testNIO() throws IOException
    {
// Paths
//        Path path = FileSystems.getDefault().getPath("/rafaelnadal/tournaments/2009", "BNP.txt");        
        Path path = Paths.get(System.getProperty("user.home"), "Experiment");

        System.out.println("The file/directory indicated by path: " + path.getFileName());
        System.out.println("Root of this path: " + path.getRoot());
        System.out.println("Parent: " + path.getParent());
        System.out.println("Number of name elements in path: " + path.getNameCount());
        for (int i = 0; i < path.getNameCount(); i++) {System.out.println("E " + i + " is: " + path.getName(i));}
        System.out.println("Subpath (0,3): " + path.subpath(0, 3));
        
        System.out.println(path.resolveSibling("Desktop").toString() + "resolveSibling");
        System.out.println(path.toString());

        Path path1 = Paths.get(System.getProperty("user.home"), "Experiment", "Finance");
        Path path2 = Paths.get(System.getProperty("user.home"), "Desktop");
        System.out.println(path1.relativize(path2).toString());
        
        if (path1.compareTo(path2) != 1) { System.out.println("Both paths1 & path2 are NOT equal"); }
        for (Path name : path) {System.out.println(name);}
        
// Attributes
        FileSystem filesys = FileSystems.getDefault();
        Set<String> views = filesys.supportedFileAttributeViews();
        for (String view : views) { System.out.println("Attribute " + view + " supported in default FS"); }; System.out.println();
        
        for (FileStore store : filesys.getFileStores())
        {
            if (store.supportsFileAttributeView(BasicFileAttributeView.class))
            {System.out.println("Store [" + store.name() + "] supports basic view");}
            else {System.out.println("Store [" + store.name() + "] NOT supports basic view");}
        } System.out.println();
        
        // Get basic fileattributes
        BasicFileAttributes attr = null;
        try { attr = Files.readAttributes(path, BasicFileAttributes.class); } catch (IOException e) { System.err.println(e); }
        System.out.println("File size: " + attr.size());
        System.out.println("File creation time: " + attr.creationTime());
        System.out.println("File was last accessed at: " + attr.lastAccessTime());
        System.out.println("File was last modified at: " + attr.lastModifiedTime());
        System.out.println("Is directory? " + attr.isDirectory());
        System.out.println("Is regular file? " + attr.isRegularFile());
        System.out.println("Is symbolic link? " + attr.isSymbolicLink());
        System.out.println("Is other? " + attr.isOther());
        System.out.println();
        
        try { long size = (Long)Files.getAttribute(path, "basic:size", NOFOLLOW_LINKS);
        System.out.println("Size: " + size); } catch (IOException e) {System.err.println(e);}
        // basic attributes: size • isRegularFile • isDirectory • isSymbolicLink • isOther • fileKey
        System.out.println();
        
        // Set dateattributes
//        long time = System.currentTimeMillis();
//        FileTime fileTime = FileTime.fromMillis(time);
//        try {Files.getFileAttributeView(path, BasicFileAttributeView.class).setTimes(fileTime, fileTime, fileTime);} catch (IOException e) {System.err.println(e);}
//        try {Files.setLastModifiedTime(path, fileTime);} catch (IOException e) {System.err.println(e);}
    
//        dos attributes: hidden • readonly • system • archive
//        DosFileAttributes dosattr = null;
//        try { attr = Files.readAttributes(path, DosFileAttributes.class);} catch (IOException e) {System.err.println(e);}
//        System.out.println("Is read only ? " + dosattr.isReadOnly());
//        System.out.println("Is Hidden ? " + dosattr.isHidden());
//        System.out.println("Is archive ? " + dosattr.isArchive());
//        System.out.println("Is system ? " + dosattr.isSystem());
        
        // Set owner
        UserPrincipal owner = null;
        try {owner = path.getFileSystem().getUserPrincipalLookupService().lookupPrincipalByName("ron");Files.setOwner(path, owner);} catch (IOException e) {System.err.println(e);}

        FileOwnerAttributeView foav = Files.getFileAttributeView(path, FileOwnerAttributeView.class);
        try {System.out.println(foav.getOwner().getName() + " owns path " + path.toString());} catch (IOException e) {System.err.println(e);}
        
//        posix view attributes: group • permissions
        // get permissions
        PosixFileAttributes posixattr = null;
        try {posixattr = Files.readAttributes(path, PosixFileAttributes.class);} catch (IOException e) {System.err.println(e);}
        System.out.println("File owner: " + posixattr.owner().getName());
        System.out.println("File group: " + posixattr.group().getName());
        System.out.println("File permissions: " + posixattr.permissions().toString());
        System.out.println();
        
        // Create file and set permission
        Path new_path = Paths.get(path.toString(), "test.txt");
        FileAttribute<Set<PosixFilePermission>> posixattrs = PosixFilePermissions.asFileAttribute(posixattr.permissions());
        try {Files.createFile(new_path, posixattrs);} catch (IOException e) {System.err.println(e);}
        Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rw-r--r--");
        try {Files.setPosixFilePermissions(new_path, permissions);} catch (IOException e) {System.err.println(e);}
        Files.deleteIfExists(new_path);
        
        // Group owner
        try {GroupPrincipal group = (GroupPrincipal) Files.getAttribute(path, "posix:group", NOFOLLOW_LINKS);
        System.out.println(group.getName());} catch (IOException e) {System.err.println(e);}
        
        // ACLs
//        List<AclEntry> acllist = null; AclFileAttributeView aclview = Files.getFileAttributeView(path, AclFileAttributeView.class);
//        try {acllist = aclview.getAcl();} catch (IOException e) {System.err.println(e.getMessage());}
        
        // File Store Attributes
        System.out.println();
        for (FileStore store : filesys.getFileStores())
        {
            try {
                    long total_space = store.getTotalSpace() / 1024;
                    long used_space = (store.getTotalSpace() - store.getUnallocatedSpace()) / 1024;
                    long available_space = store.getUsableSpace() / 1024;
                    boolean is_read_only = store.isReadOnly();
                    System.out.println("--- " + store.name() + " --- " + store.type());
                    System.out.println("Total space: " + total_space);
                    System.out.println("Used space: " + used_space);
                    System.out.println("Available space: " + available_space);
                    System.out.println("Is read only? " + is_read_only);
                } catch (IOException e) {System.err.println(e);}
            System.out.println();
        }
        
        try {
               FileStore store = Files.getFileStore(path);
               long total_space = store.getTotalSpace() / 1024;
               long used_space = (store.getTotalSpace() - store.getUnallocatedSpace()) / 1024;
               long available_space = store.getUsableSpace() / 1024;
               boolean is_read_only = store.isReadOnly();
               System.out.println("--- " + store.name() + " --- " + store.type());
               System.out.println("Total space: " + total_space);
               System.out.println("Used space: " + used_space);
               System.out.println("Available space: " + available_space);
               System.out.println("Is read only? " + is_read_only);
            } catch (IOException e) {System.err.println(e);}
            System.out.println();
            
        // Files and Directories
        if (Files.exists(path, new LinkOption[]{LinkOption.NOFOLLOW_LINKS})) { System.out.println(path + " exists"); }
        System.out.println();
        
        if (Files.isRegularFile(path)) {System.out.println("The checked file isRegularFile!");} else {System.out.println("The checked file not isRegularFile!");}
        if (Files.isReadable(path)) {System.out.println("The checked file isReadable!");} else {System.out.println("The checked file not isReadable!");}
        if (Files.isExecutable(path)) {System.out.println("The checked file isExecutable!");} else {System.out.println("The checked file not isExecutable!");}
        if (Files.isWritable(path)) {System.out.println("The checked file isWritable!");} else {System.out.println("The checked file not isWritable!");}
        System.out.println();
        
        Iterable<Path> dirs = FileSystems.getDefault().getRootDirectories();
        for (Path name : dirs) { System.out.println(name);}
        System.out.println();
        
        Path newdir = Paths.get(path.toString(), "rondir");
        if (Files.notExists(newdir)) { try {Files.createDirectory(newdir);} catch (IOException e) {System.err.println(e);} }
        try {Files.deleteIfExists(newdir);} catch (IOException e) {System.err.println(e);}
        
        Set<PosixFilePermission> perms = PosixFilePermissions.fromString("rwxr-x---");
        FileAttribute<Set<PosixFilePermission>> posixattr2 = PosixFilePermissions.asFileAttribute(perms);
        if (Files.notExists(newdir)) { try {Files.createDirectory(newdir, posixattr2);} catch (IOException e) {System.err.println(e);} }
        try {Files.deleteIfExists(newdir);} catch (IOException e) {System.err.println(e);}
        
        // List dir contents (no filter)
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path))
        { for (Path file : ds) { System.out.println(file.getFileName());}}catch(IOException e) {System.err.println(e);}
        System.out.println();
        
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(path, "*.{png,jpg,bmp,DS_Store}"))
        {for (Path file : ds) {System.out.println(file.getFileName());}} catch (IOException e) {System.err.println(e);}
        System.out.println();
        
        // Creating reading and writing files
        Path newfile = Paths.get(path.toString(), "ronfile");
        if (Files.notExists(newfile)) { try {Files.createFile(newfile);} catch (IOException e) {System.err.println(e);} }
        
        perms = PosixFilePermissions.fromString("rw-------");
        posixattr2 = PosixFilePermissions.asFileAttribute(perms);
        try {Files.createFile(newfile, posixattr2);} catch (IOException e) {System.err.println(e);}
        
        // Writing a file
        Path ball_path = Paths.get(path.toString(), "ball.png");
        byte[] ball_bytes = new byte[]{
        (byte)0x89,(byte)0x50,(byte)0x4e,(byte)0x47,(byte)0x0d,(byte)0x0a,(byte)0x1a,(byte)0x0a,
        (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0d,(byte)0x49,(byte)0x48,(byte)0x44,(byte)0x52,
        (byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,
        (byte)0x08,(byte)0x02,(byte)0x00,(byte)0x49,(byte)0x45,(byte)0x4e,(byte)0x44,(byte)0xae,
        (byte)0x42,(byte)0x60,(byte)0x82};
        try {Files.write(ball_path, ball_bytes);} catch (IOException e) {System.err.println(e);}
        try {byte[] ballArray = Files.readAllBytes(ball_path);} catch (IOException e) {System.out.println(e);}
        
        // Wiki
        Path rf_wiki_path = Paths.get(path.toString(), "wiki.txt");
        String rf_wiki = "Rafael \"Rafa\" Nadal Parera (born 3 June 1986) is a Spanish professional tennis " + "player and a former World No. 1. As of 29 August 2011 (2011 -08-29)[update], he is ranked No. 2 " + "by the Association of Tennis Professionals (ATP). He is widely regarded as one of the greatest players " + "of all time; his success on clay has earned him the nickname \"The King of Clay\", and has prompted " + "many experts to regard him as the greatest clay court player of all time. Some of his best wins are:";
        try {byte[] rf_wiki_byte = rf_wiki.getBytes("UTF-8"); Files.write(rf_wiki_path, rf_wiki_byte);} catch (IOException e) {System.err.println(e);}
        System.out.println();
        
        Charset charset = Charset.forName("UTF-8");
        ArrayList<String> lines = new ArrayList<>();
        lines.add("\n");
        lines.add("Rome Masters - 5 titles in 6 years");
        lines.add("Monte Carlo Masters - 7 consecutive titles (2005-2011)");
        lines.add("Australian Open - Winner 2009");
        lines.add("Roland Garros - Winner 2005-2008, 2010, 2011");
        lines.add("Wimbledon - Winner 2008, 2010");
        lines.add("US Open - Winner 2010");
        try {Files.write(rf_wiki_path, lines, charset, StandardOpenOption.APPEND);} catch (IOException e) {System.err.println(e);}
     
        // Buffered writer
        String text = "\nVamos Rafa!";
        try (BufferedWriter writer = Files.newBufferedWriter(rf_wiki_path, charset,StandardOpenOption.APPEND)) {writer.write(text);}
        catch (IOException e) {System.err.println(e);}

        charset = Charset.forName("ISO-8859-1");
        try {lines = (ArrayList<String>) Files.readAllLines(rf_wiki_path, charset);
        for (String line : lines) {System.out.println(line);}} catch (IOException e) {System.out.println(e);}
        System.out.println();
        
        charset = Charset.forName("UTF-8");
        try (BufferedReader reader = Files.newBufferedReader(rf_wiki_path, charset)) 
        {String line = null;while ((line = reader.readLine()) != null) {System.out.println(line);}} catch (IOException e) {System.err.println(e);}
        System.out.println();
        
        // Get tmp dir
        System.out.println("OS Tmp dir: " + System.getProperty("java.io.tmpdir"));
        
        // Create TMP Directory
//        System.out.println("Created Temp dir: " + Files.createTempDirectory(null));
        
        // Create TMP Dir in basedir
//        try { Path tmp = Files.createTempDirectory(path, "tmpdir_"); System.out.println("TMP: " + tmp.toString());}
//        catch (IOException e) {System.err.println(e);}
        
        //delete the file
        try {Files.delete(rf_wiki_path);} catch (IOException | SecurityException e) {System.err.println(e);}
        try {Files.deleteIfExists(rf_wiki_path);} catch (IOException | SecurityException e) {System.err.println(e);}
        System.out.println();
        
        // Create file
        Path myfilepath =       Paths.get(path.toString(), "a.txt");
        if (Files.notExists(myfilepath)) { try {Files.createFile(myfilepath);} catch (IOException e) {System.err.println(e);} }
        
        // Add data to file
        lines = new ArrayList<>();
        lines.add("Some text line");
        try {Files.write(myfilepath, lines, charset, StandardOpenOption.APPEND);} catch (IOException e) {System.err.println(e);}

        // First Copy
        Path mycopyfrompath =   myfilepath;
        Path mycopytopath =     myfilepath.resolveSibling("a_copy.txt");
        try {Files.copy(mycopyfrompath, mycopytopath, REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);} catch (IOException e) {System.err.println(e);}
        System.out.println();
        
        // Second Copy
        Path mycopy2frompath =  mycopytopath;
        Path mycopy2topath =    mycopy2frompath.resolveSibling("a_copy2.txt");
        try {Files.copy(mycopy2frompath, mycopy2topath, REPLACE_EXISTING, COPY_ATTRIBUTES, NOFOLLOW_LINKS);} catch (IOException e) {System.err.println(e);}
        System.out.println();
        
        // Create dir
        newdir = Paths.get(path.toString(), "rondir");
        if (Files.notExists(newdir)) { try {Files.createDirectory(newdir);} catch (IOException e) {System.err.println(e);} }
        
        // Move file MUST DEFINE TARGET FILENAME IN TARGET DIR
        Path mymovefrompath =   mycopy2topath;
        Path mymovetopath =     Paths.get(path.toString(), "rondir", mymovefrompath.getFileName().toString());
        try {Files.move(mycopy2topath, mymovetopath, StandardCopyOption.REPLACE_EXISTING);} catch (IOException e) {System.err.println(e);}

        // Rename File
        Path myrenamefrompath = mymovetopath;
        Path myrenametopath =   mymovetopath.resolveSibling("a_renamed.txt");
        try {Files.move(myrenamefrompath, myrenametopath,StandardCopyOption.REPLACE_EXISTING);} catch (IOException e) {System.err.println(e);}

        // Delete File
//        Files.deleteIfExists(myrenametopath);
        
        // Recursive Visiting Process
        
        Path newsubdir = Paths.get(newdir.toString(), "subdir2");
        if (Files.notExists(newsubdir)) { try {Files.createDirectory(newsubdir);} catch (IOException e) {System.err.println(e);} }
    }
}
abstract class FileVisit implements FileVisitor {}
