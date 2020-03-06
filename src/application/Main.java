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
	static boolean[] alphaGuessed;
	static int[] freq;
	
	@Override
	public void start(Stage primaryStage) {
		st = primaryStage;
		st.setResizable(false);
		st.setTitle("Cryptogram");
		try{
			System.out.println("SPM Project: A Cryptogram\nCode by:- \n\tRikhav Nilesh Dedhia\n\tRishikesh Maddi\n\tDaksh Parikh\n\tKrina Karia");
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
			st.setHeight(240);
		    st.setWidth(670);
			HBox hpaneb = new HBox(10);
			VBox vpanel = new VBox(10);
			GridPane gpane = new GridPane();
			Scene scene = new Scene(gpane,400,400);
			
			Button addPuzzle = new Button("Add Puzzle");
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
			
			addPuzzle.setPrefWidth(100);
//			ExitB.setPrefWidth(100);
			Reset.setPrefWidth(100);
			HintMe.setPrefWidth(100);
			newPuzz.setPrefWidth(100);
			Submit.setPrefWidth(100);
			About.setPrefWidth(100);
			
			Reset.setDisable(true);
			HintMe.setDisable(true);
			Submit.setDisable(true);
			About.setDisable(true);
			
			hpaneb.setStyle("-fx-padding: 10;" + 
	                  "-fx-border-style: solid inside;" + 
	                  "-fx-border-width: 2;" +
	                  "-fx-border-insets: 5;" + 
	                  "-fx-border-radius: 0;" + 
	                  "-fx-border-color: blue;");
			
			//hpaneb.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
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
			
			addPuzzle.setOnAction(e -> {
				gPanePMain.getChildren().removeAll();
				gPanePMain.getChildren().clear();
		        newPuzzle();
		    });
			
//			ExitB.setOnAction(e -> {
//				try {
//					file_write();
//				} catch (FileNotFoundException e1) {
//					e1.printStackTrace();
//				} catch (IOException e1) {
//					e1.printStackTrace();
//				}
//		    });
			
			newPuzz.setOnAction(e ->{
				alphaGuessed = new boolean[26];
				freq = new int[26];
				GridPane gPaneP = new GridPane();
				gPaneP.getChildren().removeAll();
				gPaneP.getChildren().clear();
				gPanePMain.getChildren().removeAll();
				gPanePMain.getChildren().clear();
				Reset.setDisable(false);
				rand = new Random();
				currPuzzle = list.get(rand.nextInt(list.size()));
				sentence = currPuzzle.getSentence();
				hint = currPuzzle.getHint();
				diff = currPuzzle.getDiff();
//				hint_l.setText("The below puzzle is about: -" + hint + "\tDifficulty of Puzzle: - " + diff);
				encryptSentence = encrypt(sentence);
				textField = new TextField[encryptSentence.length()];
				encrypt_l = new Label[encryptSentence.length()];
				System.out.println(sentence);
				System.out.println(encryptSentence);
				int cnt = 0;
				int j = 0;
//				gPanePMain.add(hint_l,0,0);
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
				        if(oldVal.length() > 0) alphaGuessed[oldVal.charAt(0) - 65] = false;
				        if(newVal.length() > 0) alphaGuessed[newVal.charAt(0) - 65] = true;
				        UnusedAlphaV.setText(getUnused());
				        vpanel.getChildren().removeAll();
				        vpanel.getChildren().clear();
				        vpanel.getChildren().addAll(UnusedAlpha,UnusedAlphaV, AlphaFre,AlphaFreV);
				    });
					if(encryptSentence.charAt(i) != ' '){
						gPaneP.add(textField[i], i%18, j + 1);
						freq[encryptSentence.charAt(i) - 65]++;
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
//				System.out.println(260 + (52 * ((encryptSentence.length() / 18) + 1)));
				vpanel.getChildren().removeAll();
		        vpanel.getChildren().clear();
		        vpanel.getChildren().addAll(UnusedAlpha,UnusedAlphaV, AlphaFre,AlphaFreV);
				st.setHeight(280 + (65 * ((encryptSentence.length() / 18) + 1)));//65
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
					textField[i].setText("");
				}
			});
			
			hpaneb.getChildren().addAll(Reset, newPuzz, HintMe, addPuzzle,Submit, About);
			if(UnusedAlphaV.getText().length() == 0) vpanel.getChildren().addAll(UnusedAlpha, AlphaFre);
			else vpanel.getChildren().addAll(UnusedAlpha,UnusedAlphaV, AlphaFre,AlphaFreV);
			gpane.add(hpaneb,0,0);
			gpane.add(gPanePMain,0,1);
			gpane.add(vpanel, 0, 2);
//			gpane.add(About, 0, 3);
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

 // Create a combo box 
		ComboBox<String> combo_box = new ComboBox<String>(FXCollections.observableArrayList(diffLevel));
		
		TextField hint_txt = new TextField("");
		TextField sentence_txt = new TextField("");
//		TextField diff_txt = new TextField("");
		
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
		
//		diff_txt.setTextFormatter(new TextFormatter<String>((Change change) -> {
//            String newText = change.getControlNewText();
//            if(newText.length() > 1){
//            	return null;
//            }
//            if(newText.length() != 0){
//        		if((newText != null) && (newText.matches("^[1-5]*$"))){
//                    return change;
//            	}
//        		return null;
//        	}
//            return change;
//		}));
		
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
			
//			combo_box.getSelectionModel().clearSelection();
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
//	        	z.printStackTrace();
	        	errord_e.setText("Select a value from dropdown");
	        	errord_e.setTextFill(Color.RED);
	        	return;
	        }
	      
	        
	        Puzzles p = new Puzzles(hint, sentence, x);
	        list.add(p);
	        System.out.println("Puzzle added to list " + list.size());
	        sentence_txt.setText("");
//	        diff_txt.setText("");
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
		char[] sub = {'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M'};
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
	
	private static String getFre(){
		StringBuilder frestr = new StringBuilder();
		for(int i = 0; i < 26; i++){
			if(freq[i] == 0) continue;
			frestr.append(((char)(i + 65)));
//			frestr.append(" ");
			frestr.append(freq[i]);
			frestr.append(";");
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
}
