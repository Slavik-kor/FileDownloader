package com.servlet.explore;

import java.io.*;
import java.util.zip.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.http.fileupload.servlet.*;
import org.apache.commons.codec.binary.Base64;

/**
 * Servlet implementation class ServletDL
 */
@WebServlet(description = "Servlet for downloading file via http post request", urlPatterns = { "/ServletDL" })
public class ServletDL extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private boolean isMultipart;
	   private int maxFileSize = 100 * 1024*1024;
	   private int maxMemSize = 4 * 1024;
       private String fileName;
       private long sizeInBytes;
       private String num;
       private String checksum;
       private boolean isLast;
       private  int amount=0;
       private byte[] dataBase64;
       private byte[] data;
        
    
    
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
	      
	      // Process the uploaded file items

	      out.println("<html>");
	      out.println("<head>");
	      out.println("<title>Servlet upload</title>");  
	      out.println("</head>");
	      out.println("<body>");
	    
	        FileItem fi = upload.parseRequest(new ServletRequestContext(request)).get(0);
	        	 
	        
	             fileName = fi.getName();
	             sizeInBytes = fi.getSize();
	           	            
	            // Get the  parameters of request
	           num = request.getParameter("num");
	    	   checksum = request.getParameter("checksum");
	    	   isLast=new Boolean(request.getParameter("isLast")).booleanValue();	
	    	   
	        InputStream o=null; 
	   		try{
	   		 o=fi.getInputStream();
	   		int i=(int)sizeInBytes;
	   		dataBase64=new byte[i];
	   		o.read(dataBase64);
	   		o.close();} catch (Exception e){e.printStackTrace();}
	   		// decoding 
	   		data=Base64.decodeBase64(dataBase64);
	    	   
	    	  if(checksum.equalsIgnoreCase(getChecksum(data))){
	    	  out.println("<h1>OK</h1>");
	            // Write the file
	    	 try{ saveFile(data);} catch (Exception e){e.printStackTrace();}
	    	  } else
	    	  {out.println("<h1>Repeat</h1>");
	    	  out.println("<h1>"+checksum+"!="+getChecksum(data)+"</h1>");
	    	  }
	            
	      out.println("</body>");
	      out.println("</html>");
	   }catch(Exception ex) {
	       System.out.println(ex);
	   }
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out= response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \n"+
		"<html>\n"+
		 "<head> <title>Response</title> </head>\n"+
		 "<body><h1>Test</h1></body>\n"+
		"</html>");
	}
	
	private String getChecksum(byte[] f){
		return DigestUtils.md5Hex(f);
	}
	
	
	private void saveFile(byte[] data) throws Exception{
	   // Create Zip file
	   File zip=new File("c:\\Temp\\tmp.zip");
	   if(!zip.exists()){zip.createNewFile();}
	  
	  
	   //Create Zip entry
	   ZipEntry entry;
	   if(isLast){entry=new ZipEntry(num+"last");
	   }else {entry=new ZipEntry(num);}
	   
	   // Record Zip Entry
	   ZipOutputStream out= new ZipOutputStream(new FileOutputStream(zip));
	   out.putNextEntry(entry);
	   out.write(data);
	   out.closeEntry();
	   out.close();
	   
	    ZipFile zipArch= new ZipFile(zip);
	    
	   // Finding entry with "last"
	   ZipInputStream in=new ZipInputStream(new FileInputStream(zip));
	   entry=null; 
	   while((entry=in.getNextEntry())!=null) {
		   if(entry.getName().endsWith("last")){
			   amount=Integer.parseInt(entry.getName().replaceAll("last", ""));
			   break;
		   }
	   }
	   in.closeEntry();
	   in.close();
	   
	   // Checking out of ready to gather
	   if((amount!=0)&&(zipArch.size()==amount)){
		   File f=new File("c:\\"+fileName);
		   f.createNewFile();
		   		   
		   //Gathering all zip entries
		   ZipInputStream inz= new ZipInputStream(new FileInputStream(zip));
		   FileOutputStream outf=new FileOutputStream(f);
		   ZipEntry ent=null; int b=1024; 
		  for(int i=0;i<amount;i++){
		if (i==amount-1){ent=zipArch.getEntry(Integer.toString(i+1)+"last");
		}else{
			 ent=zipArch.getEntry(Integer.toString(i+1));
		}
				   byte[] buf=new byte[b]; int ch;
				   InputStream inEntry=zipArch.getInputStream(ent);
				   while ((ch=inEntry.read(buf))!=-1) {
						outf.write(buf,0,ch);
					}				   
				   inEntry.close();  
				
			   inz.closeEntry();  	
	   }
		  	    
		  inz.close();
		   outf.close();	  
		   zipArch.close();
		   zip.delete();
		 
	   }
		
	}
	

}
