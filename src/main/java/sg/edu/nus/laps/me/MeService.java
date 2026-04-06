package sg.edu.nus.laps.me;

import org.springframework.stereotype.Service;

/*
    Me Service handles user/employee-facing functions (Authenticated users only)
    
                    SERVICE SCOPE
    ------------------------------------------------
    -- READ --
    getDashboardData(employeeId)        - Retrieve recent leave requests and leave balances by Employee ID
    getProfile(employeeId)              - Retrieve employee profile by Employee ID
    getNotifications(employeeId)        - Retrieve notifications by Employee ID

    -- UPDATE --
    updateProfile(employeeId, profile)  - Update employee profile details
*/
@Service
public class MeService {

}
