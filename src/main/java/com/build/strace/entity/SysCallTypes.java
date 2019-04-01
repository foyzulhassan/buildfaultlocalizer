package com.build.strace.entity;

import java.util.Arrays;
import java.util.List;

public class SysCallTypes {
	public static List<String> NEW_PROCESS_CALLS = Arrays.asList("vfork", "fork");
	public static List<String> CLONE_CALLS  = Arrays.asList("clone");
	public static List<String> CHANGE_DIR_CALLS   = Arrays.asList("getcwd","chdir","fchdir");	
	public static List<String> EXEC_CALLS   = Arrays.asList("execve");
	public static List<String> READ_CALLS   = Arrays.asList("pread", "pread64","preadv", "read", "readahead", "readv");
	public static List<String> WRITE_CALLS   = Arrays.asList("write", "writev", "pwrite", "pwrite64", "pwritev", "ftruncate","ftruncate64","vmsplice");
	public static List<String> OPENAT_CALL    = Arrays.asList("openat");
	public static List<String> OPEN_CALL    = Arrays.asList("open");
	public static List<String> CLOSE_CALLS    = Arrays.asList("close");

}
