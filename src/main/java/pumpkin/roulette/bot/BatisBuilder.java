package pumpkin.roulette.bot;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class BatisBuilder {
    SqlSessionFactory sqlSessionFactory;

    public BatisBuilder() throws IOException {
        sqlSessionFactory = buildFactory();
    }

    public SqlSession getSession () {
        return sqlSessionFactory.openSession(true);
    }

    SqlSessionFactory buildFactory() throws IOException{
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory;
    }
}
