package sg.edu.nus.laps.approval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    ApprovalController handles manager-facing pages

                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /manager/subordinates/{employeeId}/leaves
        - Display leave history for a subordinate
    GET /manager/leaves
        - Display all team leave requests pending/reviewed
    GET /manager/leaves/{leaveId}
        - Display information of specific leave request
    POST /manager/leaves/{leaveId}/approve
        - Process leave request approval
    POST /manager/leaves/{leaveId}/reject
        - Process leave request rejection
*/
@RequestMapping("/manager")
@Controller
public class ApprovalController {
    
}
