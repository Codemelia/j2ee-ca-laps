package sg.edu.nus.laps.approval;

// /**
//  * Enum representing the approval status of a leave application
//  */
// public enum ApprovalStatus {
//     PENDING("Pending"),
//     APPROVED("Approved"),
//     REJECTED("Rejected"),
//     CANCELLED("Cancelled");
    
//     private final String displayName;
    
//     ApprovalStatus(String displayName) {
//         this.displayName = displayName;
//     }
    
//     public String getDisplayName() {
//         return displayName;
//     }
    
//     /**
//      * Get ApprovalStatus from string
//      */
//     public static ApprovalStatus fromString(String status) {
//         try {
//             return ApprovalStatus.valueOf(status.toUpperCase());
//         } catch (IllegalArgumentException e) {
//             return PENDING;
//         }
//     }
// }