document.addEventListener('DOMContentLoaded', function() {
    const leaveTypeSelect = document.getElementById('leaveType');
    const proofStar = document.getElementById('proof-star');
    const compToggles = document.querySelectorAll(".compensation-toggle");

    function handleLeaveTypeChange() {
        const selectedOption = leaveTypeSelect.options[leaveTypeSelect.selectedIndex];
        const selectedText = selectedOption.text.trim();
        const selectedValue = leaveTypeSelect.value;

        // 1. Toggle Medical Proof Star
        proofStar.style.display = (selectedText === 'Medical') ? 'inline' : 'none';

        // 2. Toggle Compensation AM/PM Fields
        // Using "3" based on ID for Compensation
        const isCompensation = (selectedValue === "3");

        compToggles.forEach(el => {
            if (isCompensation) {
                el.style.display = "inline-block";
                el.style.opacity = "1"; // For a slight fade effect
            } else {
                el.style.display = "none";

                // CRITICAL: Reset values if switching away from Compensation
                // This ensures Annual/Medical defaults to Full Day (AM to PM)
                if (el.name.includes("from")) el.value = "AM";
                if (el.name.includes("to")) el.value = "PM";
            }
        });
    }

    if (leaveTypeSelect) {
        // Run once on load for Edit/Draft modes
        handleLeaveTypeChange();

        // Listener for user interaction
        leaveTypeSelect.addEventListener('change', handleLeaveTypeChange);
    }
});