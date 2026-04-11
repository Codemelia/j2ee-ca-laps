package sg.edu.nus.laps.approval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    ApprovalController handles manager-facing pages

                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /team               - Display list of team members
    GET /team/{emploeeId}/leaves
    GET /team/leaves        - Display all team members leaves
    GET /team/leaves/{leaveId}          - Display information of specific leave request
    POST /team/leaves/{leaveId}/approve - Process leave request approval
    POST /team/leaves/{leaveId}/reject  - Process leave request rejection
*/
@RequestMapping("/team")
@Controller
public class ApprovalController {
    
}
