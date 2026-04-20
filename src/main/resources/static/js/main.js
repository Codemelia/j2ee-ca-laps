// Functions for header fragment - applies to all pages
function toggleDropdown(event) {
    event.stopPropagation(); // Don't immediately close
    document.getElementById("dropdownMenu") // Find element dropdownMenu
        .classList.toggle("show"); // Retrieve element content and toggle
}

// Close drop down on click elsewhere
window.addEventListener("click", function() {
    document.getElementById("dropdownMenu")
        .classList.remove("show");
})

// Common initialiser for all pages since each page only has one table
document.addEventListener('DOMContentLoaded', function() {

    // GUARD CHECK
    if (typeof $ === "undefined" || !$.fn.dataTable) {
        return; // Exit if jQuery / DataTables is not loaded
    }

    document.querySelectorAll(".data-table") // Retrieve table by class via query selector
        .forEach(function(table) { // Loop through each element in table
            $(table).DataTable({ // Initialise DataTables on each element
            paging: true, // Enable pagination / searching / ordering rules
            searching: true,
            ordering: true,
            pageLength: 10 // Set default page length (rows) to 10
        })
    })

});
