package sg.edu.nus.laps.leave;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import sg.edu.nus.laps.auth.security.AuthUserDetails;
import sg.edu.nus.laps.leave.model.LeaveApplication;

/*
    LeaveController handles employee's OWN leaves
    
                    CONTROLLER SCOPE
    ------------------------------------------------
    GET /leaves
        - Display list of leaves for current employee
    GET /leaves/new
        - Display apply-for-leave form
    POST /leaves
        - Process leave application request
    GET /leaves/{id}
        - Display specific leave information
    GET /leaves/{id}/edit
        - Display leave info and populate leave edit form
    POST /leaves/{id}
        - Process leave update request
    POST /leaves/{id}/cancel
        - Process leave cancel request
*/
@RequestMapping("/leaves")
@Controller
public class LeaveController {

    private final LeaveAccessService lacService;
    private final LeaveService lService;

    public LeaveController(LeaveAccessService lacService, LeaveService lService) {
        this.lacService = lacService;
        this.lService = lService;
    }

    // TEST leave-details.html - DELETE when updated
    @GetMapping("/{id}")
    public String showLeaves(@AuthenticationPrincipal AuthUserDetails user,
        @PathVariable Long id, Model model) {

        if (id != null && lService.existsByLeaveId(id)) {
            LeaveApplication leaveApp = lService.findLeaveById(id).get();

            if (!lacService.canViewLeave(user, leaveApp)) {
                return "error/forbidden";
            }

            boolean isSelf = lacService.isSelf(user, leaveApp);
            String managerName = lacService.getManagerName(leaveApp.getEmployee());

            model.addAttribute("isSelf", isSelf);
            model.addAttribute("managerName", managerName);
            model.addAttribute("leaveApplication", leaveApp);
        }

        return "leave/leave-details.html";
    }

}
