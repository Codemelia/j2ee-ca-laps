document.addEventListener('DOMContentLoaded', function() {
	const leaveTypeSelect = document.getElementById('leaveType');
	const proofStar = document.getElementById('proof-star');
	const compToggles = document.querySelectorAll(".compensation-toggle");
	
	function toggleproofStar() {
		const selectedText = leaveTypeSelect.options[leaveTypeSelect.selectedIndex].text;
		
		
					if(selectedText.trim() === 'Medical') {
						proofStar.style.display='inline';
					}else{
						proofStar.style.display='none';
					}
		           
	}

if(leaveTypeSelect) {
	toggleproofStar();
	
	leaveTypeSelect.addEventListener('change',toggleproofStar);
}

	function toggleCompensationFields() {
		// "3" is the ID for Compensation Leave
		if (leaveTypeSelect.value === "3") {
			compToggles.forEach(el => el.style.display = "inline-block");
		} else {
			compToggles.forEach(el => el.style.display = "none");
		}
	}
	
	// Run on change
	leaveTypeSelect.addEventListener("change", toggleCompensationFields);

	// Run on page load (important for Edit mode/Drafts)
	toggleCompensationFields();

});