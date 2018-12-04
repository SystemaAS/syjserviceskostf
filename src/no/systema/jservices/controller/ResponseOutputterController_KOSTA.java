package no.systema.jservices.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import no.systema.jservices.common.dao.KodtsfDao;
import no.systema.jservices.common.dao.KostaDao;
import no.systema.jservices.common.dao.KosttDao;
import no.systema.jservices.common.dao.LevefDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KodtsfDaoService;
import no.systema.jservices.common.dao.services.KostaDaoService;
import no.systema.jservices.common.dao.services.KosttDaoService;
import no.systema.jservices.common.dao.services.LevefDaoService;
import no.systema.jservices.common.dto.KostaDto;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.common.json.PagingDto;
import no.systema.jservices.common.json.Select2Dto;

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
	KostaDaoService kostaDaoService;

	@Autowired
	KosttDaoService kosttDaoService;	

	@Autowired
	KodtsfDaoService kodtsfDaoService;		

	@Autowired
	LevefDaoService levefDaoService;		
	
	@Autowired
	private BridfDaoService bridfDaoService;

	/**
	 * Search in KOSTA
	 * 
	 * Note: Using usecase names of values instead of column-names, for tracebility
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKOSTA?user=SYSTEMA&bilagsnr=10218&innregnr=2001057
	 */
	@RequestMapping(path = "/syjsKOSTA", method = RequestMethod.GET)
	public List<KostaDto> searchKosta(@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "bilagsnr", required = false) Integer bilagsnr,
			@RequestParam(value = "innregnr", required = false) Integer innregnr,
			@RequestParam(value = "faktnr", required = false) String faktnr,
			@RequestParam(value = "levnr", required = false) Integer levnr,
			@RequestParam(value = "attkode", required = false) String attkode,
			@RequestParam(value = "komment", required = false) String komment,
			@RequestParam(value = "fradato", required = false) Integer fradato,
			@RequestParam(value = "fraperaar", required = false) Integer fraperaar,
			@RequestParam(value = "frapermnd", required = false) Integer frapermnd,
			@RequestParam(value = "reklamasjon", required = false) String reklamasjon,
			@RequestParam(value = "status", required = false) String status,
			@RequestParam(value = "fskode", required = false) String fskode,
			@RequestParam(value = "fssok", required = false) String fssok) {

		logger.info("/syjsKOSTA");

		checkUser(user);

		if (innregnr != null) {
			return get(innregnr);
		}
		
		
		KostaDto qDto = new KostaDto();
		qDto.setKabnr(innregnr);
		qDto.setKabnr2(bilagsnr);
		qDto.setKafnr(faktnr);
		qDto.setKalnr(levnr);
		qDto.setKasg(attkode);
		qDto.setKatxt(komment);
		qDto.setKabdt(fradato);
		qDto.setKbrekl(reklamasjon);
		qDto.setKast(status);
		qDto.setFskode(fskode);
		qDto.setFssok(fssok);
	
		logger.info("/syjsKOSTA, qDto="+ReflectionToStringBuilder.reflectionToString(qDto, ToStringStyle.MULTI_LINE_STYLE));
		
		
		List<KostaDao> queryResult = kostaDaoService.findAllComplex(qDto);
		List<KostaDto> dtoResult = new ArrayList<KostaDto>();
		
		queryResult.forEach(dao -> {
			KostaDto dto = KostaDto.get(dao);
			dto.setLevnavn(getLevName(dao.getKalnr()));
			dtoResult.add(dto);
		});
		
		return dtoResult;

	}

	private String getLevName(Integer kalnr) {
		LevefDao qDao = new LevefDao();
		qDao.setLevnr(kalnr);
		LevefDao resultDao = levefDaoService.find(qDao);
		String name;

		if (resultDao != null) {
			name = resultDao.getLnavn();
		} else {
			name = "ikke funnet";
		}
		
		return name;
	}

	/**
	 * Update Database DML operations File: KOSTA
	 * 
	 * @Example UPDATE:
	 *          http://gw.systema.no:8080/syjserviceskostf/syjsKOSTA_U.do?user=OSCAR&...&mode=U/A/D
	 *
	 */
	@RequestMapping(value = "syjsKOSTA_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsKOSTA_U(@RequestParam(value = "user", required = true) String user,
							@RequestParam(value = "kttyp", required = false) String kttyp, 
							HttpSession session,
							HttpServletRequest request) {
		JsonResponseWriter2<KostaDao> jsonWriter = new JsonResponseWriter2<KostaDao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		checkUser(user);

		try {
			logger.info("Inside syjsKOSTA_U.do");
			String mode = request.getParameter("mode");
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			KostaDao dao = new KostaDao();
			KostaDao resultDao = new KostaDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);

			// TODO: rulerLord

			if ("D".equals(mode)) {
				kostaDaoService.delete(dao);
			} else if ("A".equals(mode)) {
				if (kttyp == null) {
					throw new RuntimeException("kttyp can not be null.");
				}
				resultDao = kostaDaoService.create(dao, kttyp);
			} else if ("U".equals(mode)) {
				resultDao = kostaDaoService.update(dao);
			}
			if (resultDao == null) {
				errMsg = "ERROR on UPDATE ";
				status = "error ";
				dbErrorStackTrace.append("Could not add/update dao=" + ReflectionToStringBuilder.toString(dao));
				sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
			} else {
				// OK UPDATE
				sb.append(jsonWriter.setJsonResult_Common_GetComposite(userName, resultDao));
			}

		} catch (Exception e) {
			errMsg = "ERROR on UPDATE ";
			status = "error ";
			logger.info("Error:", e);
			dbErrorStackTrace.append(e.getMessage());
			sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
		}
		session.invalidate();
		return sb.toString();

	}


	/**
	 * Get KOSTT  nummerseries
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKOSTT?user=SYSTEMA&ktna=obs
	 */	
	@RequestMapping(path = "/syjsKOSTT", method = RequestMethod.GET)
	public List<KosttDao> getKostt(	HttpSession session,
									@RequestParam(value = "user", required = true) String user,
									@RequestParam(value = "ktna", required = false) String ktna) {
		
		checkUser(user);	
		
		List<KosttDao> returnList;
		
		if (ktna != null) {
			returnList = kosttDaoService.findByLike(ktna);
		} else {
			returnList = kosttDaoService.findAll(null);
		}
		
		session.invalidate();
		return returnList;
		
	}	

	
	/**
	 * Get KODTSF  att.kode
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKODTSF?user=SYSTEMA&kosfnv=a2
	 */	
	@RequestMapping(path = "/syjsKODTSF", method = RequestMethod.GET)
	public List<KodtsfDao> getKodtsf(HttpSession session,
									@RequestParam(value = "user", required = true) String user,
									@RequestParam(value = "kosfnv", required = false) String kosfnv) {

		checkUser(user);		
		
		List<KodtsfDao> returnList;
		
		if (kosfnv != null) {
			returnList = kodtsfDaoService.findByLike(kosfnv);
		} else {
			returnList = kodtsfDaoService.findAll(null);
		}
		
		session.invalidate();
		return returnList;
		
		
	}		
	
	/**
	 * Get KODTSF  att.kode
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsLEVEF?user=SYSTEMA&lnavn=transport
	 */	
	@RequestMapping(path = "/syjsLEVEFOBS", method = RequestMethod.GET)
	@Deprecated
	public PagingDto searchLevefOBSOLETE(HttpSession session,
									@RequestParam(value = "user", required = true) String user,
									@RequestParam(value = "lnavn", required = false) String lnavn) {

		checkUser(user);		
		
		PagingDto pagingDto = new PagingDto();
		List<Select2Dto> items = new ArrayList<Select2Dto>();
		List<LevefDao> returnList;
		
		if (lnavn != null) {
			returnList = levefDaoService.findByLike(0, lnavn);
		} else {
			returnList = levefDaoService.findAll(null);
		}
	
		returnList.forEach(dao -> {
			Select2Dto dto = new Select2Dto();
			dto.setId(dao.getLevnr());
			dto.setText(dao.getLnavn());
			items.add(dto);
		});
		pagingDto.setItems(items);
		pagingDto.setCountFiltered(returnList.size());
		
		session.invalidate();
		return pagingDto;
		
	}		


	/**
	 * Get KODTSF  att.kode
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsLEVEF?user=SYSTEMA&lnavn=transport
	 */	
	@RequestMapping(path = "/syjsLEVEF", method = RequestMethod.GET)
	public List<LevefDao> searchLevef(HttpSession session,
									@RequestParam(value = "user", required = true) String user,
									@RequestParam(value = "levnr", required = false) Integer levnr,
									@RequestParam(value = "lnavn", required = false) String lnavn) {

		checkUser(user);		
		
		logger.info("/syjsLEVEF");
		logger.info("levnr="+levnr+", lnavn="+lnavn);		
		
		//Ugly short-circuit
		if("NONE".equals(lnavn)) {
			return new ArrayList<LevefDao>();
		}
		
		List<LevefDao> returnList = levefDaoService.findByLike(levnr, lnavn);
		
		session.invalidate();
		return returnList;
		
	}			
	
	/**
	 * Get Leverandor navn
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsLEVEF_NAME?user=SYSTEMA&levnr=1
	 */	
	@RequestMapping(path = "/syjsLEVEF_NAME", method = RequestMethod.GET)
	public String getLevefName(HttpSession session,
								@RequestParam(value = "user", required = true) String user,
								@RequestParam(value = "levnr", required = true) Integer levnr) {

		checkUser(user);		

		String name = getLevName(levnr);
		
		session.invalidate();
		return name;
		
	}		
	
	private void checkUser(String user) {
		if (bridfDaoService.getUserName(user) == null) {
			throw new RuntimeException("ERROR: parameter, user, is not valid!");
		}
	}

	private List<KostaDto> get(Integer kabnr) {
		List<KostaDto> list = new ArrayList<KostaDto>();
		KostaDao qDao = new KostaDao();
		qDao.setKabnr(kabnr);
		KostaDao resultDao = kostaDaoService.find(qDao);
		
		if (resultDao != null) {
			list=  Arrays.asList(KostaDto.get(kostaDaoService.find(qDao)));
		} 
		
		return list;
		
		
	}
	
}
