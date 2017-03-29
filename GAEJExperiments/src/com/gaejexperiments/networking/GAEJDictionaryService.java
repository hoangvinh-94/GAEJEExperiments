package com.gaejexperiments.networking;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
@SuppressWarnings("serial")
public class GAEJDictionaryService extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String strCallResult = "";
		resp.setContentType("text/plain");
		try {
			//Extract out the word that needs to be looked up in the Dictionary Service
			String strWord = req.getParameter("word");
			//Do validations here. Only basic ones i.e. cannot be null/empty
			if (strWord == null) throw new Exception("Word field cannot be empty.");
			//Trim the stuff
			strWord = strWord.trim();
			if (strWord.length() == 0) throw new Exception("Word field cannot be empty.");
			String strDictionaryServiceCall ="http://services.aonaware.com/DictService/DictService.asmx/Define?word=";
			strDictionaryServiceCall += strWord;
			URL url = new URL(strDictionaryServiceCall);
			BufferedReader reader = new BufferedReader(new
					InputStreamReader(url.openStream()));
			StringBuffer response = new StringBuffer();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			strCallResult = response.toString();
			DocumentBuilderFactory builderFactory =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new
					StringReader(strCallResult.toString())));
			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr =
					xpath.compile("//Definition[Dictionary[Id='wn']]/WordDefinition/text()"
							);
			Object result = expr.evaluate(doc, XPathConstants.NODESET);
			NodeList nodes = (NodeList) result;
			for (int i = 0; i < nodes.getLength(); i++) {
				strCallResult = nodes.item(i).getNodeValue();
			}
			resp.getWriter().println(strCallResult);
		}
		catch (Exception ex) {
			strCallResult = "Fail: " + ex.getMessage();
			resp.getWriter().println(strCallResult);
		}
	}
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}