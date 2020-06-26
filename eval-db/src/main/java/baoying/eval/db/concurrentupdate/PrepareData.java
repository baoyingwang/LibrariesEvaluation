package baoying.eval.db.concurrentupdate;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class PrepareData {
    private BasicDataSource dataSource;
    PrepareData(BasicDataSource dataSource){
        this.dataSource = dataSource;
    }

    public void cleanTable() throws Exception{

        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate("delete from BaoyingT3Order");
        st.close();
        conn.commit();
        conn.close();

    }

    public void dropTable() throws Exception{

        try{
            Connection conn = dataSource.getConnection();
            Statement st = conn.createStatement();
            st.executeUpdate("drop table BaoyingT3Order");
            st.close();
            conn.commit();
            conn.close();
        }catch (Exception e){
            if(e.getMessage().indexOf("ORA-00942")>=0){
                System.out.println("ignore table/view not exists error - "+ e.getMessage());
            }else{
                e.printStackTrace();
            }

        }


    }
    public void recreateTable() throws Exception{

        dropTable();

        Connection conn = dataSource.getConnection();
        Statement st = conn.createStatement();
        st.executeUpdate("create table BaoyingT3Order"
                + "("
                + "  SERIALNUM           NUMBER(8) not null,"
                + "  ORDERTIME           NUMBER(14) not null,"
                + "  STKID               VARCHAR2(8) not null,"
                + ""
                + "  CONTRACTNUM         VARCHAR2(30) ,"
                + "  EXCHID              VARCHAR2(1),"
                + "  REGID               VARCHAR2(10),"
                + "  OFFERREGID          VARCHAR2(10) ,"
                + "  DESKID              VARCHAR2(8),"
                + "KNOCKQTY    NUMBER(15),"
                + ""
                + ""
                + "  CONSTRAINT PK_BaoyingT3Order  PRIMARY KEY (ORDERTIME, SERIALNUM, STKID)"
                + ")");
        st.close();
        conn.commit();
        conn.close();

    }

}
