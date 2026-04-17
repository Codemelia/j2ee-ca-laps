document.addEventListener('DOMContentLoaded', function() {
	const leaveTypeSelect = document.getElementById('leaveType');
	const proofStar = document.getElementById('proof-star');
	
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

});