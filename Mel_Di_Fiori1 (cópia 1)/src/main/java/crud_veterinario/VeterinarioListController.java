package crud_veterinario;

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
import model.Veterinario;

public class VeterinarioListController {

    @FXML private TableView<Veterinario> tableVeterinarios;
    @FXML private TableColumn<Veterinario, Long> colId;
    @FXML private TableColumn<Veterinario, String> colNome;
    @FXML private TableColumn<Veterinario, String> colCpf;
    @FXML private TableColumn<Veterinario, String> colCrv;
    @FXML private TableColumn<Veterinario, String> colEspecialidade;
    @FXML private TableColumn<Veterinario, String> colTelefone;
    @FXML private TableColumn<Veterinario, String> colSalario;
    @FXML private TableColumn<Veterinario, String> colDataAdmissao;

    private final ObservableList<Veterinario> dados = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("✅ VeterinarioListController inicializado!");
        configurarColunas();
        carregarVeterinarios();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
        colNome.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNome()));
        colCpf.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCpf()));
        colCrv.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCrv()));
        colEspecialidade.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEspecialidade()));
        colTelefone.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getTelefone()));
        colSalario.setCellValueFactory(c -> 
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getSalario() != null ? 
                "R$ " + String.format("%.2f", c.getValue().getSalario()) : 
                "N/A"
            )
        );
        colDataAdmissao.setCellValueFactory(c -> 
            new javafx.beans.property.SimpleStringProperty(
                c.getValue().getDataAdmissao() != null ? 
                c.getValue().getDataAdmissao().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : 
                "N/A"
            )
        );
    }

    private void carregarVeterinarios() {
        try {
            DAO<Veterinario> dao = new DAO<>(Veterinario.class);
            List<Veterinario> lista = dao.obterTodos(100, 0);
            dados.setAll(lista);
            tableVeterinarios.setItems(dados);
            System.out.println("✅ " + lista.size() + " veterinários carregados.");
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar veterinários: " + e.getMessage());
        }
    }

    @FXML
    private void abrirCadastro() {
        carregarTela("/telas/view/TelaCadastroVeterinario.fxml");
    }

    @FXML
    private void editarVeterinario() {
        Veterinario selecionado = tableVeterinarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Selecione um veterinário para editar.", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/view/TelaCadastroVeterinario.fxml"));
            Node tela = loader.load();
            
            VeterinarioCreateController controller = loader.getController();
            controller.carregarVeterinarioParaEdicao(selecionado);
            
            StackPane painel = (StackPane) tableVeterinarios.getScene().lookup("#painelConteudo");
            painel.getChildren().setAll(tela);
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao abrir edição: " + e.getMessage());
            mostrarAlerta("Erro ao abrir edição: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void excluirVeterinario() {
        Veterinario selecionado = tableVeterinarios.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Selecione um veterinário para excluir.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Excluir Veterinário");
        confirmacao.setContentText("Tem certeza que deseja excluir " + selecionado.getNome() + "?");
        
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                new DAO<>(Veterinario.class).removerPorIdTransacional(selecionado.getId());
                carregarVeterinarios();
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
            StackPane painel = (StackPane) tableVeterinarios.getScene().lookup("#painelConteudo");
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