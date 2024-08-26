document.getElementById("phone-app").addEventListener("click", function () {
  showAppContent("Phone App");
});

document.getElementById("email-app").addEventListener("click", function () {
  showAppContent("Email App");
});

document.getElementById("browser-app").addEventListener("click", function () {
  showAppContent("Browser App");
});

document.getElementById("camera-app").addEventListener("click", function () {
  showAppContent("Camera App");
});

document.getElementById("music-app").addEventListener("click", function () {
  showAppContent("Music App");
});

document.getElementById("calendar-app").addEventListener("click", function () {
  showAppContent("Calendar App");
});

function showAppContent(appName) {
  const appContent = document.getElementById("app-content");
  appContent.textContent = appName;
  appContent.style.display = "flex";

  // Hide the app content after 3 seconds
  setTimeout(() => {
    appContent.style.display = "none";
  }, 3000);
}
