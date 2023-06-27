package com.dtp.api.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.swing.JOptionPane;

import com.dtp.api.annotation.ControlMethod;
import com.dtp.api.exception.AlreadyExistException;
import com.dtp.api.exception.ApiCallException;
import com.dtp.api.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ksg.common.model.CommandMap;
import com.ksg.workbench.common.comp.View;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public abstract class AbstractController {


	protected CommandMap model = new CommandMap();

	protected ObjectMapper objectMapper = new ObjectMapper();

	protected View view; 

	public void setView(View view)
	{
		this.view = view; 
	}

	public void call(String serviceId, CommandMap param, View view) 
	{
		try
		{
			setView(view);
			call(serviceId, param);
			
		}catch(RuntimeException e)
		{	
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

	}

	public CommandMap getModel()
	{
		return model;
	}

	/**
	 * 
	 * @param serviceId
	 * @param param
	 */
	private void call(String serviceId, CommandMap param) 
	{
		log.debug("serviceId:{}, param:{}",serviceId, param);

		CommandMap model =new CommandMap();

		String errMessage =null;

		try {

			Method[] declaredMethods = getClass().getDeclaredMethods();
			for(Method method :declaredMethods)
			{
				// Check if PrintAnnotation is applied
				if (method.isAnnotationPresent(ControlMethod.class))
				{
					ControlMethod methoAnnotation = method.getAnnotation(ControlMethod.class);

					if(methoAnnotation.serviceId().equals(serviceId))
					{   
						model=(CommandMap)method.invoke(this, param );                            
						if(model==null) model = new CommandMap();
						model.put("serviceId", serviceId);
						model.put("success", true);
						return;
					}
				}
			}

			throw new ApiCallException("service not founded : "+serviceId);

		} catch (ApiCallException e) {
			
			model.put("success", false);
			model.put("error", e.getMessage());
			
			throw new RuntimeException(e.getMessage());
		}

		catch(InvocationTargetException e)
		{
			e.printStackTrace();
			Exception targetExcpetion=(Exception) e.getTargetException();
			if(targetExcpetion instanceof AlreadyExistException || targetExcpetion instanceof ResourceNotFoundException)
			{

				errMessage = targetExcpetion.getMessage();
				model.put("success", false);
				model.put("error", errMessage);
			}
			else{
				errMessage ="unhandeld error : "+targetExcpetion.getMessage();                          
				model.put("success", false);
				model.put("error", errMessage);

			}
			throw new RuntimeException(e.getMessage());
		}

		catch (Exception e) {

			e.printStackTrace();
			errMessage ="unhandeld error:"+e.getMessage();                          
			model.put("success", false);
			model.put("error", errMessage);
			
			throw new RuntimeException(e.getMessage());


		}
		finally
		{
			model.setService_id(serviceId);
			
			if(view != null)
			{
				view.setModel( model);
				view.updateView();
			}
		}

	}

}
