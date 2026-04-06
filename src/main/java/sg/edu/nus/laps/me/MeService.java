package sg.edu.nus.laps.me;

import org.springframework.stereotype.Service;

/*
    Me Service handles user/employee-facing functions (Authenticated users only)
    
                    SERVICE SCOPE
    ------------------------------------------------
    -- READ --
    getDashboardData(employeeId)        - Retrieve recent leave requests and leave balances for dashboard
    getProfile(employeeId)              - Retrieve employee profile details
    getNotifications(employeeId)        - Retrieve notifications for current employee

    -- UPDATE --
    updateProfile(employeeId, profile)  - Update employee profile details
*/
@Service
public class MeService {

}
