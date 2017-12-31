package com.rdan.mybaites.session;

import com.rdan.mybaites.annotation.Column;
import com.rdan.mybaites.annotation.Id;
import com.rdan.mybaites.annotation.Table;
import com.rdan.mybaites.constant.Constant;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Arhat
 *
 */
public class Session {

	public static Connection connection;
	static {

		final String driverClass="com.mysql.jdbc.Driver";
		final String username="root";
		final String password="624813";
		final String url="jdbc:mysql://localhost:3306/bbs2009?useUnicode=true&characterEncoding=utf-8";

		try {
			Class.forName(driverClass);
			connection = DriverManager.getConnection(url, username, password);
			System.out.println("���ݿ����ӳɹ�");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * insert into database
	 * @param object
	 * @return
	 */
	public boolean save(Object object) {
		
		String className = object.getClass().getName();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String tableName = "";
		
		try {
			Class<?> classObject = Class.forName(className);
			Table table = classObject.getAnnotation(Table.class);
			
			if (table == null || (table.value()).equals("className")) {
				
				tableName = object.getClass().getSimpleName().toLowerCase();
			}
			else {
				tableName = table.value();
			}
			
			/**
			 * a list to save the field 
			 */
			List<String> dbFields = new ArrayList<String>();
			try {
				
				String getDBFieldsSql = "select COLUMN_NAME from information_schema.COLUMNS where table_name = '"
						+ tableName+"' and table_schema ='bbs2009'";
				pstmt = connection.prepareStatement(getDBFieldsSql);
				rs = pstmt.executeQuery();
				while(rs.next()) {
					dbFields.add(rs.getString("COLUMN_NAME"));
				}
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				if (rs != null) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
			}
			
			Field [] fields = classObject.getDeclaredFields();
			StringBuilder sb = new StringBuilder("insert into ");
			sb.append(tableName);
			sb.append(" values(");
			for(String dbField : dbFields) {
				
				String colName;
				for(Field field : fields) {
					
					field.setAccessible(true);
					Column column = field.getAnnotation(Column.class);
					if (column == null || column.value().equals("FIELD")) {
						colName = field.getName().toLowerCase();
					}
					else {
						colName = column.value();
					}
					/**
					 * 
					 */
					if (colName.equalsIgnoreCase(dbField)) {
						Id id = field.getAnnotation(Id.class);
						if (id != null) {
							sb.append(" null,");
							break;
						}
						else {
							/**
							 * create the get method
							 */
							StringBuilder methodName = new StringBuilder("get");
							methodName.append(((String) colName.subSequence(0, 1)).toUpperCase());
							methodName.append(colName.substring(1));
							Method method = classObject.getDeclaredMethod(methodName.toString());
							sb.append("\"");
							sb.append(method.invoke(object));
							sb.append("\"");
							sb.append(",");
							break;
						}
					}
				}
			}
			sb.delete(sb.length()-1, sb.length());
			sb.append(")");
			
			/**
			 * show the sql
			 */
			if (Constant.isShowSql) {
				System.out.println(sb.toString());
			}
			
			/**
			 * insert the object to database
			 */
			try {
				
				pstmt = connection.prepareStatement(sb.toString());
				pstmt.executeUpdate();
			
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				if (pstmt != null) {
					pstmt.close();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}
	
	public boolean update(Object object) {
		
		return false;
	}
	
	public boolean delete(Object object) {
		
		PreparedStatement pstmt = null;
		String className = object.getClass().getName();
		String tableName;
		try {
			Class<?> classObject = Class.forName(className);
			Table table = classObject.getAnnotation(Table.class);
			if (table == null || (table.value()).equals("className")) {
				
				tableName = object.getClass().getSimpleName().toLowerCase();
			}
			else {
				tableName = table.value();
			}
			
			Field [] fields = classObject.getDeclaredFields();
			StringBuilder sb = new StringBuilder("delete from ");
			sb.append(tableName);
			sb.append(" where ");
			for(Field field : fields) {
				
				Id id = field.getAnnotation(Id.class);
				if (id != null) {
					field.setAccessible(true);
					String colName = field.getName();
					/**
					 * get the get method so that can get the value
					 */
					StringBuilder methodName = new StringBuilder("get");
					methodName.append(((String) colName.subSequence(0, 1)).toUpperCase());
					methodName.append(colName.substring(1));
					Method method = classObject.getDeclaredMethod(methodName.toString());
					
					sb.append(colName);
					sb.append("=");
					if (method.getReturnType().getName().equals("int")||method.getReturnType().getName().equals("")) {
						sb.append(method.invoke(object));
					}
					else {
						sb.append("\"");
						sb.append(method.invoke(object));
						sb.append("\" ");
					}
					sb.append(" and ");
				}
				else {
					continue;
				}
			}
			sb.delete(sb.length()-4, sb.length());
			try {
				pstmt = connection.prepareStatement(sb.toString());
				pstmt.executeUpdate();
			}catch(SQLException e) {
				e.printStackTrace();
			}finally{
				if (pstmt != null) {
					pstmt.close();
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public boolean updateBySql(String sql,Object ... params) {
		
		return false;
	}
	
	public boolean deleteBySql(String sql,Object ... params) {
		
		return false;
	}
	
	public Object findById(Object ... params) {
		
		
		return null;
	}
	
	public Object findFirst(String sql,Object ... params){
		
		int paramNum = params.length;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		if (paramNum > 0) {
			
		}
		try {
			pstmt = connection.prepareStatement(sql);
			Object param = null;
			for(int i = 0; i < params.length; i++) {
				
				param = params[i];
				if (param.getClass().getSimpleName().equals("int")||param.getClass().getSimpleName().equals("Integer")) {
					
					System.out.println(param);
					pstmt.setInt(i+1, (int) param);
				}
				else if (param.getClass().getSimpleName().equals("String")) {
					pstmt.setString(i+1, (String) param);
				}
				else if(param.getClass().getSimpleName().equals("Date")) {
					pstmt.setDate(i+1, (Date) param);
				}
				else {
					
				}
			}
			rs = pstmt.executeQuery();
			if (rs.next()) {
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public Object findAllRecord() {
		
		return null;
	}
}
