package no.systema.jservices.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import no.systema.jservices.common.dao.HeadfDao;
import no.systema.jservices.common.dao.KostbDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.HeadfDaoService;
import no.systema.jservices.common.dao.services.KostbDaoService;
import no.systema.jservices.common.dto.KostbDto;
import no.systema.jservices.common.json.JsonResponseWriter2;

/**
 * This controller contribute with CRUD-logic on file/table KOSTA
 * 
 * @author fredrikmoller
 *
 */
@RestController
public class ResponseOutputterController_KOSTB {

	private static Logger logger = Logger.getLogger(ResponseOutputterController_KOSTB.class.getName());

	@Autowired
	KostbDaoService kostbDaoService;

	@Autowired
	private BridfDaoService bridfDaoService;

	@Autowired
	HeadfDaoService headfDaoService;

	/**
	 * Search in KOSTA
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKOSTB_XTRA?user=SYSTEMA&kbbnr=2001114
	 */
	@RequestMapping(path = "/syjsKOSTB_XTRA", method = RequestMethod.GET)
	public List<KostbDto> getKostbInflated(	@RequestParam(value = "user", required = true) String user,
											@RequestParam(value = "kbbnr", required = true) Integer kbbnr) {

		logger.info("/syjsKOSTB_XTRA");
		logger.info("kbbnr="+kbbnr);
		

		checkUser(user);
		
		List<KostbDto> dtoList = new ArrayList<KostbDto>();
		List<KostbDao> daoList = kostbDaoService.findByKbbnr(kbbnr);

		daoList.forEach(dao -> {
			KostbDto dto = new KostbDto();
			dto = KostbDto.get(dao);
			HeadfDao headf = headfDaoService.find(dao.getKbavd(), dao.getKbopd());
			
			//TODO 
			if (headf != null) {
				dto.setOt(headf.getHeot());  
				dto.setFra(headf.getHefr());
				dto.setVal(headf.getTrverv());
				dto.setVkt1(String.valueOf(headf.getHevkt()));
				dto.setAnt(String.valueOf(headf.getHent()));
			} 
//			dto.setVkt2(?);
//			dto.setSk(?);
//			dto.setBusjett(?);
//			dto.setDiff(?);
//			dto.setGren(?);
			
			dtoList.add(dto);
			
		});
			
		return dtoList;

	}	
	
	
	/**
	 * Get List of KOSTB
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKOSTB_LIST?user=SYSTEMA&kbbnr=2001114
	 */
	@RequestMapping(path = "/syjsKOSTB_LIST", method = RequestMethod.GET)
	public List<KostbDto> getKostbList(	@RequestParam(value = "user", required = true) String user,
									@RequestParam(value = "kbbnr", required = true) Integer kbbnr) {

		logger.info("/syjsKOSTB");

		checkUser(user);

		List<KostbDao> daoList = kostbDaoService.findByKbbnr(kbbnr);
		List<KostbDto> dtoList = new ArrayList<KostbDto>();
		
		daoList.forEach(dao -> {
			dtoList.add(KostbDto.get(dao));
		});
		
		return dtoList;

	}

	/**
	 * Get specific KOSTB 
	 * 
	 * Note: Using RRN. No keys in KOSTB
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKOSTB_GET?user=SYSTEMA&rrn=2
	 */	
	@RequestMapping(path = "/syjsKOSTB_GET", method = RequestMethod.GET)
	public KostbDto getKostb(HttpSession session,
									@RequestParam(value = "user", required = true) String user,
									@RequestParam(value = "rrn", required = true) Integer rrn) {

		checkUser(user);		
		
		logger.info("/syjsKOSTA_GET");
		logger.info("rrn="+rrn);
	
		if (rrn == 0 ) {
			logger.error("rrn can not be null");
			throw new RuntimeException("rrn can not be null");
		}
		
		KostbDto dto = getKostb(rrn);
		
		if (dto == null) {
			logger.error("dto is null on rrn="+rrn);
		}
		
		session.invalidate();
		return dto;
		
	}		
	
	/**
	 * Update Database DML operations File: KOSTA
	 * 
	 * @Example UPDATE:
	 *          http://gw.systema.no:8080/syjserviceskostf/syjsKOSTB_U.do?user=OSCAR&...&mode=U/A/D
	 *
	 */
	@RequestMapping(value = "syjsKOSTB_U.do", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String syjsKOSTB_U(@RequestParam(value = "user", required = true) String user, HttpSession session,
			HttpServletRequest request) {
		JsonResponseWriter2<KostbDao> jsonWriter = new JsonResponseWriter2<KostbDao>();
		StringBuffer sb = new StringBuffer();
		String userName = null;
		String errMsg = null;
		String status = null;
		StringBuffer dbErrorStackTrace = null;

		checkUser(user);

		logger.info("Inside syjsKOSTB_U.do");
		
		
		try {
			String mode = request.getParameter("mode");
			errMsg = "";
			status = "ok";
			dbErrorStackTrace = new StringBuffer();
			KostbDao dao = new KostbDao();
			KostbDao resultDao = new KostbDao();
			ServletRequestDataBinder binder = new ServletRequestDataBinder(dao);
			binder.bind(request);

			// TODO: rulerLord

			
			if ("D".equals(mode)) {
				kostbDaoService.delete(dao);
			} else if ("A".equals(mode)) {
	
				logger.info("Create...");
				
				resultDao = kostbDaoService.create(dao);
			} else if ("U".equals(mode)) {
				
				logger.info("Update...");
				
				resultDao = kostbDaoService.update(dao);
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
			logger.error("Error:", e);
			dbErrorStackTrace.append(e.getMessage());
			sb.append(jsonWriter.setJsonSimpleErrorResult(userName, errMsg, status, dbErrorStackTrace));
		}
		session.invalidate();
		return sb.toString();

	}

	
	private KostbDto getKostb(Integer rrn) {
		KostbDao resultDao = kostbDaoService.findByRRN(rrn);
		KostbDto dto = KostbDto.get(resultDao);
		
		return dto;
		
	}		
	
	private void checkUser(String user) {
		if (bridfDaoService.getUserName(user) == null) {
			throw new RuntimeException("ERROR: parameter, user, is not valid!");
		}
	}

}
