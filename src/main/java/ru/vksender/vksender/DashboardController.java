package ru.vksender.vksender;



import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.Conversation;
import com.vk.api.sdk.objects.messages.ConversationPeer;
import com.vk.api.sdk.objects.messages.ConversationWithMessage;
import com.vk.api.sdk.objects.messages.responses.GetConversationsResponse;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import com.vk.api.sdk.client.TransportClient;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class DashboardController {

    @FXML
    private AnchorPane addAccForm;

    @FXML
    private Button importFileForMsg;

    @FXML
    private TextArea messageTextField;

    @FXML
    private Button sendButton;

    @FXML
    private ChoiceBox accountChoiseList;

    @FXML
    private ChoiceBox chatsList;

    @FXML
    private Button stopSend;

    @FXML
    private Button addAccountMenu;

    @FXML
    private TextField addAccount_accName;

    @FXML
    private Button addAccount_addBtn;

    @FXML
    private TableColumn<vkAccountsData, String> addAccount_col_accDesc;

    @FXML
    private TableColumn<?, ?> addAccount_col_accFirstName;

    @FXML
    private TableColumn<?, ?> addAccount_col_accLastName;

    @FXML
    private TableColumn<vkAccountsData, String> addAccount_col_accName;

    @FXML
    private TableColumn<vkAccountsData, Integer> addAccount_col_accNum;

    @FXML
    private TableColumn<vkAccountsData, String> addAccount_col_accToken;

    @FXML
    private TextField addAccount_description;

    @FXML
    private ImageView addAccount_imageView;

    @FXML
    private Button addAccount_import;

    @FXML
    private TextField addAccount_search;

    @FXML
    private TableView<vkAccountsData> addAccount_tableView;

    @FXML
    private TextField addAccount_token;

    @FXML
    private Button dashboardBtn;

    @FXML
    private AnchorPane rassilkaForm;

    @FXML
    private Button rassilkaMenu;

    @FXML
    private Button logout;

    @FXML
    private AnchorPane topForm;

    @FXML
    private Label username;

    private Map<String, Integer> chatNameToPeerIdMap = new HashMap<>();

    private Image image;

    private Connection connect;
    private Statement statement;
    private PreparedStatement prepare;
    private ResultSet result;

    public void importImage() {
        FileChooser open = new FileChooser();
        open.setTitle("Open Image File");
        open.getExtensionFilters().add(new ExtensionFilter("Image File", "*png", "*jpg"));

        Stage stage = (Stage) addAccForm.getScene().getWindow();
        File file = open.showOpenDialog(stage);

        if (file != null) {

            image = new Image(file.toURI().toString(), 97, 92, false, true);
            addAccount_imageView.setImage(image);

            getData.path = file.getAbsolutePath();
        }
    }

    private boolean isInternetAvailable() {
        try {
            java.net.URL url = new java.net.URL("https://vk.com");
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");
            connection.setConnectTimeout(3000); // 3 сек
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            return (200 <= responseCode && responseCode <= 399);
        } catch (IOException e) {
            return false;
        }
    }


    public void insertAddAccounts() {

        String sql1 = "SELECT accountName FROM vkaccounts WHERE accountName = ?";
        connect = Database.connectDb();

        try {

            prepare = connect.prepareStatement(sql1);
            prepare.setString(1, addAccount_accName.getText());
            result = prepare.executeQuery();

            if (result.next()) {
                showAlert("err", "Аккаунт \"" + addAccount_accName.getText() + "\" уже существует!");

            } else {
                if (addAccount_accName.getText().isEmpty() || addAccount_token.getText().isEmpty()
                    || addAccount_description.getText().isEmpty()) {
                    showAlert("err", "Пожалуйста заполните все поля!");
                } else if (addAccount_imageView.getImage() == null) {
                    showAlert("err", "Пожалуйста импортируйте изображние!");
                } else {
                    String sql = "INSERT INTO vkaccounts (token, accountname, description, image, first_name, last_name) VALUES (?,?,?,?,?,?)";
                    String uri = getData.path;
                    uri = uri.replace("\\", "\\\\");

//					addAccount();

                    prepare = connect.prepareStatement(sql);
                    prepare.setString(1, getToken(addAccount_token.getText()));
                    prepare.setString(2, addAccount_accName.getText());
                    prepare.setString(3, addAccount_description.getText());
                    prepare.setString(4, uri);

                    if (firstName == null && lastName == null) {
                        prepare.setString(5, "NULL");
                        prepare.setString(6, "NULL");
                    } else {
                        prepare.setString(5, firstName);
                        prepare.setString(6, lastName);
                    }

                    prepare.execute();

                    ObservableList<String> namesList = FXCollections.observableArrayList(getAccountsName());
                    accountChoiseList.setItems(namesList);
                    showAlert("info", "Успшено добавлен новый аккаунт!");
                    showAddAccounts();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String firstName;
    private String lastName;

    public ObservableList<vkAccountsData> addAccountsList() {
        ObservableList<vkAccountsData> listData = FXCollections.observableArrayList();
        String sql = "SELECT * FROM vkAccounts";
        connect = Database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            vkAccountsData accD;

            while (result.next()) {
                accD = new vkAccountsData(result.getInt("id"), result.getString("token"),
                    result.getString("accountName"), result.getString("description"), result.getString("image"),
                    result.getString("first_name"), result.getString("last_name"));
                listData.add(accD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listData;
    }

    ObservableList<vkAccountsData> listAddAccounts;

    public void showAddAccounts() {
        listAddAccounts = addAccountsList();

        addAccount_col_accNum.setCellValueFactory(new PropertyValueFactory<>("id"));
        addAccount_col_accName.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        addAccount_col_accDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        addAccount_col_accToken.setCellValueFactory(new PropertyValueFactory<>("token"));
        addAccount_col_accFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        addAccount_col_accLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        addAccount_tableView.setItems(listAddAccounts);
    }

    public void selectAddMoviesList() {
        vkAccountsData accD = addAccount_tableView.getSelectionModel().getSelectedItem();
        int num = addAccount_tableView.getSelectionModel().getFocusedIndex();

        if (accD == null || (num - 1) < -1) {
            return;
        }

        addAccount_token.setText(accD.getToken());
        addAccount_accName.setText(accD.getAccountName());
        addAccount_description.setText(accD.getDescription());

        if (accD.getImage() != null && !accD.getImage().isEmpty()) {
            try {
                String uri = "file:" + accD.getImage().replace("\\", "/");
                image = new Image(uri, 97, 92, false, true);
                addAccount_imageView.setImage(image);
                // Сохраняем путь для возможного обновления
                getData.path = accD.getImage().replace("\\\\", "\\");
            } catch (Exception e) {
                e.printStackTrace();
                addAccount_imageView.setImage(null);
            }
        } else {
            addAccount_imageView.setImage(null);
        }
    }

    public void clearAccs() {
        addAccount_token.setText("");
        addAccount_accName.setText("");
        addAccount_description.setText("");
        addAccount_imageView.setImage(null);

    }

    public void OnLogout() {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Предупреждение");
        alert.setHeaderText(null);
        alert.setContentText("Вы уверены, что хотите выйти?");
        alert.showAndWait();

        logout.getScene().getWindow().hide();
        try {
            URL url = new File("/home/asl/IdeaProjects/vk-sender/src/main/resources/ru/vksender/vksender/LoginAndReg.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("FXML фaйл не найден!");
            e.printStackTrace();
        }
    }

    public void switchForm(ActionEvent event) {
        if (event.getSource() == addAccountMenu) {
            addAccForm.setVisible(true);
            rassilkaForm.setVisible(false);
        } else if (event.getSource() == rassilkaMenu) {
            rassilkaForm.setVisible(true);
            addAccForm.setVisible(false);
        }
    }

    private String getToken(String tokenLink) {

        String accesToken = tokenLink;
        if (accesToken.isEmpty()) {
            showAlert("err", "Токен не может быть пустым!");
            return null;
        }

        String regex = "token=(.*?)&expires_in";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(accesToken);

        if (matcher.find()) {
            String accessToken = matcher.group(1);
            return accessToken;
        }

        return null;
    }

    public List<String> getAccountsName() {
        List<String> accName = new ArrayList<>();
        Connection connect = null;
        PreparedStatement prepare = null;
        ResultSet result = null;

        try {
            connect = Database.connectDb();
            String sql = "SELECT accountname FROM vkaccounts";
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

            while (result.next()) {
                String name = result.getString("accountname");
                accName.add(name);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return accName;
    }

    private volatile boolean sending = false;
    private Thread thread1;

//	public void onUpdateAccList(ActionEvent event) {
//
//	}



    private File selectedImageFile = null;  // переменная для хранения выбранного изображения

    @FXML
    private void handleImportFileForMsg(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Image File", "*png", "*jpg"));
        Stage stage = (Stage) addAccForm.getScene().getWindow();

        selectedImageFile = fileChooser.showOpenDialog(stage);

        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString(), 100, 100, true, true);
        }
    }


    @FXML
    private void handleSendMessage(ActionEvent event) {
        if (thread1 != null && thread1.isAlive()) {
            showAlert("info", "Отправка уже запущена. Остановите текущую отправку перед новым запуском.");
            return;
        }

        String selectedAccountName = (String) accountChoiseList.getValue();
        String selectedChatName = (String) chatsList.getValue();
        String message = messageTextField.getText();

        if (selectedAccountName == null || selectedAccountName.isEmpty()) {
            showAlert("err", "Выберите аккаунт для отправки!");
            return;
        }

        if (selectedChatName == null || selectedChatName.isEmpty()) {
            showAlert("err", "Выберите чат для отправки!");
            return;
        }

        if (message == null || message.isEmpty()) {
            showAlert("err", "Введите текст сообщения!");
            return;
        }

        showAlert("info", "Отправка рассылки началась");

        try {
            String accessToken = getTokenForAccount(selectedAccountName);
            TransportClient transportClient = new HttpTransportClient();
            VkApiClient vk = new VkApiClient(transportClient);
            UserActor actor = new UserActor(0, accessToken);

            if ("ВСЕ ЧАТЫ".equals(selectedChatName)) {
                sending = true;
                thread1 = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while(sending && !Thread.currentThread().isInterrupted()) {
                            try {
                                List<ConversationWithMessage> conversations = vk.messages()
                                    .getConversations(actor)
                                    .execute()
                                    .getItems();

                                for (ConversationWithMessage conv : conversations) {
                                    if (!sending || Thread.currentThread().isInterrupted()) {
                                        break;
                                    }

                                    try {
                                        int peerId = conv.getConversation().getPeer().getId();

                                        vk.messages()
                                            .send(actor)
                                            .peerId(peerId)
                                            .message(message)
                                            .randomId((int) (Math.random() * Integer.MAX_VALUE))
                                            .execute();

                                        Thread.sleep(3000);

                                    } catch (ApiException e) {
                                        // Пропускаем чаты с ошибками приватности или другими проблемами
                                        System.err.println("Ошибка при отправке в чат: " + e.getMessage());
                                        continue;
                                    } catch (ClientException e) {
                                        System.err.println("Ошибка клиента: " + e.getMessage());
                                        continue;
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                        return;
                                    }
                                }

                                Thread.sleep(10000); // ВСЕМ ЧАТАМ 10 сек

                            } catch (ApiException | ClientException | InterruptedException e) {
                                if (e instanceof InterruptedException) {
                                    Thread.currentThread().interrupt();
                                    return;
                                }
                                e.printStackTrace();
                                sending = false;
                                Platform.runLater(() -> {
                                    showAlert("err", "Ошибка при получении списка чатов: " + e.getMessage());
                                });
                                return;
                            }
                        }
                    }
                });

            } else {
                Integer peerId = chatNameToPeerIdMap.get(selectedChatName);
                if (peerId == null) {
                    showAlert("err", "Не удалось определить ID чата");
                    return;
                }

                sending = true;

                thread1 = new Thread(() -> {
                    while (sending && !Thread.currentThread().isInterrupted()) {
                        try {
                            vk.messages().send(actor)
                                .peerId(peerId)
                                .message(message)
                                .randomId((int) (Math.random() * Integer.MAX_VALUE))
                                .execute();

                            Thread.sleep(3000);

                        } catch (ApiException | ClientException e) {
                            Platform.runLater(() -> {
                                showAlert("err", "Ошибка API: " + e.getMessage());
                            });
                            sending = false;
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                });
            }

            thread1.setDaemon(true);
            thread1.start();


        } catch (Exception e) {
            showAlert("err", "Ошибка при запуске отправки: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStopSend() {
        if (sending == false) {
            showAlert("info", "Процесс не запущен!");

        }
        else {
            sending = false;

            if (thread1 != null) {
                thread1.interrupt();
                try {
                    thread1.join(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                thread1 = null;
            }

            showAlert("info", "Отправка сообщений остановлена!");
        }
    }

    private String getTokenForAccount(String accountName) {
        String sql = "SELECT token FROM vkaccounts WHERE accountname = ?";
        connect = Database.connectDb();

        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, accountName);
            result = prepare.executeQuery();

            if (result.next()) {
                return result.getString("token");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    public void loadChatsForAccount(String accountName) {
        String accessToken = getTokenForAccount(accountName);
        if (accessToken == null) {
            showAlert("err", "Не удалось получить токен для аккаунта!");
            return;
        }



        try {
            VkApiClient vk = new VkApiClient(new HttpTransportClient());
            UserActor actor = new UserActor(0, accessToken);

            List<ConversationWithMessage> conversations = vk.messages().getConversations(actor).count(200).execute()
                .getItems();

            ObservableList<String> chats = FXCollections.observableArrayList();

            Thread thread1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    for (ConversationWithMessage conv : conversations) {
                        Conversation conversation = conv.getConversation();
                        ConversationPeer peer = conversation.getPeer();

                        switch (peer.getType()) {
                            case USER:
                                List<GetResponse> users = null;
                                try {
                                    users = vk.users().get(actor).userIds(String.valueOf(peer.getId())).execute();
                                } catch (ApiException e) {
                                    e.printStackTrace();
                                } catch (ClientException e) {
                                    e.printStackTrace();
                                }

                                if (!users.isEmpty()) {
                                    GetResponse user = users.get(0);
                                    String name = user.getFirstName() + " " + user.getLastName();
                                    chats.add(name);
                                    chatNameToPeerIdMap.put(name, peer.getId());
                                }
                                break;

                            case CHAT:
                                String chatName = conversation.getChatSettings() != null
                                    ? conversation.getChatSettings().getTitle()
                                    : "Без названия";
                                chats.add(chatName);
                                chatNameToPeerIdMap.put(chatName, peer.getId());
                                break;

                            case GROUP:
                                chats.add("Сообщество");
                                chatNameToPeerIdMap.put("Сообщество", peer.getId());
                                break;
                        }
                    }
                    chatsList.setItems(chats);
                    chatsList.getItems().add("ВСЕ ЧАТЫ");

                }
            });

            thread1.start();

        } catch (ApiException | ClientException e) {
            System.out.println("Ошибка:");
            e.printStackTrace();
        }
    }

    public void showAlert(String alertType, String text) {
        if (alertType == "conf") {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Оповещение");
            alert.setHeaderText(null);
            alert.setContentText(text);
            alert.showAndWait();
        } else if (alertType == "err") {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText(text);
            alert.showAndWait();
        } else if (alertType == "info") {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Информация");
            alert.setHeaderText(null);
            alert.setContentText(text);
            alert.showAndWait();
        }
    }

    public void displayName() {
        username.setText(getData.username);
    }

    public void closeBtn() {
        System.exit(0);
    }

    public void minimizeBtn() {
        Stage stage = (Stage) topForm.getScene().getWindow();
        stage.setIconified(true);
    }

    public void initialize() {
        displayName();
        showAddAccounts();

        ObservableList<String> namesList = FXCollections.observableArrayList(getAccountsName());
        accountChoiseList.setItems(namesList);

        accountChoiseList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadChatsForAccount((String) newValue);
            }
        });

        sendButton.setOnAction(this::handleSendMessage);
    }

}
