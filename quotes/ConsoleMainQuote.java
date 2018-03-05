package quotes;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ConsoleMainQuote {
	// the URL to the quotes.xml file
	private static final String quoteFileName = "src/quotes/quotes.xml";
	private static final String TEST_QUOTE_TEXT = "I know that you believe you understand what you think I said, but I am not sure you realize that what you heard is not what I meant.";
	private static final String TEST_AUTHOR = "Richard Nixon";

	static QuoteList quoteList;
	static QuoteList searchRes;
	static Quote quoteTmp;

	static String quoteText = "";
	static String author = "";
	static String keyword = "";
	static Boolean isFound = false;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QuoteSaxParser qParser = new QuoteSaxParser(quoteFileName);
		// Stores all the quotes from the XML file
		quoteList = qParser.getQuoteList();
		
		keyword = "believe, story, life, writing";
		searchRes = quoteList.search(keyword, QuoteList.SearchKeywordVal);
		
		System.out.println("search Result Size = " + searchRes.getSize());
		
		if (searchRes.getSize() >= 1) {
			for (int i = 0; i < searchRes.getSize(); i++) {
				quoteTmp = searchRes.getQuote(i);

				if (quoteTmp.getKeyword().toLowerCase().indexOf(keyword.toLowerCase()) != -1) {
					isFound = true;
					break;
				}
			}
			
			if (isFound == false) {
				System.out.print("does not match find quotes with keyword [" + keyword + "]:\n");
			}
		}
		else {
			System.out.println("the search result should not be empty");
		}
		
		/*
		String newKeyword = "story, life, writing";		
		String[] keywordList = newKeyword.split(",", -1);
		
		for(int i = 0; i < keywordList.length; i++) {
			System.out.print("[" + keywordList[i].trim() + "]:\n");
		}
		*/
		
		/*
		if (!newKeyword.toLowerCase().matches("(?i).*" + keyword + ".*") ) {
			System.out.print("find quotes with keyword [" + keyword + "]:\n");
		}
		else {
			System.out.print("not match!");
		}
		*/	
	}

}
