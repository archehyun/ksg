package com.ksg.config;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class KSGConfig {
	
	public KSGConfig() {
		load();
	}
	public void load()	
	{
		JSONParser parser = new JSONParser();

		try {
			
			Reader reader = Resources.getResourceAsReader("config/theme.json");
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject) obj;
			
			reader.close();
			
//			System.out.print(jsonObject);
			
			JSONObject grid = (JSONObject) jsonObject.get("grid");
			JSONObject grid_body = (JSONObject) grid.get("body");
			JSONObject grid_body_row = (JSONObject) grid_body.get("row");
			JSONObject grid_body_row_cell = (JSONObject) grid_body_row.get("cell");
			JSONObject grid_body_row_cell_selected = (JSONObject) grid_body_row.get("selected");
			
			System.out.print(grid_body_row_cell.get("color"));
			
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}
		
	}

}
