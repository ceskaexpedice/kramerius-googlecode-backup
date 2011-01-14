package cz.incad.kramerius.impl.fedora;

import java.io.File;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.inject.Provider;

import cz.incad.kramerius.utils.database.JDBCQueryTemplate;

public class FedoraDatabaseUtils {

	static java.util.logging.Logger LOGGER = java.util.logging.Logger
			.getLogger(FedoraDatabaseUtils.class.getName());
	
	private FedoraDatabaseUtils() {}
	
	public static List<String> getRelativeDataStreamPath(String uuid, Provider<Connection> provider) throws SQLException {
		String dataStreamPath = getDataStreamPath(uuid, provider);
		LOGGER.info("datastream path is '"+dataStreamPath+"'");
		List<String> folderList = new ArrayList<String>();
        File currentFile = new File(dataStreamPath);
        while(!currentFile.getName().equals("data")) {
            folderList.add(currentFile.getName());
            currentFile = currentFile.getParentFile();
        }
        return folderList;
	}
	
	/**
	 * Returns path to file
	 * @param uuid UUID of the object 
	 * @param connection
	 * @return
	 * @throws SQLException
	 */
    public static String getDataStreamPath(String uuid, Provider<Connection> provider) throws SQLException {
        String sql = "select * from datastreampaths where token like ? order by tokendbid ASC";
        List<String> returnList = new JDBCQueryTemplate<String>(provider.get()){
            @Override
            public boolean handleRow(ResultSet rs, List<String> returnsList) throws SQLException {
                String path = rs.getString("path");
                returnsList.add(path);
                return super.handleRow(rs, returnsList);
            }
            
        }.executeQuery(sql, "uuid:"+uuid+"+IMG_FULL+%");
        return (returnList != null && !returnList.isEmpty()) ? returnList.get(0) : null;
    }	
}
