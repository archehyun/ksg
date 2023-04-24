package com.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class StaticItemGroup {
	
	private String day[];
	
	private int count=30;
	
	private HashMap<String, StaticDay> dayList = new HashMap<String,StaticDay>();
	
	public StaticItemGroup()
	{
		init();
	}
	public void print()
	{
		Object[] mapkey = dayList.keySet().toArray();
		
		Arrays.sort(mapkey);
		
		for(Object key:mapkey)
		{
			System.out.println(dayList.get(key));
		}
	}
	
	public List<StaticDay> getStaticDays()
	{
		List<StaticDay> valueList = dayList.values()
		          .stream()
		          .collect(Collectors.toList());
		return valueList;
	}
	
	/**
	 * init date
	 */
	public void init()	
	{	
		day = new String[count];
		
		for(int dayIndex =0;dayIndex<day.length;dayIndex++)
		{
			String day = String.format("%02d", (dayIndex+1));
			dayList.put(day, new StaticDay(day));
		}
	}
	
	public void add(String product_code,String planned_date, int planned_quantity,String confirmed_date, int confirmed_quantity, String confirmed_uom_code)
	{
		this.add(new StaticItem(product_code,planned_date, planned_quantity,confirmed_date, confirmed_quantity, confirmed_uom_code));		
	}
	
	public void add(StaticItem staticDay)
	{
		if(dayList.containsKey(staticDay.getDate()))
		{
			dayList.get(staticDay.getDate()).addItem(staticDay);
		}		
	}
	
	class StaticDay
	{
		private String day;
		
		public void setDay(String day)
		{
			this.day = day;
		}
		
		ArrayList<StaticItem> itemList;
		
		public StaticDay()
		{
			itemList = new ArrayList<StaticItemGroup.StaticItem>();
		}
		
		public StaticDay(String day) {
			this();
			this.day = day;
		}
		public void addItem(StaticItem item)
		{
			itemList.add(item);
		}
		public String getDay()
		{
			return day; 
		}
		public int getQuantity()
		{
			int total =0;
			
			for(StaticItem item:itemList)
			{
				total+=item.getQuantity();
			}
			
			return total;
		}
		
		public String toString()
		{
			return String.format("[%s,%d]", this.day, this.getQuantity());
		}
	}
	public class StaticItem
	{
		private String product_id;
		
		private String planned_date;
		
		private String confirmed_date;
		
		private long planned_quantity;
		
		private long confirmed_quantity;
		
		private Double originQuantity;
		
		private UomFactor uomFactor;
		
		public void setUomFactor(UomFactor uomFactor)
		{
			this.uomFactor =uomFactor;
		}
		
		public StaticItem(String product_id, String planned_date, long planned_quantity,String confirmed_date, long confirmed_quantity, String confirmed_uom_code)
		{
			this.product_id = product_id;
			
			this.planned_date = planned_date;
			
			this.planned_quantity = planned_quantity;
			
			this.confirmed_date = confirmed_date;
			
			this.confirmed_quantity = confirmed_quantity;
			
			originQuantity=(double) (confirmed_date==null?planned_quantity:confirmed_quantity);
		}
		
		public Double getQuantity()
		{	
			return uomFactor==null? originQuantity:originQuantity*uomFactor.factor;
		}
		public String getDate()
		{
			return confirmed_date==null?planned_date:confirmed_date;
		}
	}
	class UomFactor {
		public Integer productId;

	    public Integer fromUomId;
	    public Integer toUomId;

	    public String fromUomCode;
	    public String toUomCode;
	    public Double factor;
	}
	

}
