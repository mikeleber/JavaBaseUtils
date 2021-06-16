package xml;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.basetools.util.xml.diffing.SimpleXMLDiff;
import org.basetools.util.xml.diffing.XMLDifferences;
import org.bitbucket.cowwoc.diffmatchpatch.DiffMatchPatch;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class XMLDiffTestClient extends Application {
    private static final DiffMatchPatch diffMatchPatch = new DiffMatchPatch();
    private final String blackListFileName = "Blacklist.txt";
    private GridPane pane;
    private Button buildVisualDivBtn;
    private Button buildVisualDivLeftRightBtn;
    private TextArea logging, expectedXML, actualXML;
    private WebView summary;
    private ListView<String> blackList;
    private CharSequence xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private CharSequence xmlHeaderMarker = "<?xml";

    public static void main(String[] args) throws Exception {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Create the TextArea
        PrintStream sysoutStream = new PrintStream(new Console(getLoggingArea(), System.out), true);
        System.setOut(sysoutStream);
        System.setErr(sysoutStream);

        Label diffsLabel = new Label("Diffs:");
        diffsLabel.setStyle("-fx-rotate: -90;");

        VBox divActionPanel = new VBox(getVisualDivButton(), getVisualLeftRightDivButton());

        VBox blacklistActionPanel = new VBox(getBlacklistAddButton(), getBlacklistRemoveButton(), getBlacklistLoadButton(), getBlacklistSaveButton());
        Label blacklistLabel = new Label("Blacklist:");
        blacklistLabel.setStyle("-fx-rotate: -90;");

        StackPane rightPane = new StackPane(getLoggingArea());
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);
        splitPane.getItems().addAll(rightPane);
        splitPane.setDividerPositions(0.25);

        Label xmlLabel = new Label("XML Data:");
        xmlLabel.setStyle("-fx-rotate: -90;");

        StackPane expectedXMLPane = new StackPane(getExpectedXMLArea());
        StackPane actualXMLPane = new StackPane(getActualXMLArea());
        SplitPane splitXMLPane = new SplitPane();
        splitXMLPane.setOrientation(Orientation.HORIZONTAL);
        splitXMLPane.getItems().addAll(expectedXMLPane, actualXMLPane);
        splitXMLPane.setDividerPositions(0.5);

        getGridPane().add(xmlLabel, 0, 1);
        getGridPane().add(new Label("Expected:"), 1, 0, 1, 1);
        getGridPane().add(new Label("Actual:"), 2, 0, 1, 1);
        getGridPane().add(splitXMLPane, 1, 1, 3, 6);
        getGridPane().add(diffsLabel, 0, 8);
        getGridPane().add(getSummaryView(), 1, 8);
        getGridPane().add(divActionPanel, 3, 8);
        getGridPane().add(blacklistLabel, 0, 9);
        getGridPane().add(getBlackListView(), 1, 9);
        getGridPane().add(blacklistActionPanel, 3, 9);
        getGridPane().add(splitPane, 1, 10, 3, 4);

        // Create the Scene
        Scene scene = new Scene(getGridPane());
        // Add the Scene to the Stage
        stage.setScene(scene);
        // Set the Title
        stage.setTitle("Test the difference!");
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

    private TextArea getExpectedXMLArea() {
        if (expectedXML == null) {
            expectedXML = new TextArea();
        }
        return expectedXML;
    }

    private TextArea getActualXMLArea() {
        if (actualXML == null) {
            actualXML = new TextArea();
        }
        return actualXML;
    }


    private Button getVisualDivButton() {
        if (buildVisualDivBtn == null) {
            buildVisualDivBtn = new Button("XMLDiff");
            buildVisualDivBtn.setMaxWidth(Double.MAX_VALUE);
            buildVisualDivBtn.setOnAction(new VisuallDiffButtonListener());
            buildVisualDivBtn.setDisable(false);
        }
        return buildVisualDivBtn;
    }

    private Button getVisualLeftRightDivButton() {
        if (buildVisualDivLeftRightBtn == null) {
            buildVisualDivLeftRightBtn = new Button("XMLHTMLTextDiff");
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

    private Button getBlacklistLoadButton() {
        Button btn = new Button();
        btn.setText("Load");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction((ActionEvent event) -> {
            blackList.getItems().addAll(loadBlacklist());
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
            if (blackList.getSelectionModel().getSelectedIndices().size() > 0) {
                final int selectedIdx = blackList.getSelectionModel().getSelectedIndices().get(0);
                blackList.getItems().remove(selectedIdx);
            }
        });
        return btn;
    }


    private ListView getBlackListView() {
        if (blackList == null) {
            blackList = new ListView<>();
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
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Read Blacklist");
            File loadFile = fileChooser.showOpenDialog(getBlackListView().getScene().getWindow());

            URL blackListFileNameUrl = loadFile.toURI().toURL();
            if (blackListFileNameUrl != null) {
                try (Stream<String> lines = Files.lines(Paths.get(blackListFileNameUrl.toURI()))) {
                    result = lines.collect(Collectors.toList());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(result);
    }

    public void saveBlacklist(String fileName, List<String> blacklist) {
        // Set the Style-properties of the GridPane
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Blacklist");
        File saveFile = fileChooser.showSaveDialog(getBlackListView().getScene().getWindow());

        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileOutputStream(
                    saveFile));
            for (String xpath : blacklist) {
                pw.println(xpath);
            }
            pw.close();

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
                String[] blacklistStrings = (String[]) getBlackListView().getItems().stream().toArray(size -> new String[size]);
                String leftXMLText = StringUtils.prependIfMissingIgnoreCase(expectedXML.getText(), xmlHeaderMarker, xmlHeader);
                String rightXMLText = StringUtils.prependIfMissingIgnoreCase(actualXML.getText(), xmlHeaderMarker, xmlHeader);
                XMLDifferences aDifference = SimpleXMLDiff.builder().withBlacklistXPaths(blacklistStrings).build().diff(leftXMLText, rightXMLText);
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
                String[] blacklistStrings = (String[]) getBlackListView().getItems().stream().toArray(size -> new String[size]);
                String leftXMLText = StringUtils.prependIfMissingIgnoreCase(expectedXML.getText(), xmlHeaderMarker, xmlHeader);
                String rightXMLText = StringUtils.prependIfMissingIgnoreCase(actualXML.getText(), xmlHeaderMarker, xmlHeader);

                XMLDifferences aDifference = SimpleXMLDiff.builder().withBlacklistXPaths(blacklistStrings).build().diff(leftXMLText, rightXMLText);
                LinkedList<DiffMatchPatch.Diff> diffs = diffMatchPatch.diffMain(aDifference.getActualContent(), aDifference.getExpectedContent(), true);
                summary.getEngine().loadContent(diffMatchPatch.diffPrettyHtml(diffs));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
