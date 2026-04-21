document.addEventListener("DOMContentLoaded", function () {

    // On page load, retrieve success message
    const successMsg = sessionStorage.getItem("successMsg");
    if (successMsg) {
        sessionStorage.removeItem("successMsg"); // Refresh

        // Retrieve successMsg and insert in inner text
        // Remove display: none;
        const successAlert = document.getElementById("successMsg");
        if (successAlert) {
            successAlert.innerText = successMsg;
            successAlert.classList.remove("d-none");
        }
    }

    const form = document.getElementById("claimSubmitForm");
    if (!form) return;

    const fieldIds = ["workedDate", "claimedDays"];

    // Functs to clear errors on load/user input
    function clearFieldError(field) {
        const elem = document.getElementById(field + "Error");
        if (elem) { elem.innerText = ""; }
    }

    function clearAllFieldErrors() { fieldIds.forEach(clearFieldError); }

    // Modal (bottom) error
    function setModalError(message) {
        const modalError = document.getElementById("modalError");
        if (!modalError) return;
        modalError.innerText = message || "";
        if (message) {
            modalError.classList.remove("d-none"); //displays error on id modalError
        } else {
            modalError.classList.add("d-none"); // else display: none
        }
    }

    // for each field, add event listener to user input
    fieldIds.forEach((field) => {
        const input = form[field];
        if (!input) return;

        input.addEventListener("input", function () {
            clearFieldError(field); // On type, clear field error
            setModalError(""); // reset error
        });
    });

    // listen to submit event
    form.addEventListener("submit", async function (event) {
        event.preventDefault(); // handle ourselves

        clearAllFieldErrors(); // on submit, reset errors
        setModalError("");

        // Retrieve form data
        const formData = new FormData(form);

        // Must pass csrf token in on POST request
        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;

        // post submission to /claims/submit
        try {
            const resp = await fetch("/claims/submit", {
                method: "POST",
                headers: {
                    "X-Requested-With": "XMLHttpRequest",
                    [header]: token
                },
                body: formData
            });

            // Get response as JSON
            const result = await resp.json();

            // if response is OK 200, redirect to claims
            // set success message to session storage for display
            if (resp.ok) {
                sessionStorage.setItem("successMsg", result.message || "Claim submitted successfully");
                window.location.href = "/claims";
                return;
            }

            // if have validation errors, map as fieldError
            if (result.errors) {
                for (const field in result.errors) {
                    const elem = document.getElementById(field + "Error");
                    if (elem) { elem.innerText = result.errors[field]; }
                }
            }

            // Else if modal (generic) error, display below fields
            if (result.message) {
                setModalError(result.message);
            }
        } catch (error) {
            setModalError("Claim submission failed on unexpected error. Please try again.");
        }
    });
});
