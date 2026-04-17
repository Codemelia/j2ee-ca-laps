document.addEventListener("DOMContentLoaded", function () {

    // Grab form by ID
    const form = document.getElementById("changePasswordForm");

    if (!form) return; // Prevent error

    // Define field ids
    const fieldIds = ["oldRawPassword", "newRawPassword", "confirmPassword"];

    // Clear individual field error
    function clearFieldError(field) {
        const elem = document.getElementById(field + "Error");
        if (elem) { elem.innerText = ""; }
    }

    // Clear all field errors on load of modal
    function clearAllFieldErrors() { fieldIds.forEach(clearFieldError); }

    // While user is typing, clear error on edited fields
    fieldIds.forEach((field) => {
        const input = form[field];
        if (!input) return;

        // add event listener to form fields
        input.addEventListener("input", function () {
            clearFieldError(field);
            document.getElementById("globalError").innerText = "";
            document.getElementById("successMsg").innerText = "";
        })
    })

    
    // Listen to submit event (button click)
    form.addEventListener("submit", async function (event) {

        event.preventDefault(); // Handle it ourselves

        // Map form data
        const formData = {
            oldRawPassword: this.oldRawPassword.value,
            newRawPassword: this.newRawPassword.value,
            confirmPassword: this.confirmPassword.value
        }

        // Must pass csrf token in on PUT request
        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;

        // Send PUT request and fetch Controller endpoint response
        const resp = await fetch("/auth/change-password", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                [header]: token
            },
            body: JSON.stringify(formData)
        });

        // Get result from change request
        const result = await resp.json();

        // Clear previous messages
        clearAllFieldErrors();
        document.getElementById("globalError").innerText = "";
        document.getElementById("successMsg").innerText = "";

        // Handle success response
        if (resp.ok) {
            document.getElementById("successMsg")
                .innerText = result.message;
        }

        // Handle field errors
        else if (result.errors) {
            for (const field in result.errors) {
                const elem = document.getElementById(field + "Error"); // Find fieldError
                if (elem) {
                    elem.innerText = result.errors[field]; // Insert
                }
            }
            return;
        }

        // Handle any other errors
        else if (!resp.ok) {
            document.getElementById("globalError")
                .innerText = result.message;
        }

    })

});