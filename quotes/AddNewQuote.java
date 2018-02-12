package quotes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*;

import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Dimension;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;


public class AddNewQuote extends JFrame {
	// the URL to the quotes.xml file
	private static final String quoteFileName = "src/quotes/quotes.xml";
	
	private JLabel lblAddNewQuote;
	private JPanel contentPane;
	private JTextArea txtQuoteText;
	private JTextField txtAuthor;
	private JButton btnAddNewQuote;
	private JTextPane lblMessage;
	
	private static final String SPECIAL_CHARECTORS = "[@#$%&*()_+=|<>?{}\\\\[\\\\]~-]";
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AddNewQuote frame = new AddNewQuote();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public boolean isQuoteValid(String quoteText, String author) {
		Pattern patternSpecialCharactors = Pattern.compile(SPECIAL_CHARECTORS);
		
		boolean isQuoteValid = true;
		String message = "";
		
		Matcher hasSpecialCharactors = patternSpecialCharactors.matcher(quoteText);
		// check no special character
		if (hasSpecialCharactors.find() == true) {
			isQuoteValid = false;
			message = "Your quote contains special character(s)!";
		}
		// check no numbers
		if (quoteText.matches("[0-9]+")) {
			isQuoteValid = false;
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
			isQuoteValid = false;
			// check how similar the existing quote vs the new quote
			for (int i = 0; i < searchRes.getSize(); i++) {
				quoteTmp = searchRes.getQuote(i);
				message = "[Duplicate Quotes] \n " + quoteTmp.getQuoteText() + "(" + quoteTmp.getAuthor() + ")";
				
				System.out.println(message);
				
			}
		}
		
		
		lblMessage.setText(message);
		return isQuoteValid;
	}
	
	public void doValidation (String quoteText, String author) {
		boolean quoteValidation = isQuoteValid(quoteText, author);
		// disable the button if the quote is not valid
		if (quoteValidation) {
			btnAddNewQuote.setEnabled(true);
		} else {
			btnAddNewQuote.setEnabled(false);
		}
	}
	
	public void appendNewQuote(String quoteText, String author) throws Exception{
		 DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        Document document = documentBuilder.parse(quoteFileName);
	        Element root = document.getDocumentElement();
	        
	        Element newQuoteNode = document.createElement("quote");	        
	        Element quoteTextNode = document.createElement("quote-text");
	        Element authorNode = document.createElement("author");
	        
	        newQuoteNode.appendChild(quoteTextNode);
	        newQuoteNode.appendChild(authorNode);
	        
	        quoteTextNode.appendChild(document.createTextNode(quoteText));
	        authorNode.appendChild(document.createTextNode(author));
	        
	        
	        root.appendChild(newQuoteNode);
	        
	        
	        DOMSource source = new DOMSource(document);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        StreamResult result = new StreamResult(quoteFileName);
	        transformer.transform(source, result);
	        
	        
	}

	public void doAddNewQuote() {
		String quoteText = txtQuoteText.getText();
		String author = txtAuthor.getText();
		
		if (!quoteText.equals("") && !author.equals("")) {
			try {
				// we need to validate the quote before save it in XML file
				if (isQuoteValid(quoteText, author)) {
					// when the validation check passes, we will save new quote to the XML file
					appendNewQuote(quoteText, author);

					// empty both text
					txtQuoteText.setText("");
					txtAuthor.setText("");
					
					lblMessage.setText("Successfully add new quote to the file!");
				}
			} catch (Exception ex) {
				
			}
			
		}
	}

	/**
	 * Create the frame.
	 */
	public AddNewQuote() {
		setTitle("Add New Quote");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(200, 200, 500, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panelTitle = new JPanel();
		contentPane.add(panelTitle, BorderLayout.NORTH);

		lblAddNewQuote = new JLabel("Add New Quote");
		lblAddNewQuote.setFont(new Font("Arial Black", Font.PLAIN, 16));
		panelTitle.add(lblAddNewQuote);

		JPanel panelMain = new JPanel();
		contentPane.add(panelMain, BorderLayout.CENTER);
		panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.Y_AXIS));

		JPanel panelQuoteText = new JPanel();
		panelQuoteText.setPreferredSize(new Dimension(10, 230));
		panelMain.add(panelQuoteText);
		panelQuoteText.setLayout(new BoxLayout(panelQuoteText, BoxLayout.X_AXIS));

		JLabel lblNewLabel = new JLabel("Quote Text ");
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelQuoteText.add(lblNewLabel);

		txtQuoteText = new JTextArea();
		txtQuoteText.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String quoteText = txtQuoteText.getText();
				String author = "";
				doValidation(quoteText, author);
			}
		});
		txtQuoteText.setAutoscrolls(false);
		txtQuoteText.setLineWrap(true);
		txtQuoteText.setWrapStyleWord(true);
		txtQuoteText.setFont(new Font("Papyrus", Font.BOLD, 20));
		txtQuoteText.setForeground(new Color(0, 204, 102));
		panelQuoteText.add(txtQuoteText);

		JPanel panelAuthor = new JPanel();
		panelAuthor.setPreferredSize(new Dimension(10, 5));
		panelMain.add(panelAuthor);
		panelAuthor.setLayout(new BoxLayout(panelAuthor, BoxLayout.X_AXIS));

		JLabel lblAuthor = new JLabel("Author       ");
		lblAuthor.setFont(new Font("Tahoma", Font.BOLD, 12));
		panelAuthor.add(lblAuthor);

		txtAuthor = new JTextField();
		txtAuthor.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String quoteText = txtQuoteText.getText();
				String author = txtAuthor.getText();
				doValidation(quoteText, author);
			}
		});
		txtAuthor.setAutoscrolls(false);
		txtAuthor.setFont(new Font("MS Mincho", Font.ITALIC, 16));
		txtAuthor.setPreferredSize(new Dimension(7, 10));
		panelAuthor.add(txtAuthor);
		txtAuthor.setColumns(5);
		
		JPanel panelMessage = new JPanel();
		panelMain.add(panelMessage);
		panelMessage.setLayout(new BorderLayout(0, 0));
		
		lblMessage = new JTextPane();
		lblMessage.setForeground(new Color(255, 0, 0));
		lblMessage.setBackground(SystemColor.control);
		panelMessage.add(lblMessage, BorderLayout.NORTH);

		JPanel panelQuoteButtons = new JPanel();
		panelMain.add(panelQuoteButtons);

		btnAddNewQuote = new JButton("Add New Quote");
		btnAddNewQuote.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				doAddNewQuote();
			}
		});
		panelQuoteButtons.add(btnAddNewQuote);

		JButton btnReset = new JButton("Reset");
		btnReset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				txtQuoteText.setText("");
				txtAuthor.setText("");
			}
		});
		panelQuoteButtons.add(btnReset);
	}

}
