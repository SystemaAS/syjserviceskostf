package no.systema.jservices.controller.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.systema.jservices.common.dao.KostaDao;
import no.systema.jservices.common.dao.services.ValufDaoService;
import no.systema.jservices.common.json.JsonResponseWriter;
import no.systema.jservices.common.util.StringUtils;

/**
 * 
 * @author fredrikmoller
 *
 */
@Service
public class KOSTA_U {

	@Autowired
	private JsonResponseWriter jsonWriter;
	@Autowired
	private MessageSourceHelper messageSourceHelper;
	
	@Autowired
	ValufDaoService valufDaoService;
	
	/**
	 * Validate null values and exist controls i db.
	 * 
	 * @param dao
	 * @param user
	 * @param mode
	 * @param dbErrorStackTrace 
	 * @param sb 
	 * @return
	 */
	public boolean isValidInput(KostaDao dao, String user, String mode, StringBuffer errors, StringBuffer dbErrors) {
		boolean retval = true;
		if ( (StringUtils.hasValue(dao.getKaval()) && !valufDaoService.exist(dao.getKaval()))) {
			errors.append(jsonWriter.setJsonSimpleErrorResult(user,
					messageSourceHelper.getMessage("systema.kostf.bilag.hode.error.kaval", new Object[] { dao.getKaval()}), "error", dbErrors));
			retval = false;					
		}
		
		return retval;
		
	}
	
}
