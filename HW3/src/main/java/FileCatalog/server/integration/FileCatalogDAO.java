package FileCatalog.server.integration;

import FileCatalog.server.model.File;
import FileCatalog.server.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

public class FileCatalogDAO {

    private final EntityManagerFactory emFactory;
    private final ThreadLocal<EntityManager> threadLocalEntityManager = new ThreadLocal<>();

    public FileCatalogDAO() {
        emFactory = Persistence.createEntityManagerFactory("fileCatalogPersistenceUnit");
    }

    private EntityManager begin() {
        EntityManager em = emFactory.createEntityManager();
        threadLocalEntityManager.set(em);
        EntityTransaction transaction = em.getTransaction();
        if (!transaction.isActive()) {
            transaction.begin();
        }
        return em;
    }

    private void commit() {
        threadLocalEntityManager.get().getTransaction().commit();
    }
    public User findUserByName(String userName, boolean endTransaction) {
        if (userName == null) {
            return null;
        }
        try {
            EntityManager em = begin();
            try {
                return em.createNamedQuery("findUserByName", User.class).
                        setParameter("userName", userName).getSingleResult();
            } catch (NoResultException noSuchAccount) {
                return null;
            }
        } finally {
            if (endTransaction) {
                commit();
            }
        }
    }

    public void createUser(User user) {
        try {
            EntityManager em = begin();
            em.persist(user);
        } finally {
            commit();
        }
    }

    public User verify(String userName, String password) {
        EntityManager em = begin();
        try{
        return em.createNamedQuery("verifyUser", User.class).
                setParameter("userName", userName).
                setParameter("password", password)
                .getSingleResult();
        }
        catch(Exception e){
            return null;
        }

    }

    public File findFileByName(String fileName, boolean endTransaction) {
        if (fileName == null) {
            return null;
        }
        try {
            EntityManager em = begin();
            try {
                return em.createNamedQuery("findFileByName", File.class).
                        setParameter("fileName", fileName).getSingleResult();
            } catch (NoResultException noSuchFile) {
                return null;
            }
        } finally {
            if (endTransaction) {
                commit();
            }
        }
    }

    public void uploadFile(File file) {
        try {
            EntityManager em = begin();
            em.persist(file);
        } finally {
            commit();
        }
    }

    public File updateFileByName(String oldFileName, String newFileName, boolean endTransaction) {
        if (oldFileName.equals("") || newFileName.equals("")) {
            return null;
        }
        try {
            EntityManager em = begin();
            em.createNamedQuery("updateFileByName", File.class).
                    setParameter("oldFileName", oldFileName)
                    .setParameter("newFileName", newFileName)
                    .executeUpdate();
        } finally {
            if (endTransaction) {
                commit();
            }
        }
        return findFileByName(newFileName, true);
    }

    public void deleteFileByName(String fileName){
        try {
            EntityManager em = begin();
            em.createNamedQuery("deleteFileByName", File.class).
                    setParameter("fileName", fileName).executeUpdate();
        } finally {
            commit();
        }
    }

    public List<File> findAllFiles(boolean endTransaction) {
        try {
            EntityManager em = begin();
            try {
                return em.createNamedQuery("findAllFiles", File.class).getResultList();
            } catch (NoResultException noSuchAccount) {
                return new ArrayList<>();
            }
        } finally {
            commit();
        }
    }
}
