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
package it.geosolutions.fra2015.mvc.controller.utils;

import it.geosolutions.fra2015.server.model.survey.Status;
/**
 * Utility Class for status 
 * @author Lorenzo Natali
 *
 */
public class StatusUtils {
    public static final String EMPTY="empty";
    public static final String IN_PROGRESS="inprogress";
    public static final String COMPILED="compiled";
    public static final String PENDING_FIX="pendingfix";
    public static final String UNDER_REVIEW="underreview";
    public static final String REVIEW_COMPLETED="reviewcompleted";
    public static final String REVIEW_EDITING="reviewediting";
    public static final String COMPLETED="completed";
    public static final String ACCEPTED="accepted";
    
    /**
     * Check if the status is one of the submit allowed
     * @param s the status
     * @return true if the contributor can submit the status, false otherwise
     */
    public static boolean isSubmitAllowed(Status s){
        return isInProgress(s) || isPendingFix(s) || isCompiled(s) || isEmpty(s);
    }
    
    public static boolean isEmpty(Status s){
        if(s!=null){
            return EMPTY.equals(s.getStatus());
        }
        return false; 
    }
    public static boolean isInProgress(Status s){
        if(s!=null){
            return IN_PROGRESS.equals(s.getStatus());
        }
        return false; 
    } 
    public static boolean isCompiled(Status s){
        if(s!=null){
            return COMPILED.equals(s.getStatus());
        }
        return false; 
    } 
    public static boolean isPendingFix(Status s){
        if(s!=null){
            return PENDING_FIX.equals(s.getStatus());
        }
        return false; 
    } 
    public static boolean isUnderReview(Status s){
        if(s!=null){
            return UNDER_REVIEW.equals(s.getStatus());
        }
        return false; 
    } 
    public static boolean isReviewCompleted(Status s){
        if(s!=null){
            return REVIEW_COMPLETED.equals(s.getStatus());
        }
        return false; 
    }   
    
    public static boolean isReviewEditing(Status s){
        if(s!=null){
            return REVIEW_EDITING.equals(s.getStatus());
        }
        return false; 
    }public static boolean isCompleted(Status s){
        if(s!=null){
            return COMPLETED.equals(s.getStatus());
        }
        return false; 
    }public static boolean isAccepted(Status s){
        if(s!=null){
            return ACCEPTED.equals(s.getStatus());
        }
        return false; 
    }
    
    
}
