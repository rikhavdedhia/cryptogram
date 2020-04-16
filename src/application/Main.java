package application;
	
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.Random;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.HashMap;


public class Main extends Application {
	
	static ArrayList<Puzzles> list = new ArrayList<Puzzles>();
	static String hint, sentence, encryptSentence;
	static int diff;
	static Puzzles currPuzzle;
	protected static Stage st;
	static Random rand;
	static TextField textField[];
	static HBox hpanep[];
	static Label encrypt_l[];
	static GridPane gPanePMain = new GridPane();
	static GridPane gPaneH = new GridPane();
	static GridPane gPaneP = new GridPane();
	static boolean[] alphaGuessed;
	static int[] freq;
	static HashMap<Character, ArrayList<Integer>> textboxes;
	static ArrayList<Integer> textboxIndices;
	static boolean doubleAlpha = false;
	static int hintcnt;
	
	@Override
	public void start(Stage primaryStage) {
		st = primaryStage;
		st.setResizable(false);
		st.setTitle("Cryptogram");
		try{
			System.out.println("SPM Project: A Cryptogram\nCode by:- \n\tRikhav Nilesh Dedhia\n\tRishikesh Maddi"
					+ "\n\tDaksh Parikh\n\tKrina Karia");
			FileInputStream file_ip = new FileInputStream("Puzzle_database.txt");
			ObjectInputStream file = new ObjectInputStream(file_ip);
			try{
				while (true){
					Puzzles P = (Puzzles) file.readObject();
					list.add(P);
				}
			}
			catch(EOFException eof){}
			file.close();
	        buildGui();
			}
		catch (FileNotFoundException e){
	        newPuzzle();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if(list.size() == 0) newPuzzle();
		else buildGui();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	static void buildGui(){
		try {
			st.setHeight(255);
		    st.setWidth(670);
			HBox hpaneb = new HBox(10);
			VBox vpanel = new VBox(10);
			GridPane gpane = new GridPane();
			Scene scene = new Scene(gpane,400,400);
			
			Button admin = new Button("Admin");
//			Button ExitB = new Button("Exit");
			Button Reset = new Button("Reset");
			Button newPuzz = new Button("New");
			Button HintMe = new Button("Give Hint");
			Button Submit = new Button("Submit");
			Button About = new Button("About");
			
			Label UnusedAlpha = new Label("Unused Alphabets are: -");
			Label AlphaFre = new Label("Frequency of each Alphabets is: -");
			Label UnusedAlphaV = new Label("");
			Label AlphaFreV = new Label("");
			Label hint_l = new Label("");
			Label diff_l = new Label("");

			Font fSize = new Font(25);
			
			admin.setPrefWidth(100);
//			ExitB.setPrefWidth(100);
			Reset.setPrefWidth(100);
			HintMe.setPrefWidth(100);
			newPuzz.setPrefWidth(100);
			Submit.setPrefWidth(100);
			About.setPrefWidth(100);
			
			UnusedAlphaV.setFont(fSize);
			AlphaFreV.setFont(fSize);
			hint_l.setFont(fSize);
			diff_l.setFont(fSize);
			
			Reset.setDisable(true);
			HintMe.setDisable(true);
			Submit.setDisable(true);
			
			hpaneb.setStyle("-fx-padding: 10;" + 
	                  "-fx-border-style: solid inside;" + 
	                  "-fx-border-width: 2;" +
	                  "-fx-border-insets: 5;" + 
	                  "-fx-border-radius: 0;" + 
	                  "-fx-border-color: blue;");
			
			vpanel.setStyle("-fx-padding: 10;" + 
	                  "-fx-border-style: solid inside;" + 
	                  "-fx-border-width: 2;" +
	                  "-fx-border-insets: 5;" + 
	                  "-fx-border-radius: 0;" + 
	                  "-fx-border-color: blue;");
			
			gPanePMain.setStyle("-fx-padding: 10;" + 
	                  "-fx-border-style: solid inside;" + 
	                  "-fx-border-width: 2;" +
	                  "-fx-border-insets: 5;" + 
	                  "-fx-border-radius: 0;" + 
	                  "-fx-border-color: blue;");
			
			gPaneH.setStyle("-fx-padding: 10;" + 
	                  "-fx-border-style: solid inside;" + 
	                  "-fx-border-width: 2;" +
	                  "-fx-border-insets: 5;" + 
	                  "-fx-border-radius: 0;" + 
	                  "-fx-border-color: blue;");
			
			admin.setOnAction(e -> {
				gPanePMain.getChildren().removeAll();
				gPanePMain.getChildren().clear();
		        verifyAdmin();
		    });
				
			newPuzz.setOnAction(e ->{
				hintcnt = 0;
				textboxes = new HashMap<Character, ArrayList<Integer>>();
				alphaGuessed = new boolean[26];
				freq = new int[26];
				gPaneP.getChildren().removeAll();
				gPaneP.getChildren().clear();
				gPanePMain.getChildren().removeAll();
				gPanePMain.getChildren().clear();
				gPaneH.getChildren().removeAll();
				gPaneH.getChildren().clear();
				Reset.setDisable(false);
				Submit.setDisable(false);
				HintMe.setDisable(false);
				rand = new Random();
				currPuzzle = list.get(rand.nextInt(list.size()));
				sentence = currPuzzle.getSentence();
				hint = currPuzzle.getHint();
				diff = currPuzzle.getDiff();
				hint_l.setText("Puzzle is about: - " + hint);
				diff_l.setText("Difficulty of Puzzle: - " + diff);
				gPaneH.add(hint_l,0,0);
				gPaneH.add(diff_l,0,1);
				encryptSentence = encrypt(sentence);
				textField = new TextField[encryptSentence.length()];
				encrypt_l = new Label[encryptSentence.length()];
				System.out.println(sentence);
				System.out.println(encryptSentence);
				int cnt = 0;
				int j = 0;
				for(int i = 0; i < encryptSentence.length(); i++){
					cnt++;
					
					textField[i] = new TextField();
					textField[i].setTextFormatter(new TextFormatter<String>((Change change) -> {
			            String newText = change.getControlNewText();
			            if (newText.length() > 1) {
			                return null ;
			            }
			            else{
			            	if(newText.length() != 0){
			            		if(!Character.isAlphabetic(newText.charAt(0))){
				                     return null;
				            	}
			            		if(!Character.isUpperCase(newText.charAt(0))){
			            			change.setText(newText.toUpperCase());
			            		}
			            	}
			                return change ;
			            }
			        }));
					textField[i].setMaxWidth(35);
					final int index = i;
					textField[i].textProperty().addListener((obs, oldVal, newVal) -> {
				        System.out.println("Text of Textfield on index " + index + " changed from " + oldVal
				                + " to " + newVal);
				        if(textboxIndices.size() > 0){
				        	return;
				        }
				        //10 - Check if alphabet entered twice by - Rikhav
				        if(newVal.length() > 0){
				        	if(checkAlpha(newVal.charAt(0))){
				        		Alert alert = new Alert(AlertType.ERROR);
				                alert.setTitle("Error");
//				                alert.setHeaderText("");
				                alert.setContentText("Alphabet \""+newVal + "\" already used");
				                alert.showAndWait();
				                doubleAlpha = true;
				                textField[index].setText("");
				                return;
				        	}
				        }
				        if(doubleAlpha){
				        	doubleAlpha = false;
				        	return;
				        }				        
				        updateTextBoxes(encryptSentence.charAt(index), newVal);
				        if(oldVal.length() > 0) alphaGuessed[oldVal.charAt(0) - 65] = false;
//				        if(newVal.length() > 0) alphaGuessed[newVal.charAt(0) - 65] = true;
				        UnusedAlphaV.setText(getUnused());
				        vpanel.getChildren().removeAll();
				        vpanel.getChildren().clear();
				        vpanel.getChildren().addAll(UnusedAlpha,UnusedAlphaV, AlphaFre,AlphaFreV);
				    });
					if(encryptSentence.charAt(i) != ' '){
						gPaneP.add(textField[i], i%18, j + 1);
						freq[encryptSentence.charAt(i) - 65]++;
						if(textboxes.containsKey(encryptSentence.charAt(i))){
							textboxIndices = textboxes.get(encryptSentence.charAt(i));
						}
						else{
							textboxIndices = new ArrayList<Integer>();
						}
						textboxIndices.add(i);
						textboxes.put(encryptSentence.charAt(i), textboxIndices);
						textboxIndices = new ArrayList<Integer>();
					}
					else{
						textField[i].setDisable(true);
						gPaneP.add(textField[i], i%18, j + 1);
					}
					encrypt_l[i] = new Label("  " + Character.toString(encryptSentence.charAt(i)));
					gPaneP.add(encrypt_l[i], i%18, j+2);
					if(cnt == 18){
						j+=2;
						cnt = 0;
					}
				}
				gPanePMain.add(gPaneP, 0, 0);
				AlphaFreV.setText(getFre());
				UnusedAlphaV.setText(getUnused());
				vpanel.getChildren().removeAll();
		        vpanel.getChildren().clear();
		        vpanel.getChildren().addAll(UnusedAlpha,UnusedAlphaV, AlphaFre,AlphaFreV);
				st.setHeight(420 + (53 * ((encryptSentence.length() / 18) + 1)));//65
				System.out.println(textboxes);
			});
			
			st.setOnCloseRequest(new EventHandler<WindowEvent>() {
	          public void handle(WindowEvent we) {
	        	  try {
						file_write();
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	              System.out.println("Stage is closing");
	          }});
		          
			Reset.setOnAction(e ->{
				for(int i = 0; i < encryptSentence.length(); i++){
					if(textField[i].getText() != "") textField[i].setText("");
				}
				hintcnt = 0;
				HintMe.setDisable(false);
			});
			
			//11 - To submit the puzzle. by - Rishikesh and Krina
			Submit.setOnAction(e -> {
				for(int i = 0; i < encryptSentence.length(); i++){
					if(textField[i].isDisable()) continue;
					if(textField[i].getText().length() == 0){
						Alert alert = new Alert(AlertType.INFORMATION);
		                alert.setTitle("Messsage");
//		                alert.setHeaderText("");
		                alert.setContentText("Please fill all the boxes before submitting");
		                alert.showAndWait();
		                return;
					}
					if(textField[i].getText().charAt(0) != sentence.charAt(i)){
						Alert alert = new Alert(AlertType.INFORMATION);
		                alert.setTitle("Messsage");
//		                alert.setHeaderText("");
		                alert.setContentText("Incorrect guess");
		                alert.showAndWait();
		                Reset.fire();
		                return;
					}
				}
				Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Messsage");
                alert.setContentText("You Won");
                alert.showAndWait();
                newPuzz.fire();
                return;
			});
			
			//15 - To display "About Game" by - Rishikesh
			About.setOnAction(e -> {
				Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("About the game");
                alert.setContentText("Cryptogram are short sentences which are replaced by an encrypted sentences"
                		+ "using substitution cipher. Each alphabet is mapped to one and only one other alphabet. This"
                		+ " mapping is used to do the substitution.\n\nTo play the game, click on \"New\" and a new"
                		+ " puzzle will pop-up. Keep guessing the words by entering alphabet into textboxes.\n\nFor"
                		+ " help, you can use the Frequency of each alphabet used and the \"Alphabet Used\" column to "
                		+ "track the alphabet you have already used.\n\nOnce you think you have guessed the correct"
                		+ "sentence, click on \"Submit\" to get it checked.\n\nTo add a new puzzle to the list, click"
                		+ " on \"Admin\", login using the proper credentials and add new puzzles.");
                alert.showAndWait();
			});
			
			//14 - To give hint by - Rikhav and Daksh
			HintMe.setOnAction(e -> {
				ArrayList<Integer> notGuessed = getNotGuessed(); 
				rand = new Random();
				if(notGuessed.size() == 0){
					Alert alert = new Alert(AlertType.INFORMATION);
	                alert.setTitle("Hint");
	                alert.setContentText("There are no Alphabets left to hint");
	                alert.showAndWait();
	                return;
				}
				if(hintcnt == 5){
					Alert alert = new Alert(AlertType.INFORMATION);
	                alert.setTitle("Hint");
	                alert.setContentText("You have used all 5 hints");
	                alert.showAndWait();
	                HintMe.setDisable(true);
	                return;
				}
				hintcnt++;
				int x = rand.nextInt(notGuessed.size());
				char encryptchar = encryptSentence.charAt(notGuessed.get(x));
//				updateTextBoxes(encryptchar, Character.toString(sentence.charAt(notGuessed.get(x))));
				textField[notGuessed.get(x)].setText(Character.toString(sentence.charAt(notGuessed.get(x))));
			});
			
			hpaneb.getChildren().addAll(Reset, newPuzz, HintMe, admin,Submit, About);
			if(UnusedAlphaV.getText().length() == 0) vpanel.getChildren().addAll(UnusedAlpha, AlphaFre);
			else vpanel.getChildren().addAll(UnusedAlpha,UnusedAlphaV, AlphaFre,AlphaFreV);
			gpane.add(hpaneb,0,0);
			gpane.add(gPaneH, 0, 1);
			gpane.add(gPanePMain,0,2);
			gpane.add(vpanel, 0, 3);
			st.setScene(scene);
			st.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static void newPuzzle(){
		
		Label hint_l = new Label("Hint(Max 30 char)");
		Label sentence_l = new Label("Sentence(only alphabets & space allowed)");
		Label errors_l = new Label("");
		Label diff_l = new Label("Difficulty(1 - 5)");
		Label errord_l = new Label("");
		Label errord_e = new Label("");
		
		String diffLevel[] = {"1","2","3","4","5"}; 

		ComboBox<String> combo_box = new ComboBox<String>(FXCollections.observableArrayList(diffLevel));
		
		TextField hint_txt = new TextField("");
		TextField sentence_txt = new TextField("");
		
		hint_txt.setTextFormatter(new TextFormatter<String>((Change change) -> {
            String newText = change.getControlNewText();
            if (newText.length() > 30) {
                return null ;
            }
            return change ;
        }));
		
		Button add = new Button("Add Puzzle");
		Button back = new Button("Back");
		
		HBox hpane = new HBox(10);
		GridPane root = new GridPane();
		
		sentence_txt.setTextFormatter(new TextFormatter<String>((Change change) -> {
            String newText = change.getControlNewText();
            if(newText.length() != 0){
        		if((newText != null) && (newText.matches("^[a-zA-Z ]*$"))){
                    return change;
            	}
        		return null;
        	}
            return change;
		}));
		
		root.add(hint_l, 0, 0);
		root.add(hint_txt, 1, 0);
		root.add(sentence_l, 0, 1);
		root.add(sentence_txt, 1, 1);
		root.add(errors_l, 2, 1);
		root.add(diff_l, 0, 2);
		root.add(combo_box, 1, 2);
		root.add(errord_l, 2, 2);
		hpane.getChildren().addAll(add,back);
		root.add(hpane, 0, 3);
		root.add(errord_e, 1, 3);
		
		add.setOnAction(e -> {
			
	        hint = hint_txt.getText();
	        sentence = sentence_txt.getText().toUpperCase();
	        int x;
	        
	        if(hint.length() == 0 || sentence.length() == 0){
	        	errord_e.setText("Please enter all the details");
	        	errord_e.setTextFill(Color.RED);
	        	return;
	        }
	        
	        try{
	        	x = Integer.parseInt(combo_box.getValue().toString());
	        	combo_box.valueProperty().set(null);
	        }catch(Exception z){
	        	errord_e.setText("Select a value from dropdown");
	        	errord_e.setTextFill(Color.RED);
	        	return;
	        }
	      
	        
	        Puzzles p = new Puzzles(hint, sentence, x);
	        list.add(p);
	        System.out.println("Puzzle added to list " + list.size());
	        sentence_txt.setText("");
	        hint_txt.setText("");
	        errord_e.setText("Puzzle added");
	        errord_e.setTextFill(Color.BLUE);
	    });
		
		back.setOnAction(e -> {
			buildGui();
		});
		
		final Scene scene = new Scene(root, 500, 400);
	    st.setScene(scene);
	    st.setHeight(175);
	    st.setWidth(500);
	    st.show();
	}
	
	static public void file_write() throws FileNotFoundException, IOException{
    	FileOutputStream file_op = new FileOutputStream("Puzzle_database.txt");           // FileNotFoundException 
        ObjectOutputStream file  = new ObjectOutputStream(file_op);
		for(Puzzles p : list){
			file.writeObject(p);
		}
		file.close();
		System.exit(0);
	}
	
	private static String encrypt(String s){
		char[] sub = GenerateKey();
		StringBuilder op = new StringBuilder();
		for(int i = 0; i < s.length();i++){
			if(s.charAt(i) != ' '){
				op.append(sub[s.charAt(i) - 65]);
			}
			else{
				op.append(' ');
			}
		}
		return op.toString();
	}
	
	//12 - Generate new key everytime by - Daksh
	private static char[] GenerateKey(){
		char[] key = new char[26];
		boolean[] visited = new boolean[26];
		rand = new Random();
		for(int i = 0; i < 26; i++){
			int x = rand.nextInt(26);
			if(visited[x] || x == i){
				i--;
				continue;
			}
			visited[x] = true;
			key[i] = (char)(x + 65);
		}
		System.out.println();
		return key;
	}
	
	private static String getFre(){
		StringBuilder frestr = new StringBuilder();
		for(int i = 0; i < 26; i++){
			if(freq[i] == 0) continue;
			frestr.append(((char)(i + 65)));
			frestr.append(freq[i]);
			frestr.append(";");
			frestr.append(" ");
		}
		return frestr.toString();
	}
	
	private static String getUnused(){
		StringBuilder unusedstr = new StringBuilder();
		for(int i = 0; i < 26; i++){
			if(!alphaGuessed[i]){
				unusedstr.append((char)(i + 65));
				unusedstr.append(" ");
			}
		}
		return unusedstr.toString();
	}
	
	//9 - To update all the textbox with same alphabet by - Rikhav
	private static void updateTextBoxes(char c, String newVal){
		if(newVal.length() > 0) alphaGuessed[newVal.charAt(0) - 65] = true;
		textboxIndices = textboxes.get(c);
		for(int i = 0; i < textboxIndices.size(); i++){
			textField[textboxIndices.get(i)].setText(newVal);
		}
		textboxIndices = new ArrayList<Integer>();
	}
	
	private static boolean checkAlpha(char c){
		return alphaGuessed[c - 65];
	}
	
	//13 - Admin Login by - Krina
	static void verifyAdmin(){
		Label id_l = new Label("Username");
		Label pass_l = new Label("Password");
		
		TextField id_txt = new TextField("");
		TextField pass_txt = new TextField("");
		
		Button login = new Button("Login");
		Button back = new Button("Back");
		
		GridPane root = new GridPane();
		
		login.setOnAction(e -> {
			admin a = new admin();
			if(a.verifyAdmin(id_txt.getText(), pass_txt.getText())) {
				newPuzzle();
			}
			else{
				Alert alert = new Alert(AlertType.ERROR);
	            alert.setTitle("Error");
	            alert.setContentText("Incorrect username or password");
	            alert.showAndWait();
			}
		});
		
		back.setOnAction(e -> {
			buildGui();
		});
		
		root.add(id_l, 0, 0);
		root.add(id_txt, 1, 0);
		root.add(pass_l, 0, 1);
		root.add(pass_txt, 1, 1);
		root.add(login, 0, 2);
		root.add(back, 1, 2);
		
		final Scene scene = new Scene(root, 500, 400);
	    st.setScene(scene);
	    st.setHeight(140);
	    st.setWidth(300);
	    st.show();
	}
	
	static ArrayList<Integer> getNotGuessed(){
		ArrayList<Integer> notGuessed = new ArrayList<Integer>();
		for(int i = 0; i < sentence.length(); i++){
			if(sentence.charAt(i) == ' ') continue;
			if(alphaGuessed[sentence.charAt(i) - 65] == false){
				notGuessed.add(i);
			}
		}
		return notGuessed;
	}
}
