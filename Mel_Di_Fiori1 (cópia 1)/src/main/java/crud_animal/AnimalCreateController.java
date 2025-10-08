package crud_animal;

import dao.DAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import model.Animal;
import model.Tutor;

import java.util.List;

public class AnimalCreateController {

    @FXML private TextField txtNome;
    @FXML private ComboBox<String> comboEspecie;
    @FXML private TextField txtRaca;
    @FXML private Spinner<Integer> spinnerIdade;
    @FXML private TextField txtPeso;
    @FXML private TextArea txtObservacoes;
    @FXML private ComboBox<Tutor> comboTutor;

    private Animal animalEmEdicao;
    private boolean modoEdicao = false;

    @FXML
    public void initialize() {
        System.out.println("✅ AnimalCreateController inicializado!");
        
        // Configurar espécies
        comboEspecie.getItems().addAll("Cachorro", "Gato", "Pássaro", "Roedor", "Réptil", "Outros");
        
        // Configurar spinner de idade
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 1);
        spinnerIdade.setValueFactory(valueFactory);
        
        // Configurar ComboBox de tutores
        configurarComboTutor();
        
        // Carregar tutores
        carregarTutores();
    }

    private void configurarComboTutor() {
        // Configurar como os tutores são exibidos no ComboBox
        comboTutor.setCellFactory(param -> new javafx.scene.control.ListCell<Tutor>() {
            @Override
            protected void updateItem(Tutor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNome() + " - CPF: " + item.getCpf());
                }
            }
        });
        
        comboTutor.setButtonCell(new javafx.scene.control.ListCell<Tutor>() {
            @Override
            protected void updateItem(Tutor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Selecione um tutor (OBRIGATÓRIO)");
                } else {
                    setText(item.getNome() + " - CPF: " + item.getCpf());
                }
            }
        });
    }

    private void carregarTutores() {
        try {
            System.out.println("🔄 Carregando tutores para o ComboBox...");
            DAO<Tutor> daoTutor = new DAO<>(Tutor.class);
            List<Tutor> tutores = daoTutor.obterTodos(100, 0);
            
            comboTutor.getItems().clear();
            comboTutor.getItems().addAll(tutores);
            
            System.out.println("✅ " + tutores.size() + " tutores carregados no ComboBox.");
            
            if (tutores.isEmpty()) {
                mostrarAlerta("ATENÇÃO", 
                    "Nenhum tutor cadastrado no sistema!\n\n" +
                    "Para cadastrar um animal, você PRECISA primeiro cadastrar pelo menos um tutor.\n\n" +
                    "Vá para o menu 'Tutores' e cadastre um tutor antes de continuar.", 
                    Alert.AlertType.WARNING);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao carregar tutores: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível carregar a lista de tutores: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public void carregarAnimalParaEdicao(Animal animal) {
        if (animal == null) {
            System.err.println("❌ Animal nulo passado para edição");
            return;
        }
        
        this.animalEmEdicao = animal;
        this.modoEdicao = true;
        
        System.out.println("🔄 Carregando animal para edição: " + animal.getNome());
        
        // Preenche os campos com os dados do animal
        txtNome.setText(animal.getNome());
        comboEspecie.setValue(animal.getEspecie());
        txtRaca.setText(animal.getRaca());
        spinnerIdade.getValueFactory().setValue(animal.getIdade() != null ? animal.getIdade() : 1);
        txtPeso.setText(animal.getPeso() != null ? String.valueOf(animal.getPeso()) : "");
        txtObservacoes.setText(animal.getObservacoes() != null ? animal.getObservacoes() : "");
        
        // Define o tutor no ComboBox
        if (animal.getTutor() != null) {
            // Procura o tutor na lista carregada
            for (Tutor tutor : comboTutor.getItems()) {
                if (tutor.getId().equals(animal.getTutor().getId())) {
                    comboTutor.setValue(tutor);
                    System.out.println("✅ Tutor definido: " + tutor.getNome());
                    break;
                }
            }
        } else {
            System.err.println("⚠️ Animal não tem tutor associado!");
        }
    }

    @FXML
    private void salvarAnimal() {
        try {
            System.out.println("💾 Iniciando processo de salvamento do animal...");
            
            // Validação rigorosa dos campos
            if (!validarCampos()) {
                System.err.println("❌ Validação falhou - campos obrigatórios não preenchidos");
                return;
            }

            // Obter valores dos campos
            String nome = txtNome.getText().trim();
            String especie = comboEspecie.getValue();
            String raca = txtRaca.getText().trim();
            Integer idade = spinnerIdade.getValue();
            Float peso = parsePeso(txtPeso.getText());
            String observacoes = txtObservacoes.getText().trim();
            Tutor tutor = comboTutor.getValue();

            // Validação EXTRA do tutor (deve ser obrigatório)
            if (tutor == null) {
                System.err.println("❌ Tentativa de salvar animal sem tutor selecionado");
                mostrarAlerta("TUTOR OBRIGATÓRIO", 
                    "Você deve selecionar um tutor para o animal!\n\n" +
                    "Se não há tutores disponíveis, cadastre um tutor primeiro no menu 'Tutores'.",
                    Alert.AlertType.ERROR);
                comboTutor.requestFocus();
                return;
            }

            System.out.println("📋 Dados validados:");
            System.out.println("   Nome: " + nome);
            System.out.println("   Espécie: " + especie);
            System.out.println("   Raça: " + raca);
            System.out.println("   Idade: " + idade);
            System.out.println("   Peso: " + peso);
            System.out.println("   Tutor: " + tutor.getNome() + " (ID: " + tutor.getId() + ")");

            if (modoEdicao && animalEmEdicao != null) {
                // MODO EDIÇÃO: Atualiza animal existente
                System.out.println("✏️ Atualizando animal existente...");
                animalEmEdicao.setNome(nome);
                animalEmEdicao.setEspecie(especie);
                animalEmEdicao.setRaca(raca);
                animalEmEdicao.setIdade(idade);
                animalEmEdicao.setPeso(peso);
                animalEmEdicao.setObservacoes(observacoes);
                animalEmEdicao.setTutor(tutor);

                new DAO<>(Animal.class).atualizarTransacional(animalEmEdicao);
                mostrarAlerta("Sucesso", "✅ Animal atualizado com sucesso!", Alert.AlertType.INFORMATION);
                System.out.println("✅ Animal atualizado: " + nome);
                
            } else {
                // MODO CADASTRO: Cria novo animal
                System.out.println("🆕 Criando novo animal...");
                Animal novoAnimal = new Animal(nome, especie, raca, idade, peso, observacoes, tutor);
                new DAO<>(Animal.class).incluirTransacional(novoAnimal);
                mostrarAlerta("Sucesso", "✅ Animal cadastrado com sucesso!", Alert.AlertType.INFORMATION);
                System.out.println("✅ Novo animal cadastrado: " + nome);
            }

            // Voltar para a lista após salvar
            voltarParaLista();

        } catch (Exception e) {
            System.err.println("❌ ERRO CRÍTICO ao salvar animal: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Erro Crítico", 
                "Falha ao salvar animal:\n" + e.getMessage(), 
                Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limparCampos() {
        System.out.println("🗑️ Limpando campos do formulário...");
        txtNome.clear();
        comboEspecie.setValue(null);
        txtRaca.clear();
        spinnerIdade.getValueFactory().setValue(1);
        txtPeso.clear();
        txtObservacoes.clear();
        comboTutor.setValue(null);
        modoEdicao = false;
        animalEmEdicao = null;
        removerDestaquesErro();
        System.out.println("✅ Campos limpos");
    }

    @FXML
    private void voltarParaLista() {
        try {
            System.out.println("⬅️ Voltando para lista de animais...");
            StackPane painel = (StackPane) txtNome.getScene().lookup("#painelConteudo");
            if (painel != null) {
                javafx.scene.Node tela = javafx.fxml.FXMLLoader.load(getClass().getResource("/telas/view/TelaListaAnimal.fxml"));
                painel.getChildren().setAll(tela);
                System.out.println("✅ Navegação para lista concluída");
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao voltar para lista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean validarCampos() {
        boolean valido = true;
        removerDestaquesErro();
        
        System.out.println("🔍 Validando campos...");
        
        // Validar Tutor (OBRIGATÓRIO)
        if (comboTutor.getValue() == null) {
            System.err.println("❌ Tutor não selecionado");
            destacarCampoErro(comboTutor);
            valido = false;
        } else {
            System.out.println("✅ Tutor selecionado: " + comboTutor.getValue().getNome());
        }
        
        // Validar Nome
        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            System.err.println("❌ Nome não preenchido");
            destacarCampoErro(txtNome);
            valido = false;
        } else {
            System.out.println("✅ Nome preenchido: " + txtNome.getText());
        }
        
        // Validar Espécie
        if (comboEspecie.getValue() == null) {
            System.err.println("❌ Espécie não selecionada");
            destacarCampoErro(comboEspecie);
            valido = false;
        } else {
            System.out.println("✅ Espécie selecionada: " + comboEspecie.getValue());
        }
        
        // Validar Raça
        if (txtRaca.getText() == null || txtRaca.getText().trim().isEmpty()) {
            System.err.println("❌ Raça não preenchida");
            destacarCampoErro(txtRaca);
            valido = false;
        } else {
            System.out.println("✅ Raça preenchida: " + txtRaca.getText());
        }
        
        if (valido) {
            System.out.println("✅ Todos os campos obrigatórios estão preenchidos");
        } else {
            mostrarAlerta("Campos Obrigatórios", 
                "Preencha todos os campos obrigatórios:\n\n" +
                "• Tutor\n• Nome\n• Espécie\n• Raça\n\n" +
                "Os campos obrigatórios estão destacados em vermelho.",
                Alert.AlertType.WARNING);
        }
        
        return valido;
    }

    private void destacarCampoErro(Control campo) {
        campo.setStyle("-fx-border-color: #e74c3c; -fx-border-width: 2px; -fx-background-color: #ffeaea;");
    }

    private void removerDestaqueErro(Control campo) {
        campo.setStyle("");
    }
    
    private void removerDestaquesErro() {
        removerDestaqueErro(txtNome);
        removerDestaqueErro(comboEspecie);
        removerDestaqueErro(txtRaca);
        removerDestaqueErro(comboTutor);
    }

    private Float parsePeso(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }
        try {
            String pesoStr = texto.replace(",", ".").trim();
            Float peso = Float.parseFloat(pesoStr);
            System.out.println("⚖️ Peso convertido: " + peso);
            return peso;
        } catch (NumberFormatException e) {
            System.err.println("❌ Erro ao converter peso: '" + texto + "'");
            return null;
        }
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    // ✅ NOVO MÉTODO: Abrir cadastro de tutor diretamente
    @FXML
    private void abrirCadastroTutor() {
        try {
            System.out.println("👤 Navegando para cadastro de tutor...");
            StackPane painel = (StackPane) txtNome.getScene().lookup("#painelConteudo");
            if (painel != null) {
                javafx.scene.Node tela = javafx.fxml.FXMLLoader.load(getClass().getResource("/telas/view/TelaCadastroTutor.fxml"));
                painel.getChildren().setAll(tela);
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao abrir cadastro de tutor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}