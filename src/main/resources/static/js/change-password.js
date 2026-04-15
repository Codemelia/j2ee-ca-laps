document.addEventListener("DOMContentLoaded", function () {

    // Grab form by ID
    const form = document.getElementById("changePasswordForm");

    if (!form) return; // Prevent error

    // Listen to submit event (button click)
    form.addEventListener("submit", async function (event) {

        event.preventDefault; // Handle it ourselves

        // Map form data
        const formData = {
            oldRawPassword: this.oldRawPassword.value,
            newRawPassword: this.newRawPassword.value,
            confirmPassword: this.confirmPassword.value
        }

        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;

        // fetch Controller endpoint response
        // Pass in formData as JSON
        const resp = await fetch("/auth/change-password", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                [header]: token
            },
            body: JSON.stringify(formData)
        });

        // Get result from change request
        const result = await resp.json();

        // Clear previous messages
        document.getElementById("globalError").innerText = "";
        document.getElementById("successMsg").innerText = "";

        // Handle success response
        if (resp.ok) {
            document.getElementById("successMsg")
                .innerText = result.message;
        }

        // Handle field errors
        if (result.errors) {
            for (const field in result.errors) {
                const elem = document.getElementById(field + "Error"); // Find fieldError
                if (elem) {
                    elem.innerText = result.errors[field]; // Insert
                }
            }
            return;
        }

        // Handle anyother errors
        document.getElementById("globalError")
            .innerText = result.message;

    })

});