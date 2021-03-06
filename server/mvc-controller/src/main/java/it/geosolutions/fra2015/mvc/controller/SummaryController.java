/*
 *  fra2015
 *  https://github.com/geosolutions-it/fra2015
 *  Copyright (C) 2007-2013 GeoSolutions S.A.S.
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

import static it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.SESSION_USER;
import it.geosolutions.fra2015.entrypoint.model.CountryValues;
import it.geosolutions.fra2015.mvc.controller.utils.ControllerServices;
import it.geosolutions.fra2015.mvc.controller.utils.ControllerServices.Profile;
import it.geosolutions.fra2015.mvc.controller.utils.StatusUtils;
import it.geosolutions.fra2015.server.model.survey.Country;
import it.geosolutions.fra2015.server.model.survey.Question;
import it.geosolutions.fra2015.server.model.survey.Status;
import it.geosolutions.fra2015.server.model.user.User;
import it.geosolutions.fra2015.services.SurveyService;
import it.geosolutions.fra2015.services.exception.InternalErrorServiceEx;
import it.geosolutions.fra2015.services.utils.UserUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Provides a summary for the variables
 * @author Lorenzo Natali
 * 
 */
@Controller
public class SummaryController {

    @Autowired
    private ControllerServices utils;
    
    @Autowired
    private SurveyService surveyService;
    
    /**
     * Provides a summary for the contributor.
     * The country is taken from the user
     * @param model
     * @param session
     * @return
     * @throws InternalErrorServiceEx
     */
    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public String handleGet(Model model, HttpSession session) throws InternalErrorServiceEx {

        model.addAttribute("context", "summary");

        User su = (User) session.getAttribute(SESSION_USER);
        String iso3 = UserUtil.getSingleIso3(su);

        // Set the parameter operationWR, the domain is "WRITE" "READ"
        model.addAttribute("profile", ControllerServices.Profile.PRINT.toString());
        utils.prepareHTTPRequest(model, null, utils.retrieveValues(null, iso3), false);

        return "index";

    }
    /**
     * Provides summary for reviewers and editor
     * @param model
     * @param session
     * @param country the country iso3
     * @return
     * @throws InternalErrorServiceEx
     */
    @RequestMapping(value = "/summary/{country}", method = RequestMethod.GET)
    public String handleGet(Model model, HttpSession session,@PathVariable(value = "country") String country) throws InternalErrorServiceEx {
        model.addAttribute("context", "summary");
        model.addAttribute("country", country);
        User su = (User) session.getAttribute(SESSION_USER);
        String iso3 = country;
        Status status = surveyService.getStatus(iso3);
        String statusLocale= StatusUtils.getStatusLocaleCode(status);

        model.addAttribute("statuscode",statusLocale);
        String targetPage = "";
        if(su==null){
           return "redirect:/"; 
        }
        if(su.getRole().equalsIgnoreCase(Profile.EDITOR.toString())){
            targetPage = "editor";
        }
        else if(su.getRole().equalsIgnoreCase(Profile.REVIEWER.toString())){
            targetPage = "reviewer";
        }
        else{
            targetPage= "redirect:/";
        }
        

        // Set the parameter operationWR, the domain is "WRITE" "READ"
        model.addAttribute("profile", ControllerServices.Profile.PRINT.toString());
        utils.prepareHTTPRequest(model, null, utils.retrieveValues(null, iso3), false);
        model.addAttribute("allowedQuestions",ControllerServices.getAllowedQuestions(su));
        model.addAttribute("sessionUser",su);
        return targetPage;

    }
    
    /**
     * Provides summary for reviewers and editor
     * @param model
     * @param session
     * @param country the country iso3
     * @return
     * @throws InternalErrorServiceEx
     */
    @RequestMapping(value = "/fullSummary", method = RequestMethod.GET)
    public String handleGetAllCountries(Model model, HttpSession session) throws InternalErrorServiceEx {

      User su = (User) session.getAttribute(SESSION_USER);
      String targetPage = "";
      if(su==null){
         return "redirect:/"; 
      }
      if(su.getRole().equalsIgnoreCase(Profile.EDITOR.toString())){
          targetPage = "editor";
      }
      else if(su.getRole().equalsIgnoreCase(Profile.REVIEWER.toString())){
          targetPage = "reviewer";
      }
      else{
          targetPage= "redirect:/";
      }
        
        long start = System.currentTimeMillis();
        
        List<Country> countriesList = new ArrayList<Country>(su.getCountriesSet());
        Map<String,CountryValues> map = new java.util.HashMap<String, CountryValues>();
        CountryValues cv = null;
        List<String> sortedCountryNames = new ArrayList<String>();
        int i = 0;
        for(Country country : countriesList){
            cv = utils.retrieveValues(null, country.getIso3());
            map.put(country.getIso3(), cv);
            sortedCountryNames.add(country.getName() + ";" + country.getIso3());
        }
        model.addAttribute("allowedQuestions",ControllerServices.getAllowedQuestions(su));
        Map<String, Map<String, String>> mapToBeDisplayed = utils.prepareCountryValuesMaps(map);
        session.setAttribute("countryValuesToDisplay", mapToBeDisplayed);
        Collections.sort(sortedCountryNames);
        session.setAttribute("sortedCountryNames", sortedCountryNames);
        long end = System.currentTimeMillis();
        
        System.out.println("I have taken '" + (end-start)/1000 + "' seconds to finish. The map built has '" + mapToBeDisplayed.size() + "' elements");
        model.addAttribute("context", "totalSummary");
        return targetPage;
    }    
    
}
