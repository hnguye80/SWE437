package quotes;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuoteListKeywordSearchTest {
	// the URL to the quotes.xml file
	private static final String quoteFileName = "src/quotes/quotes.xml";
	
	private static final String TEST_QUOTE_TEXT = "When writing the story of your life, don't let anyone else hold the pen";
	private static final String TEST_AUTHOR = "Harley Davidson";
	private static final String TEST_KEYWORDS = "story, life, writing";

	QuoteList quoteList;
	QuoteList searchRes;
	Quote quoteTmp;

	String quoteText = "";
	String author = "";
	String keyword = "";
	Boolean isFound = false;
	QuoteAdder quoteAdder;

	@Before
	public void setup() {
		QuoteSaxParser qParser = new QuoteSaxParser(quoteFileName);
		// Stores all the quotes from the XML file
		quoteList = qParser.getQuoteList();
		// create new adder
		quoteAdder = new QuoteAdder(quoteFileName);
		
		quoteText = "";
		author = "";
		keyword = "";
		isFound = false;
	}

	@After
	public void teardown() {
		quoteList = null;
		quoteAdder = null;
	}

	@Test(timeout = 1000)
	public void testEmptyKeywordSearch() {
		keyword = "";
		searchRes = quoteList.search(keyword, QuoteList.SearchKeywordVal);
		assertTrue("Does not return empty list for empty keyword", 0 == searchRes.getSize());
	}

	public void searchAKeyword(String keyword) {				
		isFound = false;
		searchRes = quoteList.search(keyword, QuoteList.SearchKeywordVal);
		
		if (searchRes.getSize() >= 1) {
			for (int i = 0; i < searchRes.getSize(); i++) {
				quoteTmp = searchRes.getQuote(i);

				if (quoteTmp.getKeyword().toLowerCase().indexOf(keyword.toLowerCase()) != -1
					||
					!quoteTmp.getKeyword().toLowerCase().matches ("(?i).*" + keyword.toLowerCase() + ".*")
					) {
					isFound = true;
					break;
				}
			}
			
			assertTrue("does not match find quotes with keyword [" + keyword + "]:\n", isFound == true);
		} else {
			fail("the search result should not be empty");
		}
	}
	
	@Test(timeout = 1000)
	public void testAKeywordSearch() {
		keyword = "believe";
		searchAKeyword(keyword);
	}
	
	@Test(timeout = 1000)
	public void testAnotherKeywordSearch() {
		keyword = "experienced";
		searchAKeyword(keyword);
	}

	@Test(timeout = 1000)
	public void testKeywordJustAddedSearch() {
		// validate the quote
		quoteAdder.validQuote(TEST_QUOTE_TEXT, TEST_AUTHOR);
		if (quoteAdder.isQuoteValid()) {
			try
			{
				// add new quote to the XML file
				quoteAdder.appendNewQuote(TEST_QUOTE_TEXT, TEST_AUTHOR, TEST_KEYWORDS);
				
				// then search for the new added keyword
				searchAKeyword(TEST_KEYWORDS);
			} catch (Exception ex) 
			{
				fail(ex.toString());
			}
		}		
	}
	
	@Test(timeout = 1000)
	public void testMultipleKeywordsSearch() {
		keyword = "story, writing";
		searchAKeyword(keyword);
	}
	
}
