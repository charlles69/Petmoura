package crud_tutor;

import java.io.IOException;
import java.util.List;

import dao.DAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import model.Tutor;

public class TutorListController {

    @FXML private TableView<Tutor> tableTutores;
    @FXML private TableColumn<Tutor, Long> colId;
    @FXML private TableColumn<Tutor, String> colNome;
    @FXML private TableColumn<Tutor, String> colCpf;
    @FXML private TableColumn<Tutor, String> colTelefone;
    @FXML private TableColumn<Tutor, String> colEmail;
    @FXML private TableColumn<Tutor, String> colEndereco;
    @FXML private TableColumn<Tutor, String> colDataCadastro;

    private final ObservableList<Tutor> dados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("✅ TutorListController inicializado!");
        configurarColunas();
        carregarTutores();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        colNome.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNome()));
        colCpf.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCpf()));
        colTelefone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTelefone()));
        colEmail.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEmail()));
        colEndereco.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEndereco()));
        colDataCadastro.setCellValueFactory(c -> 
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDataCadastro() != null ? 
                c.getValue().getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : 
                "N/A"
            )
        );
    }

    private void carregarTutores() {
        try {
            DAO<Tutor> dao = new DAO<>(Tutor.class);
            List<Tutor> lista = dao.obterTodos(100, 0);
            dados.setAll(lista);
            tableTutores.setItems(dados);
            System.out.println("✅ " + lista.size() + " tutores carregados.");
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar tutores: " + e.getMessage());
        }
    }

    @FXML
    private void abrirCadastro() {
        carregarTela("/telas/view/TelaCadastroTutor.fxml");
    }

    @FXML
    private void editarTutor() {
        Tutor selecionado = tableTutores.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Selecione um tutor para editar.", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/view/TelaCadastroTutor.fxml"));
            Node tela = loader.load();
            
            TutorCreateController controller = loader.getController();
            controller.carregarTutorParaEdicao(selecionado);
            
            StackPane painel = (StackPane) tableTutores.getScene().lookup("#painelConteudo");
            painel.getChildren().setAll(tela);
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao abrir edição: " + e.getMessage());
            mostrarAlerta("Erro ao abrir edição: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void excluirTutor() {
        Tutor selecionado = tableTutores.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Selecione um tutor para excluir.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Excluir Tutor");
        confirmacao.setContentText("Tem certeza que deseja excluir " + selecionado.getNome() + "?");
        
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                new DAO<>(Tutor.class).removerPorIdTransacional(selecionado.getId());
                carregarTutores();
                mostrarAlerta(selecionado.getNome() + " excluído com sucesso!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                mostrarAlerta("Erro ao excluir: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void voltarParaDashboard() {
        carregarTela("/telas/view/TelaDashboard.fxml");
    }

    private void carregarTela(String caminho) {
        try {
            Node tela = FXMLLoader.load(getClass().getResource(caminho));
            StackPane painel = (StackPane) tableTutores.getScene().lookup("#painelConteudo");
            painel.getChildren().setAll(tela);
        } catch (IOException e) {
            System.err.println("❌ Erro ao carregar tela: " + caminho);
            mostrarAlerta("Erro ao carregar tela: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensagem, Alert.AlertType tipo) {
        new Alert(tipo, mensagem).showAndWait();
    }
}