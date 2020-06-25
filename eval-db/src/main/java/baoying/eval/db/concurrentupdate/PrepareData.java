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

    public void popuateData() throws  Exception{

        String serialNum = "9001";
        String ordertime = "123456789";
        //String stkId = "%06d";
        String exchid = "0";

        String sql = "insert into BaoyingT3Order( SERIALNUM    ,  ORDERTIME    ,  STKID        ,  CONTRACTNUM ,  EXCHID      ,  REGID       ,  OFFERREGID  ,  DESKID     )values\n" +
                "(?, ?, ?, 'CONTRACTNUM1',?,'REGID3','OFFREGID4','DESKID5')";
        Connection conn = dataSource.getConnection();

        PreparedStatement st = conn.prepareStatement(sql);
        for(int i=0; i<15; i++){

            st.setString(1, serialNum);
            st.setString(2, ordertime);
            st.setString(3, String.format("%6d", i+1));
            st.setString(4, exchid);
            st.executeUpdate();

        }
        st.close();
        conn.commit();
        conn.close();
    }
}
