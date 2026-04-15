const rankDropdown = document.getElementById('rank');
const leaveInput = document.getElementById('leave');

rankDropdown.addEventListener('change', function() {
    const selectedRank = this.value;

    // Logic to determine leave days
    switch(selectedRank) {
        case 'NON_EXECUTIVE':
            leaveInput.value = 14;
            break;
        case 'PROFESSIONAL':
            leaveInput.value = 18;
            break;
        default:
            leaveInput.value = ''; 
    }
});