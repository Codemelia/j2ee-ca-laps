document.addEventListener('DOMContentLoaded', function() {
	
	const rankDropdown = document.getElementById('rank');
	const leaveInput = document.getElementById('annualLeave');
	
	function updateLeaveValue() {
		const selectedRank = rankDropdown.value;
		
		switch(selectedRank) {
		        case 'NON_EXECUTIVE':
					leaveInput.setAttribute('min', '14.0');
					leaveInput.setAttribute('max', '17.0');
					if(leaveInput.value == '' || leaveInput.value > 17.0) {
						leaveInput.value = 14;
					}
		            break;
		        case 'PROFESSIONAL':
					leaveInput.setAttribute('min', '18');
					leaveInput.setAttribute('max', '21');
					if(leaveInput.value == '' || leaveInput.value < 18.0) {
						leaveInput.value = 18;
					};
		            break;
		        default:
		            leaveInput.value = ''; 
					break;
	}
}

if(rankDropdown) {
	rankDropdown.addEventListener('change', updateLeaveValue);
	
	if(rankDropdown.value !== '') {
		updateLeaveValue();
	}
}

});