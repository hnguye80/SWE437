import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class QuoteServerTest {
	// general setting
	final String TEST_BROWSER = "firefox";
	final String TEST_URL = "https://cs.gmu.edu:8443/offutt/servlet/quotes.quoteserve";
	final int DELAY_TIME = 1000;

	// setting for testing
	final String SITE_TITLE = "Quotes Server";
	final String TEST_SEARCH_QUOTE_TEXT = "test cases";
	final String TEST_SEARCH_AUTHOR = "Jeff offutt";

	// the testBroswer that is used to test
	WebDriver testBroswer;
	WebElement theElement;

	@Before
	public void setUp() throws Exception {
		// setup the browser used for testing
		if (TEST_BROWSER.toLowerCase().equals("firefox")) {
			// Link the driver for FireFox
			System.setProperty("webdriver.gecko.driver", "libs/FireFoxDriver/geckodriver.exe");
			testBroswer = new FirefoxDriver();
		}
		testBroswer.get(TEST_URL);
	}

	@After
	public void tearDown() throws Exception {
		testBroswer.quit();
		// testBroswer.close();
	}

	// method to enter the search query, searchScope then submit the form
	private void submitSearchText(String searchScope, String searchQuery) {

		// choose the searchScope as search both quote and author
		theElement = testBroswer.findElement(By.cssSelector("input[type='radio'][value='" + searchScope + "']"));
		theElement.click();

		// enter search value for the searchText
		theElement = testBroswer.findElement(By.name("searchText"));
		theElement.sendKeys(searchQuery);

		// Now submit the form. WebDriver will find the form for us from the element
		theElement.submit();
		
		// after submit the search query, we need to delay a little time to allow
		// the client update the search result from server
		try {
			Thread.sleep(DELAY_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/***********************
	 * The First base test: POST
	 ************************************************/

	// Test whether the Quote Server is accessed
	@Test
	public void accessQuoteServerTest() {
		String title = testBroswer.getTitle();
		assertTrue("Fail to access the website @\n" + TEST_URL, title.equals(SITE_TITLE));

	}

	// Test get random quote when the button [Get Another Random Quote] is clicked
	@Test
	public void getRandomQuoteTest() {
		// Get the current quote
		String theCurrentQuote = testBroswer.findElement(By.tagName("div")).getText();
		System.out.println("theCurrentQuote ::" + theCurrentQuote);

		// Find the text input element by its type and value
		theElement = testBroswer.findElement(By.cssSelector("input[type='submit'][value='Get Another Random Quote']"));

		// Now submit the form. WebDriver will find the form for us from the element
		theElement.click();

		String theNewQuote = testBroswer.findElement(By.tagName("div")).getText();
		System.out.println("theNewQuote ::" + theNewQuote);

		assertTrue("Fail to get random quote \ntheCurrentQuote::" + theCurrentQuote + "\ntheNewQuote::" + theNewQuote,
				!theCurrentQuote.equals(theNewQuote));
	}

	// Test whether the searchResult is empty before submitting search
	@Test
	public void searchResultNotDisplayBeforeSearchTest() {
		// the searchResult is stored in the <dl> HTML tag
		// we will test whether we find this tag before doing the search
		boolean isSearchResultAppeared = testBroswer.findElements(By.tagName("dl")).size() > 0;

		assertTrue("The search result list should not appear when user does not do the searching",
				isSearchResultAppeared == false);
	}

	// Test whether the searchResult is returned after submitting search
	@Test
	public void searchResultReturnWithEmptyListTest() {
		String searchQuery = "123456789";
		submitSearchText("both", searchQuery);
		// when the result list is empty, the message the for search will store in the
		// HTML <p> tag
		boolean isSearchResultAppeared = testBroswer.findElements(By.tagName("p")).size() > 1;

		assertTrue("The search result list should be empty for " + searchQuery, isSearchResultAppeared == true);
	}

	// Test whether the searchResult is returned after submitting search
	@Test
	public void searchResultReturnWithScopeQuotePOSTTest() {
		submitSearchText("quote", TEST_SEARCH_QUOTE_TEXT);
		// the searchResult is stored in the <dl> HTML tag
		// we will test whether we find this tag before doing the search
		boolean isSearchResultAppeared = testBroswer.findElements(By.tagName("dl")).size() > 0;
		assertTrue("The search result list should not be empty", isSearchResultAppeared == true);
	}

	// Test whether the searchResult is returned after submitting search
	@Test
	public void searchResultReturnWithScopeAuthorPOSTTest() {
		submitSearchText("author", TEST_SEARCH_AUTHOR);
		// the searchResult is stored in the <dl> HTML tag
		// we will test whether we find this tag before doing the search
		boolean isSearchResultAppeared = testBroswer.findElements(By.tagName("dl")).size() > 0;
		assertTrue("The search result list should not be empty", isSearchResultAppeared == true);
	}

	// Test whether the searchResult is returned after submitting search
	@Test
	public void searchResultReturnWithScopeBothPOSTTest() {

		submitSearchText("both", TEST_SEARCH_QUOTE_TEXT);
		// the searchResult is stored in the <dl> HTML tag
		// we will test whether we find this tag before doing the search
		boolean isSearchResultAppeared = testBroswer.findElements(By.tagName("dl")).size() > 0;
		assertTrue("The search result list should not be empty", isSearchResultAppeared == true);
	}

	// Test whether the reset button works
	// we will fill out the searchText input box, then hit reset
	// the searchResult has to be empty before and after the reset button click
	@Test
	public void resetButtonTest() {
		// the searchResult is stored in the <dl> HTML tag
		// we will test whether we find this tag before doing the search
		boolean isSearchResultAppeared_before = testBroswer.findElements(By.tagName("dl")).size() > 0;

		// choose the searchScope as search both quote and author
		theElement = testBroswer.findElement(By.cssSelector("input[type='radio'][value='quote']"));
		theElement.click();

		// enter search value for the searchText
		theElement = testBroswer.findElement(By.name("searchText"));
		theElement.sendKeys("Jeff Offutt");

		// click reset button
		theElement = testBroswer.findElement(By.name("reset"));
		theElement.click();

		// the searchResult is stored in the <dl> HTML tag
		// we will test whether we find this tag before doing the search
		boolean isSearchResultAppeared_after = testBroswer.findElements(By.tagName("dl")).size() > 0;
		assertTrue("Fail to reset the searchText content",
				isSearchResultAppeared_before == isSearchResultAppeared_after);
	}

	/***********************
	 * The First base test: GET
	 ************************************************/

	// Test whether the searchResult is returned after submitting search
	@Test
	public void searchResultReturnWithScopeQuoteGETTest() {
		submitSearchText("quote", TEST_SEARCH_QUOTE_TEXT);
		// the searchResult is stored in the <dl> HTML tag
		// the number of records returned is the size of elements found
		int numberOfRecordReturned_POST = testBroswer.findElements(By.tagName("dt")).size();
		
		// get the list of search list for GET
		// the open another one for GET method
		// form the query string
		String queryString = "?searchText=" + TEST_SEARCH_QUOTE_TEXT+ "&searchScope=quote";
		testBroswer.get(TEST_URL + queryString);
		
		// the number of records returned is the size of elements found
		int numberOfRecordReturned_GET = testBroswer.findElements(By.tagName("dt")).size();
		assertTrue("The search result list is different for POST and GET", numberOfRecordReturned_POST == numberOfRecordReturned_GET);
	}
	
	// Test whether the searchResult is returned after submitting search
	@Test
	public void searchResultReturnWithScopeAuthorGETTest() {
		submitSearchText("author", TEST_SEARCH_AUTHOR);
		// the searchResult is stored in the <dl> HTML tag
		// the number of records returned is the size of elements found
		int numberOfRecordReturned_POST = testBroswer.findElements(By.tagName("dt")).size();
		
		// get the list of search list for GET
		// the open another one for GET method
		// form the query string
		String queryString = "?searchText=" + TEST_SEARCH_AUTHOR+ "&searchScope=author";
		testBroswer.get(TEST_URL + queryString);
		
		// the number of records returned is the size of elements found
		int numberOfRecordReturned_GET = testBroswer.findElements(By.tagName("dt")).size();
		assertTrue("The search result list is different for POST and GET", numberOfRecordReturned_POST == numberOfRecordReturned_GET);
	}
	
	// Test whether the searchResult is returned after submitting search
	@Test
	public void searchResultReturnWithScopeBothGETTest() {
		submitSearchText("both", TEST_SEARCH_QUOTE_TEXT);
		// the searchResult is stored in the <dl> HTML tag
		// the number of records returned is the size of elements found
		int numberOfRecordReturned_POST = testBroswer.findElements(By.tagName("dt")).size();
		
		// get the list of search list for GET
		// the open another one for GET method
		// form the query string
		String queryString = "?searchText=" + TEST_SEARCH_QUOTE_TEXT+ "&searchScope=both";
		testBroswer.get(TEST_URL + queryString);
		
		// the number of records returned is the size of elements found
		int numberOfRecordReturned_GET = testBroswer.findElements(By.tagName("dt")).size();
		assertTrue("The search result list is different for POST and GET", numberOfRecordReturned_POST == numberOfRecordReturned_GET);
	}
	
}
