package sg.edu.nus.laps.me;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
    MeController handles user/employee-facing pages (Authenticated users only)

                    CONTROLLER SCOPE
    ------------------------------------------------
    CORE:
    GET /me OR / - Dashboard: show recent leave requests and leave balances

    OPTIONAL:
    GET  /me/profile 		- Display employee profile
    POST /me/profile/edit 	- Process employee profile edit request
    GET  /me/notifications 	- Display notifications list
*/
@RequestMapping(path={"/", "/me"})
@Controller
public class MeController {
    
}
