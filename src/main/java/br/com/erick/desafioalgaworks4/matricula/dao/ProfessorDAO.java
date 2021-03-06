package br.com.erick.desafioalgaworks4.matricula.dao;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;

import br.com.erick.desafioalgaworks4.matricula.modelo.Disciplina;
import br.com.erick.desafioalgaworks4.matricula.modelo.Professor;
import br.com.erick.desafioalgaworks4.matricula.service.NegocioException;

/**
 * Camada de persistencia da entidade {@link Professor}.
 * @author Erick Alves
 *
 */
public class ProfessorDAO extends DAO<Professor> {

	@Inject
	public ProfessorDAO(EntityManager entityManager, Logger logger) {
		super(entityManager, logger);
	}

	@Override
	protected Class<Professor> getEntityClass() {
		return Professor.class;
	}
	
	public Professor buscarComDisciplinas(Long registroProfessor) throws NegocioException{
		try {
			
			StringBuilder jpql = new StringBuilder();
			
			jpql.append("SELECT p FROM Professor p ");
			jpql.append(" LEFT JOIN FETCH p.disciplinas d ");
			jpql.append(" WHERE p.codigo = :registroProfessor ");
			
			TypedQuery<Professor> query = super.entityManager.createQuery(jpql.toString(), Professor.class);
			query.setParameter("registroProfessor", registroProfessor);
			return query.getSingleResult();
		} catch (NoResultException nre) {
			super.logger.error("Nao ha resultados para esta busca", nre);
			throw new NegocioException("Nenhuma disciplina encontrada para o professor"+ registroProfessor);
		} catch (NonUniqueResultException nure) {
			super.logger.error("Ha mais de um resultado retornado", nure);
			throw new NegocioException("Um erro ocorreu, contate o administrador do sistema");
		} catch (IllegalStateException ise) {
			super.logger.error("Erro ao tentar buscar um resultado utilizando uma instrucao JPQL UPDATE ou DELETE ao inves de uma instrucao SELECT", ise);
			throw new NegocioException("Um erro ocorreu, contate o administrador do sistema");
		} catch (QueryTimeoutException qte) {
			super.logger.error("Tempo de consulta excedido", qte);
			throw new NegocioException("Um erro ocorreu, contate o administrador do sistema");
		}
	}
	
	public List<Professor> buscarProfessoresPelaDisciplina(Long codigoDisciplina) throws NegocioException {
		
		try {
			StringBuilder jpql = new StringBuilder();
			
			jpql.append("SELECT p FROM Professor p ");
			jpql.append(" JOIN FETCH p.disciplinas d ");
			jpql.append(" WHERE d.codigo = :codigoDisciplina ");
			jpql.append(" ORDER BY d.nome ");
			
			TypedQuery<Professor> query = super.entityManager.createQuery(jpql.toString(), Professor.class);
			query.setParameter("codigoDisciplina", codigoDisciplina);
			
			return query.getResultList();
			
		} catch(PersistenceException pe) {
			super.logger.error("Tempo de consulta excedido", pe);
			throw new NegocioException("Um erro ocorreu, contate o administrador do sistema");
		} catch (IllegalStateException ise) {
			super.logger.error("Erro ao tentar buscar um resultado utilizando uma instrucao JPQL UPDATE ou DELETE ao inves de uma instrucao SELECT", ise);
			throw new NegocioException("Um erro ocorreu, contate o administrador do sistema");
		}
	}
}
