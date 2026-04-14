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