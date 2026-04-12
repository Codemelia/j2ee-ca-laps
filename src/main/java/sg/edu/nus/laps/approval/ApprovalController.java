package sg.edu.nus.laps.approval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    ApprovalController handles manager-facing pages

                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /manager/subordinates/{employeeId}/leaves
        - Display leave history for a subordinate
    GET /manager/team-leaves
        - Display all team leave requests pending/reviewed
    GET /manager/team-leaves/{leaveId}
        - Display information of specific leave request
    POST /manager/team-leaves/{leaveId}/approve
        - Process leave request approval
    POST /manager/team-leaves/{leaveId}/reject
        - Process leave request rejection
*/
@RequestMapping("/manager")
@Controller
public class ApprovalController {
    
}
