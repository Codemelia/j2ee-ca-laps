package sg.edu.nus.laps.leave;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    LeaveController handles employee's OWN leaves
    
                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /leaves              - Retrieve list of leaves for current employee
    GET /leaves/apply        - Apply for leave
    POST /leaves             - Process leave request
    GET /leaves/{id}         - Retrieve specific leave information
    GET /leaves/{id}/edit    - Retrieve leave info and populate leave edit form
    POST /leaves/{id}/update - Process leave edit request
    POST /leaves/{id}/cancel - Process leave cancel request
*/
@RequestMapping("/leaves")
@Controller
public class LeaveController {

}
