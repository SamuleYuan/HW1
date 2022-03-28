import org.hibernate.jpa.HibernatePersistenceProvider;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class MyJPADemo {

    private DataSource getDataSource() {
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUser("");
        dataSource.setPassword("");
        dataSource.setUrl("jdbc:postgresql://localhost:");
        return dataSource;
    }

    private Properties getProperties() {
        final Properties properties = new Properties();
        properties.put( "hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect" );
        properties.put( "hibernate.connection.driver_class", "org.postgresql.Driver" );
        return properties;
    }

    private EntityManagerFactory entityManagerFactory(DataSource dataSource, Properties hibernateProperties ){
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan( "com/example/java20il2021/week4/day15/demo3");
        em.setJpaVendorAdapter( new HibernateJpaVendorAdapter() );
        em.setJpaProperties( hibernateProperties );
        em.setPersistenceUnitName( "demo-unit" );
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.afterPropertiesSet();
        return em.getObject();
    }

    public static void main(String[] args) {
        MyJPADemo jpaDemo = new MyJPADemo();
        DataSource dataSource = jpaDemo.getDataSource();
        Properties properties = jpaDemo.getProperties();
        EntityManagerFactory entityManagerFactory = jpaDemo.entityManagerFactory(dataSource, properties);
        EntityManager em = entityManagerFactory.createEntityManager();
        PersistenceUnitUtil unitUtil = entityManagerFactory.getPersistenceUnitUtil();

        addToJunctionTable3(em);
        withoutOrphanRemove(em);

    }

    // curd create update retrieve delete

    private static void create(EntityManager em, String s_id, String t_id){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Student s = em.find(Student.class, s_id);
        Teacher t = em.find(Teacher.class, t_id);
        Teacher_Student ts = new Teacher_Student();
        ts.setStu(s);
        ts.setTeacher(t);
        em.persist(ts);
        tx.commit();
    }

    private static void update(EntityManager em,String s_id, String t_id){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createNativeQuery("INSERT INTO TEACHER_STUDENT (S_ID, T_ID) VALUES (?, ?)");
        query.setParameter(1, s_id);
        query.setParameter(2, t_id);
        query.executeUpdate();
        tx.commit();
    }
    private static List<Teacher_Student> retriveByStudentID(EntityManager em, String s_id){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createNativeQuery("SELECT * FROM TEACHER_STUDENT WHERE S_ID = ?1");
        query.setParameter(1, s_id);
        List<Teacher_Student> teacher_students = new ArrayList<>();
        teacher_students = query.getResultList();
        tx.commit();
        return teacher_students;
    }

    private static List<Teacher_Student> retriveByTeacherID(EntityManager em, String t_id){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createNativeQuery("SELECT * FROM TEACHER_STUDENT WHERE T_ID = ?1");
        query.setParameter(1, t_id);
        List<Teacher_Student> teacher_students = new ArrayList<>();
        teacher_students = query.getResultList();
        tx.commit();
        return teacher_students;
    }

    private static void delete(EntityManager em, String s_id, String t_id){
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createQuery("select s from Student s join fetch s.teacher_students ts where s.id = ?1");
        query.setParameter(1, s_id);
        Student s = (Student) query.getSingleResult();
        Teacher t = em.find(Teacher.class, t_id);
        List<Teacher_Student> teacher_students = new ArrayList<>();
        for(Teacher_Student ts: s.getTeacher_students()) {
            if(ts.getTeacher().getId().equals(t.getId())) {
                teacher_students.add(ts);
                em.remove(ts);
            }
        }
        s.getTeacher_students().removeAll(teacher_students);
        t.getTeacher_students().removeAll(teacher_students);
        tx.commit();
    }

}

