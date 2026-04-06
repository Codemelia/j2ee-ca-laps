package sg.edu.nus.laps.approval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    ApprovalController handles manager-facing pages

                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /requests - Display list of team leave requests
    GET /requests/{id} - Retrieve information of specific leave request
    POST /requests/{id}/approve - Approve leave request
    POST /requests/{id}/reject - Reject leave request
*/
@RequestMapping("/requests")
@Controller
public class ApprovalController {
    
}
