package barryp.xmllink;

import java.io.*;
import java.net.*;

/**
 * A really simple server that a XML-Link module can
 * connect to for testing purposes.
 */
public class TestServer 
	{
	
/**
 * Accept a connection and print whatever comes over it.
 * @param args java.lang.String[]
 */
public static void main(String args[]) throws Exception
	{
	ServerSocket ss = new ServerSocket(0);

	System.out.println("XMLLink test server listening on port " + ss.getLocalPort());

	Socket s = ss.accept();
	ss.close();

	Reader r = new InputStreamReader(s.getInputStream(), GameModule.STREAM_ENCODING);
	Writer w = new OutputStreamWriter(s.getOutputStream(), GameModule.STREAM_ENCODING);
	w.write("<session><chat>Hello Quake2</chat><cmd>sv</cmd><addlocale locale=\"nl_NL\" />");
	w.flush();
	
	char buffer[] = new char[1024];
	int n;
	
	while ((n = r.read(buffer)) >= 0)
		{
		if (n > 0)
			{
			String str = new String(buffer, 0, n);
			System.out.print(str);
			}
		}

	w.write("</session>");
	w.close();
	r.close();
	s.close();
	}
}