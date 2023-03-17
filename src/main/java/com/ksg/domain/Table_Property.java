package com.ksg.domain;

/**
 * 
 * 테이블 속서 정보
 * 
 * 
 * @author 박창현
 *
 */
public class Table_Property extends BaseInfo{
	
	private String table_id;
	
	private String company_abbr;
	
	private int page;
	
	private int vesselvoycount;
	
	private String vesselvoydivider;
	
	private int under_port;
	
	private int voyage;
	
	private int table_type;// key word type
	
	private int port_type=0;
	
	private int eta=0;
	
	public String getCompany_abbr() {
		return company_abbr;
	}
	public void setCompany_abbr(String company_abbr) {
		this.company_abbr = company_abbr;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getVesselvoycount() {
		return vesselvoycount;
	}
	public void setVesselvoycount(int vesselvoycount) {
		this.vesselvoycount = vesselvoycount;
	}
	public String getVesselvoydivider() {
		return vesselvoydivider;
	}
	public void setVesselvoydivider(String vesselvoydivider) {
		this.vesselvoydivider = vesselvoydivider;
	}
	public int getUnder_port() {
		return under_port;
	}
	public void setUnder_port(int under_port) {
		this.under_port = under_port;
	}
	public int getVoyage() {
		return voyage;
	}
	public void setVoyage(int voyage) {
		this.voyage = voyage;
	}
	public String getTable_id() {
		return table_id;
	}
	public void setTable_id(String table_id) {
		this.table_id = table_id;
	}
	public int getTable_type() {
		return table_type;
	}
	public void setTable_type(int table_type) {
		this.table_type = table_type;
	}
	public int getPort_type() {
		return port_type;
	}
	public void setPort_type(int port_type) {
		this.port_type = port_type;
	}
	public int getEta() {
		return eta;
	}
	public void setEta(int eta) {
		this.eta = eta;
	}
	@Override
	public String toInfoString() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
