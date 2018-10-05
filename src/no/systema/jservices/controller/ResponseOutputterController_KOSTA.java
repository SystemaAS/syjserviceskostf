package no.systema.jservices.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import no.systema.jservices.common.dao.KostaDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KostaDaoService;
import no.systema.jservices.common.dto.KostaDto;

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
									@RequestParam(value = "bilagsnr", 	required = false) Integer bilagsnr, 
									@RequestParam(value = "innregnr", 	required = false) Integer innregnr,
									@RequestParam(value = "faktnr", 	required = false) String faktnr,
									@RequestParam(value = "levnr", 		required = false) Integer levnr,
									@RequestParam(value = "attkode", 	required = false) String attkode,
									@RequestParam(value = "komment", 	required = false) String komment,						
									@RequestParam(value = "fradato", 	required = false) Integer fradato,
									@RequestParam(value = "fraperaar", 	required = false) Integer fraperaar,						
									@RequestParam(value = "frapermnd", 	required = false) Integer frapermnd,
									@RequestParam(value = "reklamasjon",required = false) String reklamasjon,	
									@RequestParam(value = "status",		required = false) String status,
									@RequestParam(value = "fskode",		required = false) String fskode,
									@RequestParam(value = "fssok",		required = false) String fssok
								) {

		logger.info("/kosta");
		
		checkUser(user);
		
		KostaDto qDto = new KostaDto();
		qDto.setKabnr(bilagsnr);
		qDto.setKabnr2(innregnr);
		qDto.setKafnr(faktnr);
		qDto.setKalnr(levnr);
		qDto.setKasg(attkode);
		qDto.setKatxt(komment);
		qDto.setKabdt(fradato);
		qDto.setKbrekl(reklamasjon);
		qDto.setKast(status);
		qDto.setFskode(fskode);
		qDto.setFssok(fssok);
		
		return kostaDaoDaoService.findAllComplex(qDto);
		
	}
	
	private void checkUser(String user) {
		if (bridfDaoService.getUserName(user) == null) {
			throw new RuntimeException("ERROR: parameter, user, is not valid!");
		}		
	}
	
	
	
	
}
