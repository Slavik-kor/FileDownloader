package com.servlet.explore;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.http.*;
import org.apache.tomcat.util.http.fileupload.servlet.*;

/**
 * Servlet implementation class ServletDL
 */
@WebServlet(description = "Servlet for downloading file via http post request", urlPatterns = { "/ServletDL" })
public class ServletDL extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private boolean isMultipart;
	   private String filePath;
	   private int maxFileSize = 50 * 1024*1024;
	   private int maxMemSize = 4 * 1024;
	   private File file;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletDL() {
        super();
        // TODO Auto-generated constructor stub
    }

    
    
    
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		
	
		 isMultipart = ServletFileUpload.isMultipartContent(request);
	      response.setContentType("text/html");
	      java.io.PrintWriter out = response.getWriter( );
	      if( !isMultipart ){
	         out.println("<html>");
	         out.println("<head>");
	         out.println("<title>Servlet upload</title>");  
	         out.println("</head>");
	         out.println("<body>");
	         out.println("<p>No file uploaded. Repeat, please </p>"); 
	         out.println("</body>");
	         out.println("</html>");
	         return;
	      }
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
	      // maximum size that will be stored in memory
	      factory.setSizeThreshold(maxMemSize);
	      // Location to save data that is larger than maxMemSize.
	      factory.setRepository(new File("c:\\Temp"));

	      // Create a new file upload handler
	      ServletFileUpload upload = new ServletFileUpload(factory);
	      // maximum file size to be uploaded.
	      upload.setSizeMax(maxFileSize);

	      try{ 
	      // Parse the request to get file items.
	      	
	      // Process the uploaded file items
	      

	      out.println("<html>");
	      out.println("<head>");
	      out.println("<title>Servlet upload</title>");  
	      out.println("</head>");
	      out.println("<body>");
	    
	        FileItem fi = upload.parseRequest(new ServletRequestContext(request)).get(0);
	        
	            // Get the uploaded file parameters
	            String fieldName = fi.getFieldName();
	            String fileName = fi.getName();
	            String contentType = fi.getContentType();
	            boolean isInMemory = fi.isInMemory();
	            long sizeInBytes = fi.getSize();
	           
	            
	            // Get the  parameters of request
	          String num = request.getParameter("num");
	    	  String checksum = request.getParameter("checksum");
	    	  
	    	  
	    	  if(checksum.equals(getChecksum(fi,sizeInBytes))){
	    	  out.println("<h1>OK</h1>");
	            // Write the file
	    	  saveFile(fi,fileName);
	    	  } else
	    	  {out.println("<h1>Repeat</h1>");}
	            

	          //  response.getWriter().println("checksum="+request.getParameter("checksum"));
	         /*  InputStream in=fi.getInputStream();
	            OutputStream outf = new FileOutputStream(new File("d://"+fileName));
	            
	            byte[] buf=new byte[1024];
	            int ch;
	    		while ((ch=in.read(buf))!=-1) {
	    			outf.write(buf);
	    		}		
	    		outf.close();*/
	         
	      
	      out.println("</body>");
	      out.println("</html>");
	   }catch(Exception ex) {
	       System.out.println(ex);
	   }
	   
		
	
		/*ServletInputStream in= request.getInputStream();
		File file=new File("c://Users//Korotkiy//workspace//Download//File.tif");
		FileOutputStream out=new FileOutputStream(file);
		if(!file.exists()){file.createNewFile();}
		byte[] buf=new byte[1024*1024];
		int ch;
		Base64.Decoder dec= Base64.getDecoder();
		while ((ch=in.read(buf))!=-1) {
			out.write(dec.decode(buf));
		}		
		out.close();
		*/
		
	//	String num=request.getParameterValues("num");
	//	String checksum=request.getParameterValues("checksum");
	//	String[] name=request.getParameterValues("isLast");
	//	String[] data=request.getParameterValues("data");
		
		
	//	GetInfoDL();
		
		
		//doGet(request,response);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out= response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \n"+
		"<html>\n"+
		 "<head> <title>Response</title> </head>\n"+
		 "<body><h1>Hello world by Slv</h1></body>\n"+
		"</html>");
	}
	
	private String getChecksum(FileItem fi,long size){
		InputStream o=null; 
		String md5 = null;
		try{
		 o=fi.getInputStream();
		int i=(int)size;
		byte[] f=new byte[i];
		o.read(f);
		o.close();
		md5=DigestUtils.md5Hex(f);
		} catch (Exception e) {e.printStackTrace();}
		return md5;
	}
	
	
	private void saveFile(FileItem fi, String fileName){
		try
		{file = new File("d:\\slv\\"+fileName);
         fi.write( file );}
		catch(Exception e) {e.printStackTrace();}
	}
	

}
