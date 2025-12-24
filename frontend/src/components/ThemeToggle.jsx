import React, { useState, useEffect } from "react";

const ThemeToggle = () => {
  const [theme, setTheme] = useState(localStorage.getItem("theme") || "light");

  useEffect(() => {
    // Aplicar el tema al elemento <html>
    document.documentElement.setAttribute("data-bs-theme", theme);
    localStorage.setItem("theme", theme);
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prevTheme) => (prevTheme === "light" ? "dark" : "light"));
  };

  return (
    <button
      onClick={toggleTheme}
      className={`btn btn-${theme === "light" ? "dark" : "light"} position-fixed top-0 end-0 m-3 z-3 shadow`}
      title={theme === "light" ? "Activar Modo Oscuro" : "Activar Modo Claro"}
      style={{ borderRadius: "50%", width: "40px", height: "40px", display: "flex", alignItems: "center", justifyContent: "center" }}
    >
      {theme === "light" ? "🌙" : "☀️"}
    </button>
  );
};

export default ThemeToggle;
