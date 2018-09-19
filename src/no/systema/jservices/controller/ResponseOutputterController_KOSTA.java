package no.systema.jservices.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.systema.jservices.common.dao.KostaDao;
import no.systema.jservices.common.dao.services.KostaDaoService;

/**
 * This controller contribute with CRUD-logic on file/table KOSTA
 * 
 * @author fredrikmoller
 *
 */
@RestController
public class ResponseOutputterController_KOSTA {

	private static Logger logger = Logger.getLogger(ResponseOutputterController_KOSTA.class.getName());	

	@Autowired
	KostaDaoService kostaDaoDaoService;
	
	/**
	 * Example :  http://localhost:8080/syjserviceskostf/kosta.do?user=SYSTEMA
	 * @param kundnr
	 * @param fraDato
	 * @return
	 */
	@RequestMapping(path = "/kosta.do", method = RequestMethod.GET)
	public List<KostaDao> doKosta(@RequestParam("user") String user) {
	
		return kostaDaoDaoService.findAll(null);
		
	
	}
	
}
