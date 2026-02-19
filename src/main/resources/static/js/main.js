// Show selected file name when user picks a file
document.getElementById("compress-input").addEventListener("change", function () {
    document.getElementById("compress-name").textContent = this.files[0]?.name || "No file selected";
});

document.getElementById("decompress-input").addEventListener("change", function () {
    document.getElementById("decompress-name").textContent = this.files[0]?.name || "No file selected";
});

// Drag and drop support
setupDragDrop("compress-drop", "compress-input", "compress-name");
setupDragDrop("decompress-drop", "decompress-input", "decompress-name");

function setupDragDrop(zoneId, inputId, nameId) {
    var zone = document.getElementById(zoneId);
    var input = document.getElementById(inputId);
    var nameEl = document.getElementById(nameId);
    if (!zone || !input) return;

    zone.addEventListener("dragover", function(e) {
        e.preventDefault();
        zone.classList.add("dragover");
    });
    zone.addEventListener("dragleave", function() {
        zone.classList.remove("dragover");
    });
    zone.addEventListener("drop", function(e) {
        e.preventDefault();
        zone.classList.remove("dragover");
        var files = e.dataTransfer.files;
        if (files.length > 0) {
            var dt = new DataTransfer();
            dt.items.add(files[0]);
            input.files = dt.files;
            nameEl.textContent = files[0].name;
        }
    });
}

// Show loading state when form is submitted
document.querySelectorAll("form").forEach(function(form) {
    form.addEventListener("submit", function() {
        var btn = this.querySelector("button[type=submit]");
        if (btn) {
            btn.disabled = true;
            btn.textContent = "Processing... please wait";
        }
    });
});

// Animate the ratio bar on result page
window.addEventListener("load", function() {
    var fill = document.querySelector(".ratio-fill");
    if (fill) {
        var targetWidth = fill.getAttribute("data-width");
        if (targetWidth) {
            fill.style.width = "0%";
            setTimeout(function() {
                fill.style.width = targetWidth + "%";
            }, 300);
        }
    }
});
