package com.asdamp.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
	 public static int copy(InputStream input, OutputStream output) throws IOException {
		 long count = copyLarge(input, output);
		if (count > Integer.MAX_VALUE) {
		return -1;
		 }
		        return (int) count;
	   }
	 
	 public static long copyLarge(InputStream input, OutputStream output)
	           throws IOException {
	      byte[] buffer = new byte[1024];
       long count = 0;
	        int n = 0;
       while (-1 != (n = input.read(buffer))) {
	            output.write(buffer, 0, n);
	        count += n;
	        }
	       return count;
	   }
}
