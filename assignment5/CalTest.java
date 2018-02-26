package assignment5;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class CalTest {

	public int month1, day1, month2, day2, year, expectedResult, actualResult;

	public CalTest(int month1, int day1, int month2, int day2, int year, int expectedResult) {
		this.month1 = month1;
		this.day1 = day1;
		this.month2 = month2;
		this.day2 = day2;
		this.year = year;
		this.expectedResult = expectedResult;
	}

	@Parameters
	public static Collection<Object[]> parameters() {
		Object[][] dataDriven = new Object[][] {
			// days from the same day and same month for not a leap year
			{ 1, 1, 1, 1, 2018, 0 },
			{ 1, 1, 1, 31, 2018, 30 },
			{ 2, 1, 2, 28, 2018, 27 },
			{ 3, 1, 3, 31, 2018, 30 },
			{ 4, 1, 4, 30, 2018, 29 },
			{ 5, 1, 5, 31, 2018, 30 },
			{ 6, 1, 6, 30, 2018, 29 },
			{ 7, 1, 7, 31, 2018, 30 },
			{ 8, 1, 8, 31, 2018, 30 },
			{ 9, 1, 9, 30, 2018, 29 },
			{ 10, 1, 10, 31, 2018, 30 },
			{ 11, 1, 11, 30, 2018, 29 },
			{ 12, 1, 12, 31, 2018, 30 },
			
			// test fail return days for days in the same month
			{ 4, 1, 4, 31, 2018, 29 },
			{ 6, 1, 6, 31, 2018, 29 },
			{ 9, 1, 9, 31, 2018, 29 },
			{ 11, 1, 11, 31, 2018, 29 },
			
			// days from same month for leap year 2020	
			{ 2, 1, 2, 29, 2020, 28 },
			
			// days from different month for not a leap year
			{ 1, 1, 2, 28, 2018, 58 },
			
			// days from different month for leap year 2020	
			{ 1, 1, 3, 31, 2020, 90 },
			
			// days from different month for not a leap year
			{ 2, 27, 3, 1, 2020, 3 },
			
			// days in one year
			{ 1, 1, 12, 31, 2018, 364 },
			
			// days in one leap year
			{ 1, 1, 12, 31, 2020, 365 }
		};
		return Arrays.asList(dataDriven);
	}
	
	public String formatMessage(int month1, int day1, int month2, int day2, int year, int expectedResult, int actualResult) {
		String message = new String();
		
		message = "Test days calculated from [" + month1 + "/" + day1 + "]" + 
				   " to [" + month2 + "/" + day2 + "/" + year + "]" +  
				   "\nExpecting: " + expectedResult + 
				   "\nActual: " + actualResult;
		
		return message.toString();
	}

	// multiple situations for same month testing
	@Test(timeout = 1000)
	public void testCal() {
		actualResult = Cal.cal(month1, day1, month2, day2, year);
		assertTrue(formatMessage(month1, day1, month2, day2, year, expectedResult, actualResult), 
				expectedResult == actualResult);
	}

}