package no.systema.jservices.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import no.systema.jservices.common.dao.KostbDao;
import no.systema.jservices.common.dao.KosttDao;
import no.systema.jservices.common.dao.LevefDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KodtsfDaoService;
import no.systema.jservices.common.dao.services.KostaDaoService;
import no.systema.jservices.common.dao.services.KostbDaoService;
import no.systema.jservices.common.dao.services.KosttDaoService;
import no.systema.jservices.common.dao.services.LevefDaoService;
import no.systema.jservices.common.dto.KostaDto;
import no.systema.jservices.common.dto.KostbDto;
import no.systema.jservices.common.json.JsonResponseWriter2;
import no.systema.jservices.controller.rules.KOSTA_U;

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
	KostbDaoService kostbDaoService;	
	
	@Autowired
	KosttDaoService kosttDaoService;	

	@Autowired
	KodtsfDaoService kodtsfDaoService;		

	@Autowired
	LevefDaoService levefDaoService;		
	
	@Autowired
	private BridfDaoService bridfDaoService;
	
	@Autowired
	private KOSTA_U rulerLord;

	/**
	 * Search in KOSTA
	 * 
	 * Note: Using usecase names of values instead of column-names, for tracebility
	 * 
	 * Dto i used, in favor of Dao, to avoid roundtriping for more data. eg. levnamn.
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKOSTA?user=SYSTEMA&bilagsnr=10218&innregnr=2001057
	 */
	@RequestMapping(path = "/syjsKOSTA", method = RequestMethod.GET)
	public List<KostaDto> searchKosta(@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "bilagsnr", required = false) Integer bilagsnr,
			@RequestParam(value = "innregnr", required = false) Integer innregnr,
			@RequestParam(value = "faktnr", required = false) String faktnr,
			@RequestParam(value = "levnr", required = false) BigDecimal levnr,
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

		KostaDao qDao = new KostaDao();
		qDao.setKabnr(innregnr);
		qDao.setKabnr2(bilagsnr);
		qDao.setKafnr(faktnr);
		qDao.setKalnr(levnr);
		qDao.setKasg(attkode);
		qDao.setKatxt(komment);
		qDao.setKabdt(fradato);
		qDao.setKast(status);
	
		logger.info("/syjsKOSTA, qDto="+ReflectionToStringBuilder.reflectionToString(qDao, ToStringStyle.MULTI_LINE_STYLE));
		
		
		List<KostaDao> queryResult = kostaDaoService.findAllComplex(qDao, reklamasjon, fskode, fssok);
		List<KostaDto> dtoResult = new ArrayList<KostaDto>();
		
		queryResult.forEach(dao -> {
			KostaDto dto = KostaDto.get(dao);
			dto.setLevnavn(getLevName(dao.getKalnr().intValue()));
			dtoResult.add(dto);
		});
		
		return dtoResult;

	}

	/**
	 * Get specific KOSTA 
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKOSTA_GET?user=SYSTEMA&innregnr=1
	 */	
	@RequestMapping(path = "/syjsKOSTA_GET", method = RequestMethod.GET)
	public KostaDto getKosta(HttpSession session,
									@RequestParam(value = "user", required = true) String user,
									@RequestParam(value = "innregnr", required = true) Integer innregnr) {

		checkUser(user);		
		
		logger.info("/syjsKOSTA_GET");
		logger.info("innregnr="+innregnr);
		
		KostaDto dto = getKosta(innregnr);
		
		if (dto != null) {
			logger.error("dto is null on innregnr="+innregnr);
		}
		dto.setLevnavn(getLevName(new Integer(dto.getKalnr())));
		dto.setFordelt(getFordelt(innregnr));	
		
		
		session.invalidate();
		return dto;
		
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
		StringBuffer error = new StringBuffer();
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;
		
		logger.info("Inside syjsKOSTA_U.do");
		logger.info("kttyp="+kttyp);
		
		checkUser(user);

		try {
			String mode = request.getParameter("mode");
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			KostaDao dao = new KostaDao();
			KostaDao resultDao = new KostaDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);

			if ("D".equals(mode)) {
				//Delete
				kostaDaoService.delete(dao);
			} else if ("A".equals(mode)) {
				if (kttyp == null || kttyp.isEmpty()) {
					kttyp = "Ø"; //default
				}
				//TODO
				if (rulerLord.isValidInput(dao, user, mode, error, dbErrorStackTrace)) {
					//Create
					resultDao = createKosta(dao, kttyp, user);
					
				} else {
					errMsg = "ERROR on ADD: invalid rulerLord, error="+error.toString();
					status = "error";
					error.append(jsonWriter.setJsonSimpleErrorResult(user, errMsg, status, dbErrorStackTrace));
					logger.error(error);
				}
			} else if ("U".equals(mode)) {

				//TODO
				if (rulerLord.isValidInput(dao, user, mode, error, dbErrorStackTrace)) {
					//Update
					resultDao = updateKosta(dao, user);
					
				} else {
					errMsg = "ERROR on UPDATE: invalid rulerLord, error="+error.toString();
					status = "error";
					error.append(jsonWriter.setJsonSimpleErrorResult(user, errMsg, status, dbErrorStackTrace));
					logger.error(error);
				}				
				
			
			}
			if (resultDao == null) {
				errMsg = error.toString();
				logger.error("Error:"+ errMsg);
				status = "error";
				dbErrorStackTrace.append("Could not add/update dao=" + ReflectionToStringBuilder.toString(dao));
				error.append(jsonWriter.setJsonSimpleErrorResult(user, errMsg, status, dbErrorStackTrace));
			} else {
				// OK UPDATE
				error.append(jsonWriter.setJsonResult_Common_GetComposite(user, resultDao));
			}

		} catch (Exception e) {
			errMsg = "ERROR : ";
			status = "error";
			logger.info("Error:", e);
			dbErrorStackTrace.append(e.getMessage());
			error.append(jsonWriter.setJsonSimpleErrorResult(user, errMsg, status, dbErrorStackTrace));
		}
		session.invalidate();
		return error.toString();

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
	 * Search LEVEF -   leveradorer
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
	 * Get specific LEVEF -   leveradorer
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsLEVEF_GET?user=SYSTEMA&levnr=1
	 */	
	@RequestMapping(path = "/syjsLEVEF_GET", method = RequestMethod.GET)
	public LevefDao getLevef(HttpSession session,
									@RequestParam(value = "user", required = true) String user,
									@RequestParam(value = "levnr", required = true) Integer levnr) {

		checkUser(user);		
		
		logger.info("/syjsLEVEF");
		logger.info("levnr="+levnr);
		
		LevefDao dao = getLevef(levnr);
		
		session.invalidate();
		return dao;
		
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

	
	private KostaDao createKosta(KostaDao dao, String kttyp, String user) {
		addAudit(dao, user);
		
		return kostaDaoService.create(dao, kttyp);	
		
	}
	
	private KostaDao updateKosta(KostaDao dao, String user) {
		addAudit(dao, user);
		if (dao.getKaffdt() == null) {
			dao.setKaffdt(new BigDecimal(dao.getKabdt()));
		}
		
		return kostaDaoService.update(dao);	
		
	}
	
	private void addAudit(KostaDao dao, String user) {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd"); 
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");
		
		LocalDateTime now = LocalDateTime.now();
		String nowDate = now.format(dateFormatter);
		String nowTime = now.format(timeFormatter);

		int kadte = Integer.valueOf(nowDate);
		int katme = Integer.valueOf(nowTime);		
				
		dao.setKadte(kadte);
		dao.setKatme(katme);
		
		String kauser = bridfDaoService.getUserName(user);
		dao.setKauser(kauser);
		
	}
	
	private KostaDto getKosta(Integer kabnr) {
		KostaDao qDao = new KostaDao();
		qDao.setKabnr(kabnr);
		KostaDao resultDao = kostaDaoService.find(qDao);
		
		if (resultDao != null) {
			KostaDto dto = KostaDto.get(resultDao);
			dto.setLevnavn(getLevName(resultDao.getKalnr().intValue()));
			return dto;
		} else {
			return null;
		}
		
	}	

	private String getFordelt(Integer kabnr) {
		logger.info("::getFordelt::, kabnr="+kabnr);
		double fordelt = kostbDaoService.getFordelt(kabnr);
	
		logger.info("fordelt="+fordelt);
		
		
		
		return String.valueOf(fordelt);
		
	}	
	
	private LevefDao getLevef(Integer kalnr) {
		LevefDao qDao = new LevefDao();
		qDao.setLevnr(kalnr);

		return levefDaoService.find(qDao);
		
	}	
	
	
	private String getLevName(Integer kalnr) {
		LevefDao resultDao = getLevef(kalnr);
		String name;

		if (resultDao != null) {
			name = resultDao.getLnavn();
		} else {
			name = "ikke funnet";
		}
		
		return name;
	}
	
}
