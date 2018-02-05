package quotes;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;

import javax.swing.JTextArea;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Window.Type;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextPane;
import java.awt.Cursor;


public class MainQuote {

	// the URL to the quotes.xml file
	private static final String quoteFileName = "src/quotes/quotes.xml";
	
	// the main frame
	private static JFrame frmQuoteApplication;
	private static JTextField txtSearch;
	private static JTextArea txtQuoteDescription;
	private static JTextArea txtAuthor;
	private static QuoteList quoteList;
	private static JRadioButton radAuthor;
	private static JRadioButton radQuote;
	private static JRadioButton radBoth;
	private JTable tblSearchHistory;

	private static JTextPane txtSearchResult;

	static ArrayList<String> searchList;
	static ArrayList<String> searchContextList;
	
	// the searchListScope will store search scope at each submission 
	static ArrayList<String> searchListScope;
	
	DefaultTableModel tableModelSearchHistory;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Initialize the window
					MainQuote window = new MainQuote();
					window.frmQuoteApplication.setVisible(true);

					// get the quote list
					QuoteSaxParser qParser = new QuoteSaxParser(quoteFileName);
					// Stores all the quotes from the XML file
					quoteList = qParser.getQuoteList();

					Quote quoteTmp = quoteList.getRandomQuote();
					txtQuoteDescription.setText(" " + quoteTmp.getQuoteText());
					txtAuthor.setText(quoteTmp.getAuthor());

					/* Initialize searchList and searchContextList */
					searchList = new ArrayList<String>();
					searchContextList = new ArrayList<String>();
					searchListScope = new ArrayList<String>();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/* method to update the tableModelSearchHistory; the datasource is the searchList and searchContextList
	 * 
	 * */
	public void updateSearchHistory() {
		int index = 0;

		if (tableModelSearchHistory.getRowCount() > 0) {
			/*
			 * When delete rows in table increasingly, the table will update new row index
			 * after the deletion remove the table backward to avoid the update of the
			 * deletion
			 */
			for (index = tableModelSearchHistory.getRowCount() - 1; index >= 0; index--) {
				tableModelSearchHistory.removeRow(index);
			}
		}

		for (index = 0; index < searchList.size(); index++) {
			tableModelSearchHistory
					.addRow(new Object[] { searchList.get(index).toString(), searchContextList.get(index).toString() });
		}
	}

	
	/**
	 * Print the search result
	 * @param out display the result in the searchResult
	 * @param searchText search String input from user
	 * @param searchScope scope for this search
	*/
	public void printSearch(String searchText, String searchScope) {
		// add a string builder
		StringBuilder searchResult = new StringBuilder();

		if (searchText != null && !searchText.equals("")) { // Received a search request
			int searchScopeInt = QuoteList.SearchBothVal; // Default
			if (searchScope != null && !searchScope.equals("")) { // If no parameter value, let it default.
				if (searchScope.equals("quote")) {
					searchScopeInt = QuoteList.SearchTextVal;
				} else if (searchScope.equals("author")) {
					searchScopeInt = QuoteList.SearchAuthorVal;
				} else if (searchScope.equals("both")) {
					searchScopeInt = QuoteList.SearchBothVal;
				}
			}

			QuoteList searchRes = quoteList.search(searchText, searchScopeInt);
			Quote quoteTmp;

			searchResult.append("<HTML>");

			if (searchRes.getSize() == 0) {
				searchResult.append("<p>Your search - <b>" + searchText + "</b> - did not match any quotes.</p>");
			} else {
				searchResult.append("<dl>");
				for (int i = 0; i < searchRes.getSize(); i++) {
					quoteTmp = searchRes.getQuote(i);
					searchResult.append("<dt>" + quoteTmp.getQuoteText() + "</dt>");
					searchResult.append("<dd>&mdash;" + quoteTmp.getAuthor() + "</dd>");
				}
				searchResult.append("</dl>");
			}
			searchResult.append("<HTML>");
		}
		txtSearchResult.setText(searchResult.toString());
	}

	/**
	 * Execute the search and then call printSearch to display result
	 * @param out update the search history list and the result in the searchResult
	 * @param searchText search String input from user
	*/
	public void doSearch(String searchText) {

		if (!searchText.equals("") && searchText.length() > 0) {
			String searchScope = "";

			if (radAuthor.isSelected() == true) {
				searchScope = "author";
			} else if (radQuote.isSelected() == true) {
				searchScope = "quote";
			} else if (radBoth.isSelected() == true) {
				searchScope = "both";
			}

			System.out.println("searchText = " + searchText + ", scope = " + searchScope);

			// Add the search String into the lists
			if (searchText != null && searchText.length() > 0) {
				searchList.add(searchText);
				searchContextList.add(searchText);
				searchListScope.add(searchScope);
			}

			// Remove the oldest searches if more than 5
			if (searchList.size() > 5) {
				searchList.remove(0);
			}

			if (searchContextList.size() > 5) {
				searchContextList.remove(0);
			}
			
			if (searchListScope.size() > 5) {
				searchListScope.remove(0);
			}
			
			// Done with updating the search lists

			// update the table search history
			updateSearchHistory();

			// print the search result
			printSearch(searchText, searchScope);

		} // end if searchText is not empty

	}

	/**
	 * Create the application.
	 */
	public MainQuote() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		/* The main frame */
		frmQuoteApplication = new JFrame();
		frmQuoteApplication.setType(Type.UTILITY);
		frmQuoteApplication.setTitle("Quote Application");
		frmQuoteApplication.setResizable(false);
		frmQuoteApplication.setBounds(200, 200, 600, 700);
		frmQuoteApplication.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmQuoteApplication.getContentPane().setLayout(null);

		/* The top panel for application title */
		JPanel panelTop = new JPanel();
		panelTop.setBounds(0, 0, 584, 33);
		frmQuoteApplication.getContentPane().add(panelTop);

		JLabel lblQuoteApplication = new JLabel("Quote Generator");
		lblQuoteApplication.setFont(new Font("Arial Black", Font.PLAIN, 16));
		panelTop.add(lblQuoteApplication);

		/* the panel quote, contain quote, author and button to generate next quote */
		JPanel panelQuote = new JPanel();
		panelQuote.setBounds(10, 44, 574, 184);
		frmQuoteApplication.getContentPane().add(panelQuote);
		panelQuote.setLayout(new BoxLayout(panelQuote, BoxLayout.Y_AXIS));

		/* the quote */
		txtQuoteDescription = new JTextArea();
		txtQuoteDescription.setRequestFocusEnabled(false);
		txtQuoteDescription.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		txtQuoteDescription.setLineWrap(true);
		txtQuoteDescription.setForeground(new Color(0, 204, 102));
		txtQuoteDescription.setBackground(SystemColor.control);
		txtQuoteDescription.setWrapStyleWord(true);
		txtQuoteDescription.setFont(new Font("Papyrus", Font.BOLD, 20));
		panelQuote.add(txtQuoteDescription);
		txtQuoteDescription.setColumns(28);
		txtQuoteDescription.setRows(3);
		txtQuoteDescription.setText("QuoteDescription");

		/* the author of that quote */
		txtAuthor = new JTextArea();
		txtAuthor.setRequestFocusEnabled(false);
		txtAuthor.setFont(new Font("MS Mincho", Font.ITALIC, 16));
		txtAuthor.setBackground(SystemColor.control);
		txtAuthor.setText("Author");
		txtAuthor.setRows(2);
		txtAuthor.setColumns(6);
		panelQuote.add(txtAuthor);

		/* the button to generate next quote */
		JButton btnNextQuote = new JButton("Get Next Random Quote");
		panelQuote.add(btnNextQuote);
		btnNextQuote.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Quote quoteTmp = quoteList.getRandomQuote();
				txtQuoteDescription.setText(" " + quoteTmp.getQuoteText());
				txtAuthor.setText(quoteTmp.getAuthor());
			}
		});

		/* the panel search */
		JPanel panelSearch = new JPanel();
		panelSearch.setBounds(10, 239, 263, 117);
		frmQuoteApplication.getContentPane().add(panelSearch);
		panelSearch.setLayout(null);

		txtSearch = new JTextField();
		txtSearch.setToolTipText("Search");
		txtSearch.setBounds(10, 1, 250, 20);
		panelSearch.add(txtSearch);
		txtSearch.setColumns(20);

		JLabel lblNewLabel = new JLabel("Search By:");
		lblNewLabel.setBounds(10, 32, 70, 14);
		panelSearch.add(lblNewLabel);

		radAuthor = new JRadioButton("Author");
		radAuthor.setBounds(82, 54, 70, 23);
		panelSearch.add(radAuthor);

		radQuote = new JRadioButton("Quote");
		radQuote.setBounds(10, 54, 70, 23);
		panelSearch.add(radQuote);

		radBoth = new JRadioButton("Both");
		radBoth.setSelected(true);
		radBoth.setBounds(150, 54, 70, 23);
		panelSearch.add(radBoth);

		ButtonGroup searchScope = new ButtonGroup();
		searchScope.add(radAuthor);
		searchScope.add(radQuote);
		searchScope.add(radBoth);

		/* reset button */
		JButton btnReset = new JButton("Reset");
		btnReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				txtSearch.setText("");
				radBoth.setSelected(true);
			}
		});
		btnReset.setBounds(133, 83, 120, 23);
		panelSearch.add(btnReset);

		/* search button */
		JButton btnSearch = new JButton("Search Quote");
		btnSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				String searchText = txtSearch.getText();
				doSearch(searchText);
			}
		});
		btnSearch.setBounds(10, 83, 120, 23);
		panelSearch.add(btnSearch);

		/* the panel search history */
		JPanel panelSearchHistory = new JPanel();
		panelSearchHistory.setBounds(283, 239, 301, 117);
		frmQuoteApplication.getContentPane().add(panelSearchHistory);

		tableModelSearchHistory = new DefaultTableModel();
		tableModelSearchHistory.addColumn("User Searchs");
		tableModelSearchHistory.addColumn("Community Searchs");

		tblSearchHistory = new JTable(tableModelSearchHistory);
		tblSearchHistory.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		tblSearchHistory.setRowSelectionAllowed(false);
		tblSearchHistory.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// get the location	of the cell
				int row = tblSearchHistory.rowAtPoint(arg0.getPoint());
				int column = tblSearchHistory.columnAtPoint(arg0.getPoint());
				
				String textFromCell = "";
				String searchScope = "";
				
				if (row >=0 && column >= 0) {
					// get the text back from the cell
					textFromCell = tblSearchHistory.getValueAt(row, column).toString();
					
					txtSearch.setText(textFromCell);
					
					// get the searchScope from the list of scope
					searchScope = searchListScope.get(row);
					
					if (searchScope.equals("both")) {
						radBoth.setSelected(true);
					}
					else if (searchScope.equals("quote")) {
						radQuote.setSelected(true);
					} 
					else if (searchScope.equals("author")) {
						radAuthor.setSelected(true);
					} 
					
					// execute the doSearch method
					String searchText = txtSearch.getText();
					doSearch(searchText);
				}
				

			}
		});
		tblSearchHistory.setFont(new Font("Tahoma", Font.PLAIN, 14));
		tblSearchHistory.setBorder(new LineBorder(new Color(0, 0, 0), 1, true));
		tblSearchHistory.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tblSearchHistory.getColumnModel().getColumn(0).setPreferredWidth(120);
		tblSearchHistory.getColumnModel().getColumn(1).setPreferredWidth(120);

		JLabel lblUserSearch = new JLabel("User Searchs");
		panelSearchHistory.add(lblUserSearch);

		JLabel lblCommunitySearches = new JLabel("Community Searches");
		panelSearchHistory.add(lblCommunitySearches);
		panelSearchHistory.add(tblSearchHistory);

		/* the panel search result */
		JPanel panelSearchResult = new JPanel();
		panelSearchResult.setBounds(10, 367, 574, 293);
		frmQuoteApplication.getContentPane().add(panelSearchResult);
		panelSearchResult.setLayout(new BoxLayout(panelSearchResult, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel_1 = new JLabel("Search Result");
		lblNewLabel_1.setRequestFocusEnabled(false);
		panelSearchResult.add(lblNewLabel_1);

		txtSearchResult = new JTextPane();
		txtSearchResult.setBackground(SystemColor.controlLtHighlight);
		txtSearchResult.setContentType("text/html");
		txtSearchResult.setPreferredSize(new Dimension(550, 250));
		panelSearchResult.add(txtSearchResult);
		


	}
}
