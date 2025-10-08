package crud_animal;

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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.StackPane;
import model.Animal;

public class AnimalListController {

    @FXML private TableView<Animal> tableAnimais;
    @FXML private TableColumn<Animal, Long> colId;
    @FXML private TableColumn<Animal, String> colNome;
    @FXML private TableColumn<Animal, String> colEspecie;
    @FXML private TableColumn<Animal, String> colRaca;
    @FXML private TableColumn<Animal, Integer> colIdade;
    @FXML private TableColumn<Animal, Float> colPeso;
    @FXML private TableColumn<Animal, String> colTutor; // ✅ AGORA DEVE SER INJETADA
    @FXML private TableColumn<Animal, String> colDataCadastro;
    @FXML private Label labelMensagem;

    private final ObservableList<Animal> dados = FXCollections.observableArrayList();
    private Animal animalSelecionado;

    @FXML
    public void initialize() {
        System.out.println("✅ AnimalListController inicializado!");
        
        // ✅ VERIFICAÇÃO DE SEGURANÇA: Verificar se todas as colunas foram injetadas
        verificarInjecaoColunas();
        
        configurarColunas();
        carregarAnimais();
    }

    // ✅ NOVO MÉTODO: Verificar se todas as colunas foram injetadas corretamente
    private void verificarInjecaoColunas() {
        System.out.println("🔍 Verificando injeção de colunas...");
        
        if (tableAnimais == null) System.err.println("❌ tableAnimais não injetada");
        if (colId == null) System.err.println("❌ colId não injetada");
        if (colNome == null) System.err.println("❌ colNome não injetada");
        if (colEspecie == null) System.err.println("❌ colEspecie não injetada");
        if (colRaca == null) System.err.println("❌ colRaca não injetada");
        if (colIdade == null) System.err.println("❌ colIdade não injetada");
        if (colPeso == null) System.err.println("❌ colPeso não injetada");
        if (colTutor == null) System.err.println("❌ colTutor não injetada");
        if (colDataCadastro == null) System.err.println("❌ colDataCadastro não injetada");
        if (labelMensagem == null) System.err.println("❌ labelMensagem não injetada");
        
        System.out.println("✅ Verificação de colunas concluída");
    }

    private void configurarColunas() {
        System.out.println("⚙️ Configurando colunas da tabela...");
        
        try {
            // ✅ CONFIGURAÇÃO COM VERIFICAÇÃO DE NULL
            if (colId != null) {
                colId.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getId()));
            }
            
            if (colNome != null) {
                colNome.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNome()));
            }
            
            if (colEspecie != null) {
                colEspecie.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEspecie()));
            }
            
            if (colRaca != null) {
                colRaca.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRaca()));
            }
            
            if (colIdade != null) {
                colIdade.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getIdade()));
            }
            
            if (colPeso != null) {
                colPeso.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getPeso()));
            }
            
            // ✅ COLUNA DO TUTOR - AGORA COM VERIFICAÇÃO
            if (colTutor != null) {
                colTutor.setCellValueFactory(c -> 
                    new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getTutor() != null ? 
                        c.getValue().getTutor().getNome() : 
                        "Sem tutor"
                    )
                );
            } else {
                System.err.println("⚠️ colTutor é null - criando coluna programaticamente");
                // Criar coluna programaticamente se não foi injetada
                TableColumn<Animal, String> colTutorProgramatica = new TableColumn<>("Tutor");
                colTutorProgramatica.setPrefWidth(150);
                colTutorProgramatica.setCellValueFactory(c -> 
                    new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getTutor() != null ? 
                        c.getValue().getTutor().getNome() : 
                        "Sem tutor"
                    )
                );
                tableAnimais.getColumns().add(colTutorProgramatica);
            }
            
            if (colDataCadastro != null) {
                colDataCadastro.setCellValueFactory(c -> 
                    new javafx.beans.property.SimpleStringProperty(
                        c.getValue().getDataCadastro() != null ? 
                        c.getValue().getDataCadastro().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : 
                        "N/A"
                    )
                );
            }
            
            System.out.println("✅ Configuração de colunas concluída com sucesso");
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao configurar colunas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarAnimais() {
        try {
            System.out.println("🔄 Carregando animais do banco...");
            DAO<Animal> dao = new DAO<>(Animal.class);
            List<Animal> lista = dao.obterTodos(100, 0);
            
            dados.setAll(lista);
            tableAnimais.setItems(dados);
            
            // ✅ VERIFICAÇÃO DE LABEL MENSAGEM
            if (labelMensagem != null) {
                if (lista.isEmpty()) {
                    labelMensagem.setVisible(true);
                    labelMensagem.setText("Nenhum animal cadastrado. Clique em 'Novo Animal' para cadastrar.");
                    if (tableAnimais != null) tableAnimais.setVisible(false);
                } else {
                    labelMensagem.setVisible(false);
                    if (tableAnimais != null) tableAnimais.setVisible(true);
                }
            }
            
            // Debug: mostrar informações dos animais carregados
            System.out.println("📊 Animais carregados: " + lista.size());
            for (Animal animal : lista) {
                System.out.println("🐕 " + animal.getNome() + 
                    " - Tutor: " + (animal.getTutor() != null ? animal.getTutor().getNome() : "Nenhum"));
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar animais: " + e.getMessage());
            e.printStackTrace();
            carregarDadosExemplo();
        }
    }

    private void carregarDadosExemplo() {
        System.out.println("🔄 Carregando dados de exemplo...");
        dados.clear();
        
        if (labelMensagem != null) {
            labelMensagem.setVisible(true);
            labelMensagem.setText("Erro ao carregar dados do banco. Use 'Novo Animal' para cadastrar.");
        }
        
        if (tableAnimais != null) {
            tableAnimais.setVisible(false);
        }
    }

    @FXML
    private void abrirCadastro() {
        System.out.println("📍 Navegando para cadastro de animal...");
        carregarTela("/telas/view/TelaCadastroAnimal.fxml");
    }

    @FXML
    private void editarAnimal() {
        if (tableAnimais == null) {
            mostrarAlerta("Erro", "Tabela não carregada corretamente.", Alert.AlertType.ERROR);
            return;
        }
        
        animalSelecionado = tableAnimais.getSelectionModel().getSelectedItem();
        if (animalSelecionado == null) {
            mostrarAlerta("Selecione um animal para editar.", Alert.AlertType.WARNING);
            return;
        }
        
        try {
            System.out.println("✏️ Editando animal: " + animalSelecionado.getNome());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/telas/view/TelaCadastroAnimal.fxml"));
            Node tela = loader.load();
            
            AnimalCreateController controller = loader.getController();
            controller.carregarAnimalParaEdicao(animalSelecionado);
            
            StackPane painel = (StackPane) tableAnimais.getScene().lookup("#painelConteudo");
            painel.getChildren().setAll(tela);
            
        } catch (IOException e) {
            System.err.println("❌ Erro ao abrir edição: " + e.getMessage());
            mostrarAlerta("Erro ao abrir edição: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void excluirAnimal() {
        if (tableAnimais == null) {
            mostrarAlerta("Erro", "Tabela não carregada corretamente.", Alert.AlertType.ERROR);
            return;
        }
        
        Animal selecionado = tableAnimais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Selecione um animal para excluir.", Alert.AlertType.WARNING);
            return;
        }
        
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText("Excluir Animal");
        confirmacao.setContentText("Tem certeza que deseja excluir " + selecionado.getNome() + "?");
        
        if (confirmacao.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                new DAO<>(Animal.class).removerPorIdTransacional(selecionado.getId());
                mostrarAlerta(selecionado.getNome() + " excluído com sucesso!", Alert.AlertType.INFORMATION);
                carregarAnimais(); // Recarrega a lista
            } catch (Exception e) {
                mostrarAlerta("Erro ao excluir: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void voltarParaDashboard() {
        System.out.println("📍 Voltando para dashboard...");
        carregarTela("/telas/view/TelaDashboard.fxml");
    }

    private void carregarTela(String caminho) {
        try {
            System.out.println("🔄 Carregando tela: " + caminho);
            Node tela = FXMLLoader.load(getClass().getResource(caminho));
            StackPane painel = (StackPane) tableAnimais.getScene().lookup("#painelConteudo");
            painel.getChildren().setAll(tela);
        } catch (IOException e) {
            System.err.println("❌ Erro ao carregar tela: " + caminho);
            mostrarAlerta("Erro ao carregar tela: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo, mensagem);
        alerta.showAndWait();
    }
}