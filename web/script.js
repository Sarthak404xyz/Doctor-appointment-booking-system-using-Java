const form = document.getElementById("bookingForm");
const doctorSelect = document.getElementById("doctor");
const message = document.getElementById("message");
const dateInput = document.getElementById("date");

window.onload = function () {
    const today = new Date().toISOString().split("T")[0];

    dateInput.min = today;
    dateInput.value = today;

    loadDoctors();
    loadAppointments();
};

function loadDoctors() {
    fetch("/doctors")
        .then(response => response.json())
        .then(doctors => {

            doctorSelect.innerHTML =
                '<option value="">Choose a doctor</option>';

            doctors.forEach(doctor => {

                const option = document.createElement("option");

                option.value = doctor.id;

                option.textContent =
                    doctor.name + " - " +
                    doctor.specialization + " (" +
                    doctor.time + ")";

                doctorSelect.appendChild(option);
            });
        });
}

form.addEventListener("submit", function (event) {

    event.preventDefault();

    const data = new URLSearchParams(
        new FormData(form)
    );

    fetch("/book", {
        method: "POST",
        body: data
    })
    .then(response => response.json())
    .then(result => {

        if (result.error) {
            showMessage(result.error, true);
        } else {
            showMessage(
                "Appointment booked successfully!",
                false
            );

            form.reset();

            dateInput.min =
                new Date().toISOString().split("T")[0];

            loadAppointments();
        }
    })
    .catch(() => {
        showMessage(
            "Something went wrong. Please try again.",
            true
        );
    });
});

function loadAppointments() {

    fetch("/appointments")
        .then(response => response.json())
        .then(appointments => {

            const table =
                document.getElementById("appointmentTable");

            if (appointments.length === 0) {
                table.innerHTML =
                    '<tr><td colspan="8">No appointments yet.</td></tr>';
                return;
            }

            table.innerHTML = "";

            appointments.forEach(a => {

                const row = document.createElement("tr");

                row.innerHTML =
                    "<td>" + a.id + "</td>" +
                    "<td>" + a.patient + "</td>" +
                    "<td>" + a.doctor + "</td>" +
                    "<td>" + a.specialization + "</td>" +
                    "<td>" + a.date + "</td>" +
                    "<td>" + a.time + "</td>" +
                    "<td>" + a.status + "</td>" +
                    "<td>" +
                    (a.status === "Booked"
                        ? '<button class="cancel" onclick="cancelAppointment(' +
                          a.id + ')">Cancel</button>'
                        : "") +
                    "</td>";

                table.appendChild(row);
            });
        });
}

function cancelAppointment(id) {

    const data = new URLSearchParams();

    data.append("id", id);

    fetch("/cancel", {
        method: "POST",
        body: data
    })
    .then(() => {
        showMessage(
            "Appointment cancelled.",
            false
        );

        loadAppointments();
    });
}

function showMessage(text, error) {

    message.textContent = text;

    message.className =
        error ? "error" : "success";

    setTimeout(() => {
        message.className = "";
        message.textContent = "";
    }, 4000);
}
