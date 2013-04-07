/*
 *  fra2015
 *  https://github.com/geosolutions-it/fra2015
 *  Copyright (C) 2007-2012 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 *  GPLv3 + Classpath exception
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.geosolutions.fra2015.mvc.controller;

import static it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.SURVEY_INSTANCES;
import it.geosolutions.fra2015.mvc.controller.utils.FlashAttributesHandler;
import it.geosolutions.fra2015.mvc.controller.utils.ReviewerUtils;
import it.geosolutions.fra2015.mvc.controller.utils.StatusUtils;
import it.geosolutions.fra2015.server.model.survey.Question;
import it.geosolutions.fra2015.server.model.survey.Status;
import it.geosolutions.fra2015.server.model.survey.SurveyInstance;
import it.geosolutions.fra2015.server.model.user.User;
import it.geosolutions.fra2015.services.FeedbackService;
import it.geosolutions.fra2015.services.SurveyCatalog;
import it.geosolutions.fra2015.services.SurveyService;
import it.geosolutions.fra2015.services.UserService;
import it.geosolutions.fra2015.services.exception.BadRequestServiceEx;
import it.geosolutions.fra2015.services.exception.NotFoundServiceEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author DamianoG
 * 
 */
@Controller
@RequestMapping("/reviewersubmit/{country}")
public class ReviewerSubmitController {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private SurveyService surveyService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private SurveyCatalog catalog;
    
    private static final Logger LOGGER = Logger.getLogger(ReviewerSubmitController.class);

    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(@PathVariable(value = "country") String country, HttpServletRequest request, Model model,
            HttpSession session) {

        model.addAttribute("context","surveylist");
        User su = (User) session.getAttribute("sessionUser");
        if (su == null) {
            return "redirect:/login";
        }

        Map<String, SurveyInstance> surveyInstanceMap = (Map<String, SurveyInstance>) session
                .getAttribute(SURVEY_INSTANCES);
        SurveyInstance si = surveyInstanceMap.get(country);

        List<Long> notAcceptedQuestions = new ArrayList<Long>();
        boolean accepted = true;

        Set<Question> questions = su.getQuestions();
        if(questions == null || questions.isEmpty()){
            StatusUtils.addReviewerToReviewerSubmit(su, si.getStatus());
            // submit the Survey
            FlashAttributesHandler.addFlashAttribute(session, "error", "revsubmit.error", 10000, null, null);
            return "redirect:/surveylist/0";
        }
        for (Question el : questions) {

            long qid = el.getId();
            if (!feedbackService.checkQuestionFeedbackStatus(su, si, qid)) {
                
                notAcceptedQuestions.add(qid);
                accepted = false;
            }
        }
        Long[] qArray = (Long[]) notAcceptedQuestions.toArray(new Long[notAcceptedQuestions.size()]);
        Arrays.sort(qArray);
        
        if (accepted) {
            Status s = si.getStatus();
            s.setCountry(si.getCountry().getIso3());
            boolean outcome = StatusUtils.addReviewerToReviewerSubmit(su, s);
            if(!outcome){
                FlashAttributesHandler.addFlashAttribute(session, "error", "revsubmit.doublesubmit", 10000, null, null);
                return "redirect:/surveylist/0";
            }
            
            //Calculate the revision coverage
            List<String> reviewers = StatusUtils.getReviewerSubmit(s);
            ReviewerUtils ru = new ReviewerUtils(catalog, userService);
            s.setCoverage(ru.getSurveyCoverage(reviewers));
            
            try {
                surveyService.changeStatus(s);
            } catch (BadRequestServiceEx e) {
                LOGGER.error(e.getMessage(), e);
            } catch (NotFoundServiceEx e) {
                LOGGER.error(e.getMessage(), e);
            }
            
            // submit the Survey
            FlashAttributesHandler.addFlashAttribute(session, "success", "revsubmit.ok", 10000, null, null);
            return "redirect:/surveylist/0";
        }
        
        FlashAttributesHandler.addFlashAttribute(session, "warning", "revsubmit.ko", 10000, null, Arrays.toString(qArray));
        return "redirect:/surveylist/0";
    }
}
