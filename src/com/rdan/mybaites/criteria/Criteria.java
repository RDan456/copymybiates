package com.rdan.mybaites.criteria;

import com.rdan.mybaites.annotation.Column;
import com.rdan.mybaites.annotation.Table;
import com.rdan.mybaites.session.Session;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Arhat
 *
 */
public class Criteria {

	private Class<?> object;
	private Map<String, Object> map = new HashMap<String, Object>();
	
	public Criteria(Class<?> object) {
		
		this.object = object;
	}

	public Object findFirst() {
		
		String className = object.getName();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			Class<?> classObject = Class.forName(className);
			Table table = classObject.getAnnotation(Table.class);
			String tableName = "";
			if (table == null || table.value().equals("className")) {
				
				tableName = object.getClass().getSimpleName().toLowerCase();
			}
			else {
				tableName = table.value().toString();
			}
			
			StringBuilder sb = new StringBuilder("select * from ");
			sb.append(tableName);
			sb.append(" where ");
			
			Set<String> params = map.keySet();
			for(String param : params) {
				
				Object paramValue = map.get(param);
				Field field = classObject.getDeclaredField(param);
				field.setAccessible(true);
				String dbField;
				
				if (field.getType().getSimpleName().equals("int")||field.getType().getSimpleName().equals("Integer")) {
					
					Column column = classObject.getAnnotation(Column.class);
					if (column == null || column.value().equals("FIELD")) {
						dbField = field.getName();
					}
					else {
						dbField = column.value();
					}
					sb.append(dbField);
					sb.append("=");
					sb.append(paramValue);
					sb.append(" and ");
				}
				else if (field.getType().getSimpleName().equals("String")) {
					
					Column column = classObject.getAnnotation(Column.class);
					if (column == null || column.value().equals("FIELD")) {
						dbField = field.getName();
					}
					else {
						dbField = column.value();
					}
					sb.append(dbField);
					sb.append("=");
					sb.append("\"");
					sb.append(paramValue);
					sb.append("\"");
					sb.append(" and ");
				}
				else {
					
				}
			}
			sb.delete(sb.length()-4, sb.length());
			try {
				pstmt = Session.connection.prepareStatement(sb.toString());
				rs = pstmt.executeQuery();
				
				if (rs.next()) {
					
					Method [] methods = classObject.getDeclaredMethods();
					for(Method method : methods) {
						
						if (method.getName().startsWith("set")) {
							
						}
					}
				}
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * ���ò���
	 * @return
	 */
	public void setParams(String paramName, Object value) {
		
		this.map.put(paramName, value);
	}

	public Class<?> getObject() {
		return object;
	}

	public void setObject(Class<?> object) {
		this.object = object;
	}
}
