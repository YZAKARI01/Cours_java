package tp1.dao;



import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
@Component("dao")
//@Repository("d")
public class DaoImpl implements IDao{

    @Override
    public double getData() {
        System.out.println("version base de donnees");
        double t = 34;
        return t;
    }
}