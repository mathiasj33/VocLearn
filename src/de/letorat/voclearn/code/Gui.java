package de.letorat.voclearn.code;

import java.awt.Desktop;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Gui extends Application {
	
	private Button erstellen;
	private Button abfragen;
	private Button bearbeiten;
	private Button change;
	private Button yes;
	private Button no;
	private TabPane tabPane;
	private Tab erstellenTab;
	private Tab abfragenTab;
	private Tab bearbeitenTab;
	private Scene scene;
	private Stage secondaryStage;
	private Stage updateStage;
	
	private ArrayList<Thread> threads = new ArrayList<Thread>();
	private PrintWriter writer;
	
	private static final double VERSION = 3.0;
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) {
		
		java.util.Locale.setDefault(new java.util.Locale("en"));
		
		primaryStage.setTitle("VocLearn");
		
		Label vocLern = new Label("VocLearn");
		erstellen = new Button("Create a set of cards");
		abfragen = new Button("Test your knowledge");
		bearbeiten = new Button("Edit a set of cards");
		change = new Button("Change design");
		
		erstellen.setPrefWidth(300);
		abfragen.setPrefWidth(300);
		bearbeiten.setPrefWidth(300);
		change.setPrefWidth(300);
		erstellen.setOnAction(new buttonHandler());
		abfragen.setOnAction(new buttonHandler());
		bearbeiten.setOnAction(new buttonHandler());
		change.setOnAction(new buttonHandler());
		
		vocLern.setFont(new Font(75));
		vocLern.setAlignment(Pos.TOP_CENTER);
		
		VBox buttonBox = new VBox(30);
		buttonBox.getChildren().add(erstellen);
		buttonBox.getChildren().add(abfragen);
		buttonBox.getChildren().add(bearbeiten);
		buttonBox.getChildren().add(change);
		
		buttonBox.setAlignment(Pos.CENTER);
		
		VBox vbox = new VBox(50);
		vbox.setAlignment(Pos.TOP_CENTER);
		vbox.getChildren().add(vocLern);
		vbox.getChildren().add(buttonBox);
		
		tabPane = new TabPane();
		
		Tab menuTab = new Tab("Menu");
		menuTab.setClosable(false);
		menuTab.setContent(vbox);
		
		tabPane.getTabs().add(menuTab);
		tabPane.setTabClosingPolicy(TabClosingPolicy.ALL_TABS);
		
		scene = new Scene(tabPane,500,500);
		scene.getStylesheets().add(Gui.class.getResource("MenuStyle.css").toExternalForm());
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		
		primaryStage.getIcons().add(new Image(Gui.class.getResourceAsStream("VocLernLogo.png")));
		
		primaryStage.show();
		
		if(OSValidator.isWindows()) {
			if(new File(System.getProperty("user.home") + "\\VocLearnTemp").exists()) {
				deleteFolder(new File(System.getProperty("user.home") + "\\VocLearnTemp"));
			}
		}
		
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent e) {
				for(Thread t : threads) t.interrupt();
			}
		});
		
		checkForUpdates();
	}
	
	private  void deleteFolder(File folder) {
	    File[] files = folder.listFiles();
	    
	    if(files!=null) { //some JVMs return null for empty dirs
	        for(File f: files) {
	            if(f.isDirectory()) {
	                deleteFolder(f);
	            } else {
	                f.delete();
	            }
	        }
	    }
	    folder.delete();
	}
	
	private void checkForUpdates() {
		Thread t  = new Thread(new Runnable() {

			@Override
			public void run() {
				Socket textReadSocket = new Socket();
				try {
					textReadSocket.connect(new InetSocketAddress("letorat.selfhost.bz", 50001), 2000);
					writer = new PrintWriter(new OutputStreamWriter(textReadSocket.getOutputStream()), true);
					BufferedReader reader = new BufferedReader(new InputStreamReader(textReadSocket.getInputStream()));
					writer.println("$version" + VERSION);
					String msg;
					while((msg = reader.readLine()) != null) {
						if(msg.equals("$updateAvailable")) {
							Platform.runLater(new Runnable() {
							      @Override public void run() {
							    	    updateStage = new Stage();
										Label label = new Label("An update for VocLearn is available. Do you want to receive it now?");
										yes = new Button("Install it");
										no = new Button("Don't install it");
										yes.setOnAction(new buttonHandler());
										no.setOnAction(new buttonHandler());
										HBox hbox = new HBox(10);
										hbox.getChildren().addAll(yes, no);
										VBox vbox = new VBox(10);
										vbox.getChildren().addAll(label, hbox);
										vbox.setAlignment(Pos.CENTER);
										hbox.setAlignment(Pos.CENTER);
										vbox.setId("updateVBox");
										Scene s = new Scene(vbox,400,100);
										s.getStylesheets().add(Gui.class.getResource("MenuStyle.css").toExternalForm());
										updateStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
											public void handle(WindowEvent e) {
												e.consume();
											}
										});
										updateStage.setScene(s);
										updateStage.initModality(Modality.APPLICATION_MODAL);
										updateStage.getIcons().add(new Image(Gui.class.getResourceAsStream("VocLernLogo.png")));
										updateStage.show(); 
							      }
							    });
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		});
		t.start();
		threads.add(t);
	}

	public class buttonHandler implements EventHandler<ActionEvent> {
		public void handle(ActionEvent e) {
			if(e.getSource() == erstellen) {
				for(Tab t : tabPane.getTabs()) {
					if(t.equals(erstellenTab)) {
						tabPane.getSelectionModel().select(t);
						return;
					}
				}
				erstellenTab = new Erstellmodus().buildErstellenGui();
				tabPane.getTabs().add(erstellenTab);
				tabPane.getSelectionModel().select(erstellenTab);
			}
			else if(e.getSource() == abfragen) {
				for(Tab t : tabPane.getTabs()) {
					if(t.equals(abfragenTab)) {
						tabPane.getSelectionModel().select(t);
						return;
					}
				}
				abfragenTab = new Abfragemodus().buildAbfrageGui();
				tabPane.getTabs().add(abfragenTab);
				tabPane.getSelectionModel().select(abfragenTab);
			}
			else if(e.getSource() == bearbeiten) {
				for(Tab t : tabPane.getTabs()) {
					if(t.equals(bearbeitenTab)) {
						tabPane.getSelectionModel().select(t);
						return;
					}
				}
				bearbeitenTab = new Bearbeitenmodus().buildBearbeitenGui();
				tabPane.getTabs().add(bearbeitenTab);
				tabPane.getSelectionModel().select(bearbeitenTab);
			}
			else if(e.getSource() == change){
				if(scene.getStylesheets().contains(Gui.class.getResource("MenuStyle.css").toExternalForm())) {
					scene.getStylesheets().remove(Gui.class.getResource("MenuStyle.css").toExternalForm());
					scene.getStylesheets().add(Gui.class.getResource("OtherStyle.css").toExternalForm());
				}
				else {
					scene.getStylesheets().remove(Gui.class.getResource("OtherStyle.css").toExternalForm());
					scene.getStylesheets().add(Gui.class.getResource("MenuStyle.css").toExternalForm());
				}
			}
			else if(e.getSource() == yes) {
				VBox root = new VBox(20);
				Label label = new Label("Downloading the latest Version...");
				ProgressBar pb = new ProgressBar();
				pb.setPrefHeight(30);
				pb.setPrefWidth(300);
				root.getChildren().addAll(label);
				root.getChildren().add(pb);
				root.setAlignment(Pos.BASELINE_CENTER);
				Scene scene = new Scene(root,400,100);
				updateStage.setTitle("VocLearn Updater");
				updateStage.setResizable(false);
				updateStage.getIcons().add(new Image(getClass().getResourceAsStream("VocLernLogo.png")));
				updateStage.setScene(scene);
				updateStage.show();
					Thread t = new Thread(new Runnable() {
						public void run() {
							try {
								downloadNewVersion();
							} catch (IOException | InterruptedException e) {
								e.printStackTrace();
							}
						}
					});
					t.start();
					threads.add(t);
				}
			else if(e.getSource() == no) {
				writer.println("$doNotUpdate");
				updateStage.close();
			}
			else {
				secondaryStage.close();
			}
		}
		
		private void downloadNewVersion() throws IOException, InterruptedException {
	        FileOutputStream fos = null;
	        
	        Socket fileReadSocket = new Socket();
	        fileReadSocket.connect(new InetSocketAddress("letorat.selfhost.bz", 50001),  2000);
	        PrintWriter pw = new PrintWriter(new OutputStreamWriter(fileReadSocket.getOutputStream()), true);
	        
	        String homeDirectory = System.getProperty("user.home");
	        String path = "";
	        
	        if(OSValidator.isWindows()) { 
	        	File dir = new File(homeDirectory + "\\VocLearnTemp");
	        	dir.mkdirs();
	        	fos = new FileOutputStream(dir.getAbsolutePath() + "\\VocLearnSetup.exe");
	        	pw.println("$downloadwindows");
	        }
	        else {
	        	String path2 = Gui.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	        	path = URLDecoder.decode(path2, "UTF-8");
	        	path = new File(path).getParentFile().getAbsolutePath();
	        	File oldFile = new File(path2);
	        	boolean renamed = oldFile.renameTo(new File("VocLearnOld.jar"));
	        	oldFile.deleteOnExit();
	        	System.out.println(renamed);
	        	fos = new FileOutputStream(path + "\\VocLearn.jar");
	        	pw.println("$downloadnotwindows");
	        }
	        
	        byte [] bytearray  = new byte [4096];
	        InputStream is = fileReadSocket.getInputStream();
	        BufferedOutputStream bos = new BufferedOutputStream(fos);
	        
	        int count;

	        while ((count = is.read(bytearray)) > 0) {
	            bos.write(bytearray, 0, count);
	        }

	        bos.flush();
	        bos.close();
	        fileReadSocket.close();
	        if(OSValidator.isWindows()) {
	        	Desktop.getDesktop().open(new File(homeDirectory + "\\VocLearnTemp\\VocLearnSetup.exe"));
				for(Thread t : threads) t.interrupt();
	        	System.exit(0);
	        }
	       else {
	        	Desktop.getDesktop().open(new File(path + "\\VocLearn.jar"));
				for(Thread t : threads) t.interrupt();
	        	System.exit(0);
	        }
	        
	      }
	}
}
