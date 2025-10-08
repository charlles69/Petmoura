package dao;

// Importa lista genérica para retorno de dados
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

/**
 * Classe genérica DAO (Data Access Object) para operações com o banco. Usa
 * generics para permitir trabalhar com qualquer entidade JPA (ex: Colmeia,
 * Animal).
 */
public class DAO<E> {

    private static EntityManagerFactory emf;
    private EntityManager em;
    private Class<E> classe;

    // ✅ CORREÇÃO: Bloco estático com tratamento melhor
    static {
        try {
            emf = Persistence.createEntityManagerFactory("petmoura");
            System.out.println("✅ EntityManagerFactory criado com sucesso!");
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar EntityManagerFactory: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public DAO() {
        this(null);
    }

    public DAO(Class<E> classe) {
        this.classe = classe;
        
        // ✅ CORREÇÃO: Verifica se emf não é null antes de usar
        if (emf == null) {
            System.err.println("❌ EntityManagerFactory é null! Tentando recriar...");
            try {
                emf = Persistence.createEntityManagerFactory("Mel_Di_Fiori");
                System.out.println("✅ EntityManagerFactory recriado!");
            } catch (Exception e) {
                System.err.println("❌ Falha ao recriar EntityManagerFactory: " + e.getMessage());
                throw new RuntimeException("Não foi possível criar EntityManagerFactory", e);
            }
        }
        
        this.em = emf.createEntityManager();
        System.out.println("✅ DAO criado para: " + (classe != null ? classe.getSimpleName() : "null"));
    }

	// Inicia uma transação no banco
	public DAO<E> abrirTransacao() {
		em.getTransaction().begin();
		return this;
	}

	// Finaliza (confirma) a transação no banco
	public DAO<E> fecharTransacao() {
		em.getTransaction().commit(); 
		return this;
	}

	// Método interno para incluir um objeto
	public DAO<E> incluirE(E entidade) {
		em.persist(entidade);
		return this;
	}

	// Método para incluir um objeto com transação automática
	public DAO<E> incluirTransacional(E entidade) {
		return this.abrirTransacao().incluirE(entidade).fecharTransacao();
	}

	// Retorna todos os registros da entidade, com paginação
	public List<E> obterTodos(int quantidade, int deslocamento) {
		if (classe == null) {
			throw new UnsupportedOperationException("Classe nula");
		}
		// Cria uma consulta JPQL para buscar todos os registros
		String jpql = "select e from " + classe.getName() + " e";
		TypedQuery<E> query = em.createQuery(jpql, classe);
		query.setMaxResults(quantidade);
		query.setFirstResult(deslocamento);
		return query.getResultList();
	}

	// Busca uma entidade pelo ID
	public E obterPorID(Object id) {
		if (classe == null) {
			throw new UnsupportedOperationException("Classe nula");
		}
		return em.find(classe, id);
	}

	// Remove uma entidade pelo ID (sem transação)
	private DAO<E> removerPorId(Long id) {
		if (classe == null) {
			throw new UnsupportedOperationException("Classe nula");
		}
		E entidade = em.find(classe, id);
		if (entidade != null) {
			em.remove(entidade);
		}
		return this;
	}

	// Remove com transação automática
	public DAO<E> removerPorIdTransacional(Long id) {
		return this.abrirTransacao().removerPorId(id).fecharTransacao();
	}

	// Atualiza um objeto no banco (sem transação)
	private DAO<E> atualizar(E entidade) {
		em.merge(entidade); // Merge atualiza os dados
		return this;
	}

	// Atualiza com transação automática
	public DAO<E> atualizarTransacional(E entidade) {
		return this.abrirTransacao().atualizar(entidade).fecharTransacao();
	}

	// Fecha o EntityManager (boa prática)
	public void fecharEm() {
		em.close();
	}
	
	
	// Consulta genérica com um parâmetro
	public List<E> consultar(String jpql, Object parametro) {
	    TypedQuery<E> query = em.createQuery(jpql, classe);
	    query.setParameter(1, parametro);
	    return query.getResultList();
	}

	// Consulta genérica com vários parâmetros
	public List<E> consultar(String jpql, Object... parametros) {
	    TypedQuery<E> query = em.createQuery(jpql, classe);
	    for (int i = 0; i < parametros.length; i++) {
	        query.setParameter(i + 1, parametros[i]); // JPA usa indexação começando em 1
	    }
	    return query.getResultList();
	}

	
	
	
}
