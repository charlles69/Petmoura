package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "animais")
public class Animal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String especie;

    @Column(nullable = false)
    private String raca;

    private Integer idade;
    private Float peso;
    
    @Column(name = "data_cadastro")
    private java.time.LocalDate dataCadastro;
    
    @Column(length = 1000)
    private String observacoes;

    // ✅ NOVO: Relacionamento com Tutor
    @ManyToOne
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    // Construtores
    public Animal() {}

    public Animal(String nome, String especie, String raca, Integer idade, Float peso, String observacoes, Tutor tutor) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.peso = peso;
        this.dataCadastro = java.time.LocalDate.now();
        this.observacoes = observacoes;
        this.tutor = tutor;
    }

    // Getters e Setters (manter os existentes e adicionar)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }
    
    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }
    
    public Float getPeso() { return peso; }
    public void setPeso(Float peso) { this.peso = peso; }
    
    public java.time.LocalDate getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(java.time.LocalDate dataCadastro) { this.dataCadastro = dataCadastro; }
    
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    
    // ✅ NOVO: Getter e Setter para Tutor
    public Tutor getTutor() { return tutor; }
    public void setTutor(Tutor tutor) { this.tutor = tutor; }

    @Override
    public String toString() {
        return "Animal [id=" + id + ", nome=" + nome + ", especie=" + especie + ", raca=" + raca + "]";
    }
}