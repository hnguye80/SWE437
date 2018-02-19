package quotes;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class QuoteListTest {
	// the URL to the quotes.xml file
	private static final String quoteFileName = "src/quotes/quotes.xml";
	private static final String TEST_QUOTE_TEXT = "I know that you believe you understand what you think I said, but I am not sure you realize that what you heard is not what I meant.";
	private static final String TEST_AUTHOR = "Richard Nixon";

	QuoteList quoteList;
	QuoteList searchRes;
	Quote quoteTmp;
	String quoteText = "";
	String author = "";

	@Before
	public void setup() {
		QuoteSaxParser qParser = new QuoteSaxParser(quoteFileName);
		// Stores all the quotes from the XML file
		quoteList = qParser.getQuoteList();

	}

	@After
	public void teardown() {
		quoteList = null;
	}

	// does not return empty list for empty author
	@Test(timeout = 1000)
	public void testSearchEmptyAuthor() {
		author = "";
		searchRes = quoteList.search(author, QuoteList.SearchAuthorVal);

		assertTrue("Does not return empty list for empty author", 0 == searchRes.getSize());
	}

	// does not return non empty list for an author
	@Test(timeout = 1000)
	public void testSearchMultipleQuoteForAuthor() {
		author = TEST_AUTHOR;
		searchRes = quoteList.search(author, QuoteList.SearchAuthorVal);

		assertTrue("Does not return non empty list for author: " + author, 1 <= searchRes.getSize());
	}

	// does not return empty list for empty quoteText
	@Test(timeout = 1000)
	public void testSearchEmptyQuoteText() {
		quoteText = "";
		searchRes = quoteList.search(quoteText, QuoteList.SearchTextVal);

		assertTrue("Does not return empty list for empty quoteText", 0 == searchRes.getSize());
	}

	// does not return exactly 1 quote
	@Test(timeout = 1000)
	public void testSearchOneQuoteReturn() {
		quoteText = TEST_QUOTE_TEXT;
		author = TEST_AUTHOR;

		searchRes = quoteList.search(quoteText, QuoteList.SearchTextVal);

		assertTrue("does not return exactly 1 quote", 1 == searchRes.getSize());
	}

	// does not return empty list for empty author and quoteText
	@Test(timeout = 1000)
	public void testSearchEmptyAuthorAndQuoteText() {
		author = "";
		quoteText = "";
		searchRes = quoteList.search(quoteText, QuoteList.SearchBothVal);

		assertTrue("Does not return empty list for empty author and quoteText", 0 == searchRes.getSize());
	}

	@Test
	public void authorNotInListTest() {
		searchRes = quoteList.search("Huan Nguyen", QuoteList.SearchAuthorVal);

		assertTrue("There is no author Huan Nguyen in the list", 0 == searchRes.getSize());
	}

	@Test
	public void quoteNotInListTest() {
		searchRes = quoteList.search("This is a test quote!", QuoteList.SearchAuthorVal);

		assertTrue("There is no [This is a test quote] in the list", 0 == searchRes.getSize());
	}

	// does not match the quote exists in XML file
	@Test(timeout = 1000)
	public void testSearchQuoteTextNotMatch() {
		quoteText = TEST_QUOTE_TEXT;
		author = TEST_AUTHOR;

		searchRes = quoteList.search(quoteText, QuoteList.SearchTextVal);
		if (searchRes.getSize() == 1) {
			quoteTmp = searchRes.getQuote(0);

			assertTrue("does not match quote:\n" + quoteText + " (" + author + ")",
					author.equals(quoteTmp.getAuthor()));
		} else {
			fail("does not return exactly 1 quote");
		}
	}

	// when the quoteList is empty, all search should return an empty list
	// does not return empty list for empty quoteList
	@Test(timeout = 1000)
	public void testSearchEmptyList() {
		QuoteList quoteList = new QuoteList();
		author = TEST_AUTHOR;
		searchRes = quoteList.search(author, QuoteList.SearchAuthorVal);

		assertTrue("does not return empty list for empty quoteList", 0 == searchRes.getSize());
	}

	// test for Null List
	@Test(timeout = 1000)
	public void testSearchNullList() {
		QuoteList quoteList = null;
		author = TEST_AUTHOR;

		try {
			searchRes = quoteList.search(author, QuoteList.SearchAuthorVal);
		} catch (NullPointerException e) {
			return;
		}
		fail("NullPointerException expected!");

		// fail("NullPointerException Expected!");
		// assertTrue("Expected NullPointerException", 0 == searchRes.getSize());
	}

	// test NullPointerException for quoteList
	@Test(timeout = 1000, expected = NullPointerException.class)
	public void testSearchNullPointerException() {
		QuoteList quoteList = new QuoteList();
		quoteTmp = new Quote();

		quoteText = TEST_QUOTE_TEXT;
		author = TEST_AUTHOR;

		quoteTmp.setQuoteText(quoteText);
		quoteTmp.setAuthor(author);

		quoteList.setQuote(null);
		quoteList.setQuote(null);
		quoteList.setQuote(quoteTmp);

		searchRes = quoteList.search(author, QuoteList.SearchAuthorVal);
	}

}
