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

import no.systema.jservices.common.dao.KostbDao;
import no.systema.jservices.common.dao.services.BridfDaoService;
import no.systema.jservices.common.dao.services.KostbDaoService;
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

	/**
	 * Search in KOSTA
	 * 
	 * Example :
	 * http://localhost:8080/syjserviceskostf/syjsKOSTB?user=SYSTEMA&innregnr=2001075
	 */
	@RequestMapping(path = "/syjsKOSTB", method = RequestMethod.GET)
	public List<KostbDao> getKostb(	@RequestParam(value = "user", required = true) String user,
									@RequestParam(value = "kbbnr", required = true) Integer kbbnr) {

		logger.info("/syjsKOSTB");

		checkUser(user);

		return kostbDaoService.findByKbbnr(kbbnr);

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

		try {
			logger.info("Inside syjsKOSTB_U.do");
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
				resultDao = kostbDaoService.create(dao);
			} else if ("U".equals(mode)) {
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
