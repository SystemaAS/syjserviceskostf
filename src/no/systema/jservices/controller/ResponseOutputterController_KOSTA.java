package no.systema.jservices.controller;

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

import no.systema.jservices.common.dao.KostaDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KostaDaoService;
import no.systema.jservices.common.dto.KostaDto;
import no.systema.jservices.common.json.JsonResponseWriter2;

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
	private BridfDaoService bridfDaoService;

	/**
	 * Search in KOSTA
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKOSTA?user=SYSTEMA&bilagsnr=10218&innregnr=2001057
	 */
	@RequestMapping(path = "/syjsKOSTA", method = RequestMethod.GET)
	public List<KostaDao> searchKosta(@RequestParam(value = "user", required = true) String user,
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

		logger.info("/syjsKOSTA Kilroy");

		checkUser(user);

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
		
		logger.info("qDto="+ReflectionToStringBuilder.toString(qDto));
		

		return kostaDaoService.findAllComplex(qDto);

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
	public String syjsKOSTA_U(@RequestParam(value = "user", required = true) String user, HttpSession session,
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
				resultDao = kostaDaoService.create(dao);
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

	private void checkUser(String user) {
		if (bridfDaoService.getUserName(user) == null) {
			throw new RuntimeException("ERROR: parameter, user, is not valid!");
		}
	}

}
