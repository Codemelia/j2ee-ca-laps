package sg.edu.nus.laps.approval;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sg.edu.nus.laps.leave.model.LeaveApplication;
import sg.edu.nus.laps.security.AuthUserDetails;

@RequestMapping("/manager")
@Controller
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    /**
     * 对应 HTML 的 "Team Leave Applications" 页面
     */
    @GetMapping("/team-leaves")
    public String viewTeamLeaves(@AuthenticationPrincipal AuthUserDetails user, Model model) {
        // 从 Security Context 获取当前经理 ID
        List<LeaveApplication> pendingList = approvalService.getPendingRequests(user.getEmployeeId());
        model.addAttribute("leaveList", pendingList);
        return "manager/team-leave-list"; // 确保文件名对应
    }

    /**
     * 处理批准请求 (对应 HTML 里的 form action="/manager/approve")
     */
    @PostMapping("/approve")
    public String approveLeave(@RequestParam("id") Long id) {
        approvalService.approveRequest(id);
        return "redirect:/manager/team-leaves";
    }

    /**
     * 处理拒绝请求 (对应 HTML 里的 form action="/manager/reject")
     */
    @PostMapping("/reject")
    public String rejectLeave(@RequestParam("id") Long id, @RequestParam("comment") String comment) {
        approvalService.rejectRequest(id, comment);
        return "redirect:/manager/team-leaves";
    }

    /**
     * 查看特定下属的请假历史
     */
    @GetMapping("/subordinate/history/{empId}")
    public String viewSubordinateHistory(@PathVariable Long empId, Model model) {
        model.addAttribute("history", approvalService.getSubordinateHistory(empId));
        return "manager/subordinate-history";
    }
    
    /**
     * 查看单条详情 (对应 HTML 里的 /manager/leave/{id})
     */
    @GetMapping("/leave/{id}")
    public String viewLeaveDetails(@PathVariable Long id, Model model) {
        approvalService.findLeaveById(id).ifPresent(l -> model.addAttribute("leaveApplication", l));
        // 复用详情页，但通过 isSelf=false 控制显示经理审批按钮
        model.addAttribute("isSelf", false); 
        return "leave/leave-details";
    }
}
