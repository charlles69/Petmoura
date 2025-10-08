package crud_tutor;

import dao.DAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import model.Tutor;

public class TutorCreateController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCpf;
    @FXML private TextField txtTelefone;
    @FXML private TextField txtEmail;
    @FXML private TextArea txtEndereco;

    private Tutor tutorEmEdicao;
    private boolean modoEdicao = false;

    @FXML
    public void initialize() {
        System.out.println("✅ TutorCreateController inicializado!");
    }

    public void carregarTutorParaEdicao(Tutor tutor) {
        this.tutorEmEdicao = tutor;
        this.modoEdicao = true;
        
        txtNome.setText(tutor.getNome());
        txtCpf.setText(tutor.getCpf());
        txtTelefone.setText(tutor.getTelefone());
        txtEmail.setText(tutor.getEmail());
        txtEndereco.setText(tutor.getEndereco());
    }

    @FXML
    private void salvarTutor() {
        try {
            if (!validarCampos()) {
                mostrarAlerta("Campos Obrigatórios", "Preencha os campos obrigatórios.", Alert.AlertType.WARNING);
                return;
            }

            String nome = txtNome.getText();
            String cpf = txtCpf.getText();
            String telefone = txtTelefone.getText();
            String email = txtEmail.getText();
            String endereco = txtEndereco.getText();

            if (modoEdicao && tutorEmEdicao != null) {
                tutorEmEdicao.setNome(nome);
                tutorEmEdicao.setCpf(cpf);
                tutorEmEdicao.setTelefone(telefone);
                tutorEmEdicao.setEmail(email);
                tutorEmEdicao.setEndereco(endereco);

                new DAO<>(Tutor.class).atualizarTransacional(tutorEmEdicao);
                mostrarAlerta("Sucesso", "Tutor atualizado com sucesso!", Alert.AlertType.INFORMATION);
            } else {
                Tutor novoTutor = new Tutor(nome, cpf, telefone, email, endereco);
                new DAO<>(Tutor.class).incluirTransacional(novoTutor);
                mostrarAlerta("Sucesso", "Tutor cadastrado com sucesso!", Alert.AlertType.INFORMATION);
            }

            voltarParaLista();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Falha ao salvar tutor: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void limparCampos() {
        txtNome.clear();
        txtCpf.clear();
        txtTelefone.clear();
        txtEmail.clear();
        txtEndereco.clear();
        modoEdicao = false;
        tutorEmEdicao = null;
    }

    @FXML
    private void voltarParaLista() {
        try {
            StackPane painel = (StackPane) txtNome.getScene().lookup("#painelConteudo");
            if (painel != null) {
                javafx.scene.Node tela = javafx.fxml.FXMLLoader.load(getClass().getResource("/telas/view/TelaListaTutor.fxml"));
                painel.getChildren().setAll(tela);
            }
        } catch (Exception e) {
            System.err.println("❌ Erro ao voltar: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        boolean valido = true;
        
        if (txtNome.getText() == null || txtNome.getText().trim().isEmpty()) {
            destacarCampoErro(txtNome);
            valido = false;
        } else removerDestaqueErro(txtNome);
        
        if (txtCpf.getText() == null || txtCpf.getText().trim().isEmpty()) {
            destacarCampoErro(txtCpf);
            valido = false;
        } else removerDestaqueErro(txtCpf);
        
        return valido;
    }

    private void destacarCampoErro(Control campo) {
        campo.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void removerDestaqueErro(Control campo) {
        campo.setStyle("");
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}