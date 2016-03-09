package com.sunsharing.eos.clientexample.dao;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.List;

/**
 * <pre><b><font color="blue">SimpleHibernateDao</font></b></pre>
 * <p/>
 * <pre><b>&nbsp;--描述说明--</b></pre>
 * <pre></pre>
 * <pre>
 * <b>--样例--</b>
 *   SimpleHibernateDao obj = new SimpleHibernateDao();
 *   obj.method();
 * </pre>
 * JDK版本：JDK1.4.2
 *
 * @author <b>李自立</b>
 */
@SuppressWarnings("unchecked")
public class SimpleHibernateDao<T, PK extends Serializable> {
    /**
     *
     */
    protected Logger logger = Logger.getLogger(getClass());
    /**
     *
     */
    protected SessionFactory sessionFactory;
    /**
     *
     */
    protected Class<T> entityClass;

    /**
     * 用于扩展的DAO子类使用的构造函数.
     * <p/>
     * 通过子类的范型定义取得对象类型Class. eg. public class UserDao extends SimpleHibernateDao<User,
     * Long>
     */
    public SimpleHibernateDao() {
        //this.entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
    }

    /**
     * 用于Service层直接使用SimpleHibernateDAO的构造函数. eg. SimpleHibernateDao<User,
     * Long> userDao = new SimpleHibernateDao<User, Long>(sessionFactory,
     * User.class);
     */
    public SimpleHibernateDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
        this.sessionFactory = sessionFactory;
        this.entityClass = entityClass;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * 采用@Autowired按类型注入SessionFactory,当有多个SesionFactory的时候Override本函数.
     *
     * @param sessionFactory sessionFactory
     */
    @Autowired
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 保存新增或修改的对象.
     *
     * @param entity 对象
     */
    public void save(final T entity) {
        Assert.notNull(entity);
        getSession().save(entity);
        logger.debug("save entity: " + entity);
    }

    /**
     * 保存新增或修改的对象,返回保存后主键ID
     *
     * @param entity 对象
     * @return String 返回保存后的主键
     */
    public String saveObject(final T entity) {
        Assert.notNull(entity);
        Serializable id = getSession().save(entity);
        getSession().flush();
        logger.debug("save entity: " + entity);
        return id.toString();
    }

    /**
     * 保存新增或修改的对象.
     *
     * @param entity 对象.
     */
    public void update(final T entity) {
        Assert.notNull(entity);
        getSession().flush();
        getSession().clear();
        //getSession().update(entity);
        getSession().merge(entity);
        //getSession().close();
        logger.debug("update entity: " + entity);
    }

    /**
     * 保存新增或修改的对象.
     *
     * @param entity 对象
     */
    public void saveOrUpdate(final T entity) {
        Assert.notNull(entity);
        getSession().flush();
        getSession().clear();
        getSession().saveOrUpdate(entity);
        logger.debug("saveorupdate entity: " + entity);
    }

    /**
     * 删除对象.
     *
     * @param entity 对象必须是session中的对象或含id属性的transient对象.
     */
    public void delete(final T entity) {
        Assert.notNull(entity);
        getSession().delete(entity);
        getSession().flush();
        logger.debug("delete entity: " + entity);
    }

    /**
     * 按id删除对象.
     *
     * @param id 主键
     */
    public void delete(final PK id) {
        Assert.notNull(id);
        delete(get(id));
        logger.debug("delete entity " + entityClass.getSimpleName() + ",id is " + id + "");
    }

    /**
     * 按id获取对象.
     *
     * @param id 主键
     * @return T 返回对象实例
     */
    public T get(final PK id) {
        Assert.notNull(id);
        return (T) getSession().get(entityClass, id);
    }

    /**
     * 获取全部对象.
     *
     * @return T 返回全部对象列表
     */
    public List<T> getAll() {
        return findByCriteria();
    }

    /**
     * 按属性查找对象列表,匹配方式为相等.
     *
     * @param propertyName 属性名
     * @param value        属性值
     * @return List<T> 返回全部对象列表
     */
    public List<T> findByProperty(final String propertyName, final Object value) {
        Assert.hasText(propertyName);
        Criterion criterion = Restrictions.eq(propertyName, value);
        return findByCriteria(criterion);
    }

    /**
     * 按属性查找唯一对象,匹配方式为相等.
     *
     * @param propertyName 属性名
     * @param value        属性值
     * @return T 返回对象实例
     */
    public T findUniqueByProperty(final String propertyName, final Object value) {
        Assert.hasText(propertyName);
        Criterion criterion = Restrictions.eq(propertyName, value);
        return (T) createCriteria(criterion).uniqueResult();
    }

    /**
     * 按HQL查询对象列表.
     *
     * @param hql    hql
     * @param values 数量可变的参数
     * @return List<T> 返回全部对象列表
     */
    public List<T> find(final String hql, final Object... values) {
        return createQuery(hql, values).list();
    }

    /**
     * 按HQL查询唯一对象.
     *
     * @param hql    hql
     * @param values 参数
     * @return Object 返回符合条件的唯一对象
     */
    public Object findUnique(final String hql, final Object... values) {
        return createQuery(hql, values).uniqueResult();
    }

    /**
     * 按HQL查询Integer类型结果.
     *
     * @param hql    hql
     * @param values 参数
     * @return Integer 返回符合条件对象的个数
     */
    public Integer findInt(final String hql, final Object... values) {
        return (Integer) findUnique(hql, values);
    }

    /**
     * 按HQL查询Long类型结果.
     *
     * @param hql    hql
     * @param values 参数
     * @return Long 返回符合条件对象的个数（Long）
     */
    public Long findLong(final String hql, final Object... values) {
        return (Long) findUnique(hql, values);
    }

    /**
     * 根据查询HQL与参数列表创建Query对象.
     * <p/>
     * 返回对象类型不是Entity时可用此函数灵活查询.
     *
     * @param values      数量可变的参数
     * @param queryString 查询语句
     * @return Query Query
     */
    public Query createQuery(final String queryString, final Object... values) {
        Assert.hasText(queryString);
        Query query = getSession().createQuery(queryString);
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
        return query;
    }

    /**
     * 按Criteria查询对象列表.
     *
     * @param criterions 数量可变的Criterion.
     * @return List<T>  返回对象列表
     */
    public List<T> findByCriteria(final Criterion... criterions) {
        return createCriteria(criterions).list();
    }

    /**
     * 根据Criterion条件创建Criteria.
     * <p/>
     * 返回对象类型不是Entity时可用此函数灵活查询.
     *
     * @param criterions 数量可变的Criterion.
     * @return Criteria Criteria
     */
    public Criteria createCriteria(final Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(entityClass);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    /**
     * 判断对象的属性值在数据库内是否唯一.
     * <p/>
     * 在修改对象的情景下,如果属性新修改的值(value)等于属性原来的值(orgValue)则不作比较.
     *
     * @param propertyName 属性名
     * @param newValue     新属性值
     * @param orgValue     原来的属性值
     * @return boolean 对象的属性值在数据库内是否唯一
     */
    public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object orgValue) {
        if (newValue == null || newValue.equals(orgValue)) {
            return true;
        }
        Object object = findUniqueByProperty(propertyName, newValue);
        return (object == null);
    }

    /**
     * 取得对象的主键名.
     *
     * @return String  对象的主键名
     */
    public String getIdName() {
        ClassMetadata meta = getSessionFactory().getClassMetadata(entityClass);
        Assert.notNull(meta, "Class " + entityClass.getSimpleName() + " not define in HibernateSessionFactory.");
        return meta.getIdentifierPropertyName();
    }
}
