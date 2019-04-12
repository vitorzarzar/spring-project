package challenge.springproject.persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public abstract class CrudDao<T> {

    private final Class<T> type;

    public CrudDao(Class<T> type) {
        this.type = type;
    }

    @Autowired
    private SessionFactory sessionFactory;

    public void save(T entity) {
        Session session = this.sessionFactory.getCurrentSession();
        session.save(entity);
    }

    public T getById(Long id) {
        Session session = this.sessionFactory.getCurrentSession();
        return session.load(type, id);
    }

    public Object getGeneric(String table, String field, Object value) {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from " + table + " where :field = :value");
        query.setParameter("field", field);
        query.setParameter("value", value);
        return query.getSingleResult();
    }
}
