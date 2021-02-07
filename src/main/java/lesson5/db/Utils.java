package lesson5.db;

import lesson5.db.dao.CategoriesMapper;
import lesson5.db.dao.ProductsMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class Utils {
    public static CategoriesMapper getCategoriesMapper(String resource) throws IOException {
        SqlSession session = getSqlSession(resource);
        return session.getMapper(CategoriesMapper.class);
    }

    public static ProductsMapper getProductsMapper(String resource) throws IOException {
        SqlSession session = getSqlSession(resource);
        return session.getMapper(ProductsMapper.class);
    }
    private static SqlSession getSqlSession(String resource) throws IOException {
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession session = sqlSessionFactory.openSession(true);
        return session;
    }
}
