package no.systema.jservices.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.systema.jservices.common.dao.KostaDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.GenericDaoServiceImpl;
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

	@Autowired
	private BridfDaoService bridfDaoService;	
	

	/**
	 * Example :  http://localhost:8080/syjserviceskostf/kosta?user=SYSTEMA&bilagsnr=10218&innregnr=2001057
	 */
	@RequestMapping(path = "/kosta", method = RequestMethod.GET)
	public List<KostaDao> getKosta(	@RequestParam(value = "user", 		required = true) String user, 
									@RequestParam(value = "bilagsnr", 	required = false) String bilagsnr, 
									@RequestParam(value = "innregnr", 	required = false) String innregnr,
									@RequestParam(value = "faktnr", 	required = false) String faktnr,
									@RequestParam(value = "levnr", 		required = false) String levnr,
									@RequestParam(value = "attkode", 	required = false) String attkode,
									@RequestParam(value = "komment", 	required = false) String komment,						
									@RequestParam(value = "fradato", 	required = false) Number fradato						
								) {

		logger.info("/kosta");
		
		checkUser(user);
		
		return kostaDaoDaoService.findAll(bilagsnr, innregnr, faktnr, levnr, attkode, komment, fradato);

	}
	
	private void checkUser(String user) {
		if (bridfDaoService.getUserName(user) == null) {
			throw new RuntimeException("ERROR: parameter, user, is not valid!");
		}		
	}
	
	
	
	
}
