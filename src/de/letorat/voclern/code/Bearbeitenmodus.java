package de.letorat.voclern.code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Bearbeitenmodus {
	
	private Button laden;
	private Button speichern;
	private ArrayList<Card> cards = new ArrayList<Card>();
	private ObservableList<Card> data;
	private TableView<Card> table;
	@SuppressWarnings("rawtypes")
	private TableColumn ersteSprache;
	@SuppressWarnings("rawtypes")
	private TableColumn zweiteSprache;
	private Button hinzufuegen;
	private TextField questionField;
	private TextField awnserField;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Tab buildBearbeitenGui() {
		Tab tab = new Tab("Editing");
		
		laden = new Button("Load a set of cards");
		laden.setPrefWidth(160);
		laden.setOnAction(new buttonHandler());
		laden.setId("bearbeitenButton");
		speichern = new Button("Save changes");
		speichern.setPrefWidth(160);
		speichern.setOnAction(new buttonHandler());
		speichern.setDisable(true);
		
		table = new TableView<Card>();
		ersteSprache = new TableColumn("First language");
		zweiteSprache = new TableColumn<Card,String>("Second language");
		ersteSprache.setOnEditCommit(new columnHandler());
		zweiteSprache.setOnEditCommit(new columnHandler());
		
		ersteSprache.setPrefWidth(254);
		zweiteSprache.setPrefWidth(254);
		ersteSprache.setSortable(false);
		zweiteSprache.setSortable(false);
		
		ersteSprache.setCellFactory(TextFieldTableCell.forTableColumn());
		zweiteSprache.setCellFactory(TextFieldTableCell.forTableColumn());
		
		table.getColumns().addAll(ersteSprache,zweiteSprache);
		table.setPlaceholder(laden);
		table.setEditable(true);
		
		ersteSprache.setResizable(false);
		zweiteSprache.setResizable(false);
		
		ersteSprache.setCellValueFactory(
			    new PropertyValueFactory<Card,String>("question")  //Das sind die Felder in Card
			);
			zweiteSprache.setCellValueFactory(
			    new PropertyValueFactory<Card,String>("awnser")
		   );
			
		HBox hbox = new HBox(16);
		hbox.setPadding(new Insets(0,5.3,0,5.3));
		Label question = new Label("Question:");
		questionField = new TextField();
		Label awnser = new Label("Awnser:");
		awnserField = new TextField();
		
		questionField.setPrefWidth(120);
		awnserField.setPrefWidth(120);
		
		hinzufuegen = new Button("Add card");
		hinzufuegen.setDisable(true);
		hinzufuegen.setOnAction(new buttonHandler());
		
		hbox.getChildren().addAll(question,questionField,awnser,awnserField,hinzufuegen);
		
		VBox vbox = new VBox(10);
		vbox.setAlignment(Pos.CENTER);
		vbox.getChildren().add(hbox);
		vbox.getChildren().add(table);
		vbox.getChildren().add(speichern);
		
		tab.setContent(vbox);
		
		return tab;
	}
	
	public class buttonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if(e.getSource() == laden) {
				FileChooser fc = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Card (*.kar)", "*.kar");
				fc.getExtensionFilters().add(extFilter);
				fc.setTitle("Load a set of cards");
				File file = fc.showOpenDialog(null);
				if(file != null) {
					dateiLaden(file);
					Collections.sort(cards, new CardQuestionComparator());
					data = FXCollections.observableArrayList(cards);
					table.setItems(data);
					hinzufuegen.setDisable(false);
				}
			}
			else if(e.getSource() == speichern) {
				cards = new ArrayList<Card>();
				for(Card card : table.getItems()) {
					cards.add(card);
				}
				FileChooser fc = new FileChooser();
				FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Cards (*.kar)", "*.kar");
				fc.getExtensionFilters().add(extFilter);
				fc.setTitle("Save changes");
				File file = fc.showSaveDialog(null);
				if(file != null) {
					String name = file.getName();
					int index = name.lastIndexOf("."); //Endung entfernen (falls vorhanden)
					if(index > 0) {
						name = name.substring(0, index);
					}
					String newName = name + ".kar";  //"kar" - Endung anhängen
					File newFile = new File(file.getParentFile(), newName);
					dateiSpeichern(cards,newFile);
					reset();
			   }
		   }
			else { //hinzufuegen
				table.getItems().add(new Card(questionField.getText(), awnserField.getText(),false));
				questionField.clear();
				awnserField.clear();
				sortTable();
				speichern.setDisable(false);
			}
	    }
	}
	
	@SuppressWarnings("unchecked")
	private void dateiLaden(File file) {
		if(file.getName().endsWith(".kar")) {
			try {
				ObjectInputStream os = new ObjectInputStream(new FileInputStream(file));
				cards = (ArrayList<Card>) os.readObject();
				os.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public class columnHandler implements EventHandler<CellEditEvent<Card,String>> {
		public void handle(CellEditEvent<Card,String> e) {
			if(e.getTableColumn().equals(ersteSprache)) {
				((Card) e.getTableView().getItems().get(
		                e.getTablePosition().getRow())
		                ).setQuestion(e.getNewValue());
			}
			else {
				((Card) e.getTableView().getItems().get(
		                e.getTablePosition().getRow())
		                ).setAwnser(e.getNewValue());
			}
			sortTable();
			speichern.setDisable(false);
		}
	}
	
	private void dateiSpeichern(ArrayList<Card> list, File file) {
        try {
            FileOutputStream fileStream = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(fileStream);
            os.writeObject(list);
            os.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
         
    }
	
	public void reset() {
		hinzufuegen.setDisable(true);
		speichern.setDisable(true);
		table.getItems().removeAll(table.getItems());
	}
	
	public class CardQuestionComparator implements Comparator<Card> {
		public int compare(Card c1, Card c2) {
			return c1.getQuestion().compareTo(c2.getQuestion());
		}
	}
	
	public void sortTable() {
		cards = new ArrayList<Card>();
		for(Card card : table.getItems()) {
			cards.add(card);
		}
		Collections.sort(cards, new CardQuestionComparator());
		table.getItems().removeAll(table.getItems());
		for(Card c : cards) {
			table.getItems().add(c);
		}
	}
}
