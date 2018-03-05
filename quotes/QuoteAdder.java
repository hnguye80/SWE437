package quotes;

import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class QuoteAdder {
	// the URL to the quotes.xml file
	private String quoteFileName = "src/quotes/quotes.xml";

	private static final String SPECIAL_CHARECTORS = "[@#$%&*()_+=|<>?{}\\\\[\\\\]~-]";

	private boolean quoteValid = true;
	private String message;

	public boolean isQuoteValid () {
		return quoteValid;
	}

	public String getMessage() {
		return message;
	}

	public QuoteAdder() {
		// TODO Auto-generated constructor stub
	}

	public QuoteAdder (String quoteFileName) {
		this.quoteFileName = quoteFileName;
	}


	public void validQuote(String quoteText, String author) {
		Pattern patternSpecialCharactors = Pattern.compile(SPECIAL_CHARECTORS);


		Matcher hasSpecialCharactors = patternSpecialCharactors.matcher(quoteText);
		// check no special character
		if (hasSpecialCharactors.find() == true) {
			quoteValid = false;
			message = "Your quote contains special character(s)!";
		}
		// check no numbers
		if (quoteText.matches("[0-9]+")) {
			quoteValid = false;
			message = "Your quote contains number(s)!";
		}

		/* check duplicate for the quote
		 * NOTE: I will go with the assumption that a specific quote will go with certain author(s)
		 * For example: the quote "I know that you believe you understand what you think I said, but I am not sure you realize that what you heard is not what I meant." belong to Richard Nixon
		 * So, this quote will not be claimed for somebody else.
		 */
		// get the quote list
		QuoteSaxParser qParser = new QuoteSaxParser(quoteFileName);
		// Stores all the quotes from the XML file
		QuoteList quoteList;
		quoteList = qParser.getQuoteList();
		QuoteList searchRes = quoteList.search(quoteText, QuoteList.SearchTextVal);
		Quote quoteTmp;

		// if the list of quote is > 0 meaning that the author already has some quotes in the file
		if (searchRes.getSize() > 0) {
			quoteValid = false;
			// check how similar the existing quote vs the new quote
			for (int i = 0; i < searchRes.getSize(); i++) {
				quoteTmp = searchRes.getQuote(i);
				message = "[Duplicate Quotes] \n " + quoteTmp.getQuoteText() + "(" + quoteTmp.getAuthor() + ")";

				System.out.println(message);

			}
		}		
	}

	public void appendNewQuote(String quoteText, String author, String keyword) throws Exception{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(this.quoteFileName);
		Element root = document.getDocumentElement();

		Element newQuoteNode = document.createElement("quote");	        
		Element quoteTextNode = document.createElement("quote-text");
		Element authorNode = document.createElement("author");
		Element keywordNode = document.createElement("keyword");

		newQuoteNode.appendChild(quoteTextNode);
		newQuoteNode.appendChild(authorNode);
		newQuoteNode.appendChild(keywordNode);

		quoteTextNode.appendChild(document.createTextNode(quoteText));
		authorNode.appendChild(document.createTextNode(author));
		keywordNode.appendChild(document.createTextNode(keyword));


		root.appendChild(newQuoteNode);


		DOMSource source = new DOMSource(document);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(this.quoteFileName);
		transformer.transform(source, result);


	}
}
