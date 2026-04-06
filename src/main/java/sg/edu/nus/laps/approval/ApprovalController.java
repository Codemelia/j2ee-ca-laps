package sg.edu.nus.laps.approval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    ApprovalController handles manager-facing pages

                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /requests               - Display list of team leave requests
    GET /requests/{id}          - Display information of specific leave request
    POST /requests/{id}/approve - Process leave request approval
    POST /requests/{id}/reject  - Process leave request rejection
*/
@RequestMapping("/requests")
@Controller
public class ApprovalController {
    
}
