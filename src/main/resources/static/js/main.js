// Functions for header fragment - applies to all pages
function toggleDropdown(event) {
    event.stopPropagation(); // Don't immediately close
    document.getElementById("dropdownMenu") // Find element dropdownMenu
        .classList.toggle("show"); // Retrieve element content as classList and toggle
}

// Close drop down on click elsewhere
window.addEventListener("click", function() {
    document.getElementById("dropdownMenu")
        .classList.remove("show"); // Remove dropdown menu
})

// Initialize datatables on employeeTable if it exists
document.addEventListener('DOMContentLoaded', function() {
    var table = document.getElementById('employeeTable');
    if (table && typeof $ !== 'undefined' && $.fn.dataTable) {
        $('#employeeTable').DataTable({
            paging: true,
            searching: true,
            ordering: true,
            pageLength: 10
        });
    }
});

// Initialize datatables on myLeaveTable if it exists
document.addEventListener('DOMContentLoaded', function() {
    var table = document.getElementById('myLeaveTable');
    if (table && typeof $ !== 'undefined' && $.fn.dataTable) {
        $('#employeeTable').DataTable({
            paging: true,
            searching: true,
            ordering: true,
            pageLength: 10
        });
    }
});

// Initialize datatables on employeeTable if it exists
document.addEventListener('DOMContentLoaded', function() {
    var table = document.getElementById('teamLeaveTable');
    if (table && typeof $ !== 'undefined' && $.fn.dataTable) {
        $('#employeeTable').DataTable({
            paging: true,
            searching: true,
            ordering: true,
            pageLength: 10
        });
    }
});