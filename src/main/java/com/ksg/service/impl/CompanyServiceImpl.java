package com.ksg.service.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;



import com.ksg.common.exception.AlreadyExistException;
import com.ksg.dao.impl.CompanyDAOImpl;
import com.ksg.service.CompanyService;

import lombok.extern.slf4j.Slf4j;

/**

 * @FileName : CompanyServiceImpl.java

 * @Project : KSG2

 * @Date : 2021. 11. 25. 

 * @작성자 : pch

 * @변경이력 :

 * @프로그램 설명 :

 */
@Slf4j
public class CompanyServiceImpl implements CompanyService{ 

	CompanyDAOImpl companyDAO;

	public CompanyServiceImpl() {
		companyDAO = new CompanyDAOImpl();
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> selectList(Map<String, Object> param) throws SQLException {



		log.debug("param:{}", param);

		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("total", companyDAO.selectCount(param));

		resultMap.put("master", companyDAO.selectCompanyList(param));

		return resultMap;

	}

	public int update(HashMap<String, Object> param) throws SQLException{
		log.debug("param:{}", param);
		return companyDAO.update(param);

	}

	public int delete(HashMap<String, Object> param) throws SQLException{
		log.debug("param:{}", param);
		return companyDAO.deleteCompany(param);
	}

	public void insert(HashMap<String, Object> param) throws RuntimeException{
		log.debug("param:{}", param);

		try
		{
			companyDAO.insert(param);


		} catch (SQLException e1) {
			if(e1.getErrorCode()==2627)
			{

				throw new AlreadyExistException();


			}else
			{
				
				e1.printStackTrace();
			}
		}
	}

	@Override
	public int getCompanyCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public HashMap<String, Object> selectListByPage(HashMap<String, Object> param) throws SQLException {
		
		log.debug("param:{}", param);
		
		HashMap<String, Object> resultMap = new HashMap<String, Object>();

		resultMap.put("total", companyDAO.selectCount(param));

		resultMap.put("master", companyDAO.selectListByPage(param));

		resultMap.put("PAGE_NO", 1);

		return resultMap;
	}

}
