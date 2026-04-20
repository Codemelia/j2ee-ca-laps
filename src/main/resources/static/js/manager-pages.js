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
// Applies to both leaves and claims within the team
function exportCurrentPageToCsv() {

    // Select table by class
    const table = document.querySelector('.data-table');
    const exportButton = document.getElementById('exportCsvBtn');

    // Guard check for jQuery + DataTables
    if (!table || typeof $ === 'undefined' || !$.fn.dataTable) {
        alert('No records to export');
        return;
    }

    const dataTable = $(table).DataTable(); // Get table

    // Configure according to dataset (leaves vs claims); default to leaves
    const rowIdAttr = (exportButton && exportButton.dataset.rowIdAttr) || 'data-leave-id';
    const exportEndpoint = (exportButton && exportButton.dataset.exportEndpoint) || '/manager/team-leaves/export-csv';
    const emptyMessage = (exportButton && exportButton.dataset.emptyMessage) || 'No leave applications to export';
    const defaultFileName = (exportButton && exportButton.dataset.defaultFileName) || 'leaves-report.csv';

    // Retrieve list of IDs from current table results
    const itemIds = dataTable.rows({ page: 'current' })
        .nodes().toArray()
        .map(function (row) {
            return row.getAttribute(rowIdAttr);
        })
        .filter(function (value) {
            return value !== null && value !== '';
        })
        .map(function (value) {
            return Number(value);
        })
        .filter(function (value) {
            return Number.isFinite(value);
        });

    // If no leave ids in current table, don't export
    if (itemIds.length === 0) {
        alert(emptyMessage);
        return;
    }
    
    // Promise chaining to handle response
    fetch(exportEndpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            // Inject CSRF header for Spring Security
            'X-CSRF-TOKEN': document.querySelector('input[name="_csrf"]').value
        },
        body: JSON.stringify(itemIds)
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
        let fileName = defaultFileName;

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