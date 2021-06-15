package xml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
//import javafx.scene.web.WebView;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.basetools.util.xml.diffing.SimpleXMLDiff;
import org.basetools.util.xml.diffing.XMLDifferences;
import org.basetools.util.xml.diffing.XMLNodeDiff;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

public class XMLDiffTestClient extends Application {
    private static final DiffMatchPatch diffMatchPatch = new DiffMatchPatch();

    private final String blackListFileName = "Blacklist.txt";
    private GridPane pane;
    private Button buildVisualDivBtn;
    private Button buildVisualDivLeftRightBtn;
    private TextArea logging, leftXML, rightXML;
    private WebView summary;
    private ListView<String> blackList;

    public static void main(String[] args) throws Exception {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create the TextArea
        PrintStream sysoutStream = new PrintStream(new Console(getLoggingArea(), System.out), true);
        System.setOut(sysoutStream);
        System.setErr(sysoutStream);

        // Create the GridPane
        getGridPane().add(new Label("Summary: "), 1, 0);

        getGridPane().add(getSummaryView(), 1, 1);
        VBox divActionPanel = new VBox( getVisualDivButton(), getVisualLeftRightDivButton());

        getGridPane().add(divActionPanel, 2, 1);
        VBox blacklistActionPanel = new VBox(getBlacklistAddButton(), getBlacklistRemoveButton(), getBlacklistSaveButton());
        getGridPane().add(new Label("Blacklist: "), 0, 3);
        getGridPane().add(getBlackListView(), 1, 3);
        getGridPane().add(blacklistActionPanel, 2, 3);

        // getGridPane().add(getLoggingArea(), 1, 5);


        StackPane rightPane = new StackPane(getLoggingArea());
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(rightPane);
        splitPane.setDividerPositions(0.25);
        getGridPane().add(splitPane, 0, 5, 2, 6);

        StackPane leftXMLPane = new StackPane(getLeftXMLArea());
        StackPane rightXMLPane = new StackPane(getRightXMLArea());
        SplitPane splitXMLPane = new SplitPane();
        splitXMLPane.setOrientation(Orientation.HORIZONTAL);
        splitXMLPane.getItems().addAll(leftXMLPane, rightXMLPane);
        splitXMLPane.setDividerPositions(0.5);
        getGridPane().add(splitXMLPane, 0, 12, 2, 6);
        // Set the Style-properties of the GridPane

        // Create the Scene
        Scene scene = new Scene(getGridPane());
        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title
        stage.setTitle("Test the west!");
        // Display the Stage
        stage.show();
    }

    private GridPane getGridPane() {
        if (pane == null) {
            pane = new GridPane();
            // Set the horizontal and vertical gaps between children
            pane.setHgap(10);
            pane.setVgap(5);
            pane.setStyle("-fx-padding: 10;" +
                    "-fx-border-style: solid inside;" +
                    "-fx-border-width: 2;" +
                    "-fx-border-insets: 5;" +
                    "-fx-border-radius: 5;" +
                    "-fx-border-color: grey;");
        }
        return pane;
    }

    private WebView getSummaryView() {
        if (summary == null) {
            summary = new WebView();
            summary.setMaxHeight(400);
        }
        return summary;
    }

    private TextArea getLoggingArea() {
        if (logging == null) {
            logging = new TextArea();
        }
        return logging;
    }

    private TextArea getLeftXMLArea() {
        if (leftXML == null) {
            leftXML = new TextArea();
        }
        return leftXML;
    }

    private TextArea getRightXMLArea() {
        if (rightXML == null) {
            rightXML = new TextArea();
        }
        return rightXML;
    }


    private Button getVisualDivButton() {
        if (buildVisualDivBtn == null) {
            buildVisualDivBtn = new Button("VisualDiff");
            buildVisualDivBtn.setMaxWidth(Double.MAX_VALUE);
            buildVisualDivBtn.setOnAction(new VisuallDiffButtonListener());
            buildVisualDivBtn.setDisable(false);
        }
        return buildVisualDivBtn;
    }

    private Button getVisualLeftRightDivButton() {
        if (buildVisualDivLeftRightBtn == null) {
            buildVisualDivLeftRightBtn = new Button("VisualLeftRightDiff");
            buildVisualDivLeftRightBtn.setMaxWidth(Double.MAX_VALUE);
            buildVisualDivLeftRightBtn.setOnAction(new VisuallDiffLeftRightButtonListener());
        }
        return buildVisualDivLeftRightBtn;
    }

    private Button getBlacklistAddButton() {
        Button btn = new Button();
        btn.setText("Add");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction((ActionEvent event) -> {
            String c = "insertXPATH";
            blackList.getItems().add(blackList.getItems().size(), c);
            blackList.scrollTo(c);
        });
        return btn;
    }

    private Button getBlacklistSaveButton() {
        Button btn = new Button();
        btn.setText("Save");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction((ActionEvent event) -> {
            saveBlacklist(blackListFileName, blackList.getItems());
        });
        return btn;
    }

    private Button getBlacklistRemoveButton() {
        Button btn = new Button();
        btn.setText("Remove");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction((ActionEvent event) -> {
            final int selectedIdx = blackList.getSelectionModel().getSelectedIndices().get(0);
            blackList.getItems().remove(selectedIdx);
        });
        return btn;
    }


    private ListView getBlackListView() {
        if (blackList == null) {
            blackList = new ListView<>(loadBlacklist());
            blackList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            blackList.setEditable(true);

            blackList.setCellFactory(TextFieldListCell.forListView());
            blackList.setOrientation(Orientation.VERTICAL);
            blackList.setPrefSize(200, 50);
            blackList.setMinHeight(50);
        }
        return blackList;
    }



    private ObservableList<String> loadBlacklist() {
        List<String> result = new ArrayList<>();
        URL blackListFileNameUrl = ClassLoader.getSystemResource(blackListFileName);
        if (blackListFileNameUrl != null) {
            try (Stream<String> lines = Files.lines(Paths.get(blackListFileNameUrl.toURI()))) {
                result = lines.collect(Collectors.toList());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return FXCollections.observableArrayList(result);

    }

    public void saveBlacklist(String fileName, List<String> blacklist) {
        PrintWriter pw = null;
        try {

            pw = new PrintWriter(new FileOutputStream(
                    ClassLoader.getSystemResource(fileName).toURI().toURL().getFile()));

            for (String xpath : blacklist) {
                pw.println(xpath);
            }
            pw.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Saved");
            alert.setHeaderText("Blacklist saved");
            alert.setContentText("Blacklist saved for you!");

            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class Console extends OutputStream {
        private final TextArea console;
        private final OutputStream pipeTo;

        public Console(TextArea console, OutputStream pipeTo) {
            this.console = console;
            this.pipeTo = pipeTo;
        }

        public void appendText(String valueOf) {
            Platform.runLater(() -> console.appendText(valueOf));
        }

        public void write(int b) throws IOException {
            appendText(String.valueOf((char) b));
            if (pipeTo != null) {
                pipeTo.write(b);
            }
        }
    }



    private class VisuallDiffButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            try {

                String[] blacklistStrings = (String[])getBlackListView().getItems().stream().toArray(size -> new String[size]);
                XMLDifferences aDifference = SimpleXMLDiff.builder().withBlacklistXPaths(blacklistStrings).build().diff(leftXML.getText(), rightXML.getText());
                String diff = aDifference.getDifferences().stream().map(Object::toString).collect(Collectors.joining("<br>"));
                summary.getEngine().loadContent(diff);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    private class VisuallDiffLeftRightButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent e) {
            try {
                String[] blacklistStrings = (String[])getBlackListView().getItems().stream().toArray(size -> new String[size]);
                XMLDifferences aDifference = SimpleXMLDiff.builder().withBlacklistXPaths(blacklistStrings).build().diff(leftXML.getText(), rightXML.getText());
                LinkedList<DiffMatchPatch.Diff> diffs = diffMatchPatch.diffMain(aDifference.getCurrentContent(), aDifference.getTestContent(), true);
                summary.getEngine().loadContent(diffMatchPatch.diffPrettyHtml(diffs));
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }



}

