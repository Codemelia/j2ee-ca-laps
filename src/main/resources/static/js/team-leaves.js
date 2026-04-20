// Set Leave ID to reject modal
function setLeaveId(leaveId) {
    document.getElementById('leaveId').value = leaveId;
}

/**
* Form validation before submitting
* Listen to click event to export to CSV
*/
document.addEventListener('DOMContentLoaded', function() {
    const rejectForm = document.getElementById('rejectForm');
    if (rejectForm) {
        rejectForm.addEventListener('submit', function(e) {
            const comment = document.getElementById('comment').value.trim();
            if (!comment || comment.length < 5) {
                e.preventDefault();
                alert('Please provide a comment of at least 5 characters.');
                return false;
            }
        });
    }

    const exportButton = document.getElementById('exportCsvBtn');
    if (exportButton) {
        exportButton.addEventListener('click', exportCurrentPageToCsv);
    }
});

/**
* Reset modal when closed
*/
document.addEventListener('DOMContentLoaded', function() {
    const rejectModal = document.getElementById('rejectModal');
    if (rejectModal) {
        rejectModal.addEventListener('hidden.bs.modal', function() {
            document.getElementById('comment').value = '';
            document.getElementById('leaveId').value = '';
        });
    }
});

// Export current filtered table to CSV
function exportCurrentPageToCsv() {

    // Select table by class
    const table = document.querySelector('.data-table');

    // Guard check for jQuery + DataTables
    if (!table || typeof $ === 'undefined' || !$.fn.dataTable) {
        alert('No leave applications to export');
        return;
    }

    const dataTable = $(table).DataTable(); // Get table

    // Retrieve list of IDs from current table results
    const leaveIds = dataTable.rows({ page: 'current' })
        .nodes().toArray()
        .map(function (row) {
            return row.getAttribute('data-leave-id');
        })
        .filter(function (value) {
            return value !== null && value !== '';
        })
        .map(function (value) {
            return Number(value);
        });

    // If no leave ids in current table, don't export
    if (leaveIds.length === 0) {
        alert('No leave applications to export');
        return;
    }
    
    // Promise chaining to handle response
    fetch('/manager/export-csv', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            // Inject CSRF header for Spring Security
            'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
        },
        body: JSON.stringify(leaveIds)
    })

    // Retrieve csv as bytes and return as blob
    .then(function (response) {

        // Handle error
        if (!response.ok) {
            return response.text()
                .then(function (message) {
                    throw new Error(message || 'Report generation failed');
            });
        }

        // If no error
        const dispos = response.headers.get('Content-Disposition');
        
        // Default filename
        let fileName = 'team-leaves-report.csv';

        // Retrieve file attachment
        if (dispos && dispos.includes('filename')) {
            fileName = dispos
                .split('filename=')[1]
                .replaceAll('"','')
                .trim();
        }

        return response.blob()
            .then(function (blob) {
                return { blob, fileName };
        });

    })

    // Create url for blob
    // Trigger a download for CSV file and name by retrieved/default file name
    .then(function ({ blob, fileName }) {
        const dlUrl = window.URL.createObjectURL(blob);
        const link = document.createElement('a');

        link.href = dlUrl;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        link.remove();
        window.URL.revokeObjectURL(dlUrl);
    })

    // Handle errors
    .catch(function (error) {
        alert(error.message);
    });
    
}