const rankDropdown = document.getElementById('rank');
const leaveInput = document.getElementById('leave');

rankDropdown.addEventListener('change', function() {
    const selectedRank = rankDropdown.value;

    // Assign min/max according to rank
    switch(selectedRank) {
        case 'NON_EXECUTIVE':
			leaveInput.setAttribute('min', '14.0');
			leaveInput.setAttribute('max', '17.0');
            leaveInput.value = 14.0;
            break;
        case 'PROFESSIONAL':
			leaveInput.setAttribute('min', '18');
			leaveInput.setAttribute('max', '21');
            leaveInput.value = 18.0;
            break;
        default:
            leaveInput.value = ''; 
    }
});